package com.bentoco.productcatalog.core.repositories;

import com.bentoco.productcatalog.core.model.Product;

import java.util.UUID;

public interface ProductRepository {
    void upsert(Product product);
    void delete(UUID productId);
}
