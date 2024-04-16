package com.bentoco.catalog.core.repositories;

import com.bentoco.catalog.core.model.Category;

public interface CategoryRepository {

    String insert(Category category);

    void update(Category category);

    void delete(String categoryId, String ownerId);
}
