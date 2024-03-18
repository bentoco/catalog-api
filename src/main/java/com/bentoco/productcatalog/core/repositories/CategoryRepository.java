package com.bentoco.productcatalog.core.repositories;

import com.bentoco.productcatalog.core.model.Category;

import java.util.UUID;

public interface CategoryRepository {

    UUID insert(Category category);

    void update(Category category);

    void delete(UUID categoryId);
}
