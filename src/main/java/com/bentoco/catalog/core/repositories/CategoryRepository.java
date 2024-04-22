package com.bentoco.catalog.core.repositories;

import com.bentoco.catalog.core.model.Category;
import com.bentoco.catalog.model.CategoryImmutableBeanItem;

public interface CategoryRepository {

    String insert(CategoryImmutableBeanItem category);

    void update(CategoryImmutableBeanItem category);

    void delete(String categoryId, String ownerId);
}
