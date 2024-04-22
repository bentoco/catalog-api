package com.bentoco.catalog.controller;

import com.bentoco.catalog.configurations.interfaces.AccessControl;
import com.bentoco.catalog.configurations.middlewares.RequestContext;
import com.bentoco.catalog.controller.request.CreateCategoryRequest;
import com.bentoco.catalog.controller.request.UpdateCategoryRequest;
import com.bentoco.catalog.core.model.Role;
import com.bentoco.catalog.mappers.CategoryMapper;
import com.bentoco.catalog.model.CategoryImmutableBeanItem;
import com.bentoco.catalog.service.CategoryService;
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

import static com.bentoco.catalog.constants.AwsConstants.PREFIX_CATEGORY;
import static com.bentoco.catalog.constants.AwsConstants.PREFIX_OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

    private final static Logger logger = LogManager.getLogger(CategoryController.class);
    private final static CategoryMapper categoryMapper = CategoryMapper.INSTANCE;

    private final CategoryService categoryService;
    private final RequestContext requestContext;

    @PostMapping
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Category"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> insertCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest, UriComponentsBuilder uriBuilder) {
        logger.info("receive category insert request: {}", createCategoryRequest);
        String ownerId = requestContext.getProfile().ownerId().toString();
        String categoryId = categoryService.insertCategory(
                CategoryImmutableBeanItem.builder()
                        .pk(STR. "\{ PREFIX_OWNER }\{ ownerId }" )
                        .sk(STR. "\{ PREFIX_CATEGORY }\{ UUID.randomUUID() }" )
                        .title(createCategoryRequest.title())
                        .description(createCategoryRequest.description())
                        .build()
        );
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
        String ownerId = requestContext.getProfile().ownerId().toString();
        categoryService.updateCategory(
                CategoryImmutableBeanItem.builder()
                        .pk(STR. "\{ PREFIX_OWNER }\{ ownerId }" )
                        .sk(STR. "\{ PREFIX_CATEGORY }\{ categoryId }" )
                        .title(categoryRequest.title())
                        .description(categoryRequest.description())
                        .build()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{category_id}")
    @AccessControl({Role.ADMIN, Role.OWNER})
    @Operation(tags = {"Category"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "category_id") String categoryId) {
        logger.info("receive delete request to id: {}", categoryId);
        String ownerId = requestContext.getProfile().ownerId().toString();
        categoryService.deleteCategory(categoryId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
