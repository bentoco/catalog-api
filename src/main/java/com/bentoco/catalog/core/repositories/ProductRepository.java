package com.bentoco.catalog.core.repositories;

import com.bentoco.catalog.core.model.Product;

public interface ProductRepository {

    String insert(Product product);

    void update(Product product);

    void delete(String productId, String ownerId);

}
