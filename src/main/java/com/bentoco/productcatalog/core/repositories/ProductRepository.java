package com.bentoco.productcatalog.core.repositories;

import com.bentoco.productcatalog.core.model.Product;

import java.util.UUID;

public interface ProductRepository {

    UUID insert(Product product);

    void update(Product product);

    void delete(UUID productId);

}
