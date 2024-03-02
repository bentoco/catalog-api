package com.bentoco.productcatalog.repositories;

import com.bentoco.productcatalog.model.Product;

import java.util.UUID;

public interface ProductRepository {
    void upsert(Product product);
    void delete(UUID productId);
}
