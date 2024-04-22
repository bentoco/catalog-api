package com.bentoco.catalog.core.repositories;

import com.bentoco.catalog.model.ProductImmutableBeanItem;

public interface ProductRepository {

    String insert(ProductImmutableBeanItem product);

    void update(ProductImmutableBeanItem product);

    void delete(String productId, String ownerId);

    Boolean hasAnyRelationship(String categoryId);
}
