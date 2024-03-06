package com.bentoco.productcatalog.controller;

import com.bentoco.productcatalog.configurations.interfaces.AccessControl;
import com.bentoco.productcatalog.controller.request.ProductRequest;
import com.bentoco.productcatalog.core.model.Role;
import com.bentoco.productcatalog.mappers.ProductMapper;
import com.bentoco.productcatalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {

    private static final Logger logger = LogManager.getLogger(ProductController.class);
    private final ProductService productService;

    @PostMapping
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Product"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> upsertProduct(@Valid @RequestBody ProductRequest productRequest, UriComponentsBuilder uriBuilder) {
        logger.info("receive product request: {}", productRequest);
        UUID productId = productService.upsertProduct(ProductMapper.INSTANCE.toModel(productRequest));

        URI locationUri = uriBuilder.path("/v1/products/{id}").buildAndExpand(productId).toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @DeleteMapping
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Product"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteProduct(@RequestParam(value = "product_id") String productId) {
        logger.info("receive delete request to id: {}", productId);
        productService.deleteProduct(UUID.fromString(productId));
        return ResponseEntity.noContent().build();
    }
}
