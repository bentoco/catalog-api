package com.bentoco.productcatalog.service;

import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;

    public UUID upsertCategory(final Category category) {
        logger.info("inserting category: {}", category);
        return categoryRepository.upsert(category);
    }

    public void deleteCategory(final UUID categoryId) {
        logger.info("deleting category with id: {}", categoryId);
        categoryRepository.delete(categoryId);
    }
}

