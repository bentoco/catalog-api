package com.bentoco.productcatalog.controller;

import com.bentoco.productcatalog.configurations.interfaces.AccessControl;
import com.bentoco.productcatalog.controller.request.CategoryRequest;
import com.bentoco.productcatalog.controller.request.UpdateCategoryRequest;
import com.bentoco.productcatalog.core.model.Role;
import com.bentoco.productcatalog.mappers.CategoryMapper;
import com.bentoco.productcatalog.service.CategoryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

    private final static Logger logger = LogManager.getLogger(CategoryController.class);
    private final static CategoryMapper categoryMapper = CategoryMapper.INSTANCE;

    private final CategoryService categoryService;

    @PostMapping
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Category"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> insertCategory(@Valid @RequestBody CategoryRequest categoryRequest, UriComponentsBuilder uriBuilder) {
        logger.info("receive category insert request: {}", categoryRequest);
        UUID categoryId = categoryService.insertCategory(categoryMapper.toModel(categoryRequest));

        URI locationUri = uriBuilder.path("/v1/categories/{id}").buildAndExpand(categoryId).toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @PatchMapping("/{category_id}")
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Category"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateCategory(
            @PathVariable("category_id") String categoryId,
            @Valid @RequestBody UpdateCategoryRequest categoryRequest
    ) {
        logger.info("receive category update request: {}", categoryRequest);
        categoryService.updateCategory(categoryMapper.toModel(categoryRequest, UUID.fromString(categoryId)));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Category"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCategory(@RequestParam(value = "category_id") String categoryId) {
        logger.info("receive delete request to id: {}", categoryId);
        categoryService.deleteCategory(UUID.fromString(categoryId));
        return ResponseEntity.noContent().build();
    }
}
