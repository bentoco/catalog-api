package com.bentoco.productcatalog.core.repositories;

import com.bentoco.productcatalog.core.model.Category;

import java.util.UUID;

public interface CategoryRepository {

    UUID upsert(Category category);

    void delete(UUID categoryId);
}
