package com.bentoco.catalog.service;

import com.bentoco.catalog.core.repositories.CategoryRepository;
import com.bentoco.catalog.model.CategoryImmutableBeanItem;
import com.bentoco.catalog.sqs.CatalogChangePublisher;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final CatalogChangePublisher catalogChangePublisher;

    public String insertCategory(final CategoryImmutableBeanItem category) {
        logger.info("inserting category: {}", category);
        String categoryId = categoryRepository.insert(category);
        catalogChangePublisher.send(category.getSk());
        return categoryId;
    }

    public void updateCategory(final CategoryImmutableBeanItem category) {
        logger.info("updating category: {}", category);
        categoryRepository.update(category);
    }

    public void deleteCategory(final String categoryId, final String ownerId) {
        logger.info("deleting category with id: {}", categoryId);
        categoryRepository.delete(categoryId, ownerId);
    }
}

