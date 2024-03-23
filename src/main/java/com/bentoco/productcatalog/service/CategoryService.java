package com.bentoco.productcatalog.service;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.repositories.CategoryRepository;
import com.bentoco.productcatalog.sqs.CatalogChangePublisher;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryService.class);

    private final RequestContext requestContext;
    private final CategoryRepository categoryRepository;
    private final CatalogChangePublisher catalogChangePublisher;

    public UUID insertCategory(final Category category) {
        logger.info("inserting category: {}", category);
        Owner owner = new Owner(requestContext.getProfile().ownerId());
        category.setOwner(owner);
        UUID categoryId = categoryRepository.insert(category);
        catalogChangePublisher.send(owner.id().toString());
        return categoryId;
    }

    public void updateCategory(final Category category) {
        logger.info("updating category: {}", category);
        categoryRepository.update(category);
    }

    public void deleteCategory(final UUID categoryId) {
        logger.info("deleting category with id: {}", categoryId);
        categoryRepository.delete(categoryId);
    }
}

