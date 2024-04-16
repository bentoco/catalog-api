package com.bentoco.catalog.controller;

import com.bentoco.catalog.configurations.interfaces.AccessControl;
import com.bentoco.catalog.configurations.middlewares.RequestContext;
import com.bentoco.catalog.controller.request.ProductRequest;
import com.bentoco.catalog.controller.request.UpdateProductRequest;
import com.bentoco.catalog.core.model.Role;
import com.bentoco.catalog.mappers.ProductMapper;
import com.bentoco.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {

    private final static Logger logger = LogManager.getLogger(ProductController.class);
    private final static ProductMapper productMapper = ProductMapper.INSTANCE;

    private final ProductService productService;
    private final RequestContext requestContext;

    @PostMapping
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Product"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductRequest productRequest, UriComponentsBuilder uriBuilder) {
        logger.info("receive product request: {}", productRequest);
        String ownerId = requestContext.getProfile().ownerId().toString();
        String productId = productService.insertProduct(productMapper.toModel(productRequest, ownerId));

        URI locationUri = uriBuilder.path("/v1/products/{id}").buildAndExpand(productId).toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @PatchMapping("/{product_id}")
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Product"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateProduct(
            @PathVariable("product_id") String productId,
            @Valid @RequestBody UpdateProductRequest updateProductRequest
    ) {
        logger.info("receive product update request: {}", updateProductRequest);
        String ownerId = requestContext.getProfile().ownerId().toString();
        productService.updateProduct(productMapper.toModel(updateProductRequest, productId, ownerId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{product_id}")
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Product"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteProduct(@PathVariable("product_id") String productId) {
        logger.info("receive delete request to id: {}", productId);
        String ownerId = requestContext.getProfile().ownerId().toString();
        productService.deleteProduct(productId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
