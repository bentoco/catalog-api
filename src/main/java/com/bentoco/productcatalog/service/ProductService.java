package com.bentoco.productcatalog.service;

import com.bentoco.productcatalog.model.Product;
import com.bentoco.productcatalog.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LogManager.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public void upsertProduct(final Product product) {
        logger.info("inserting product: {}", product);
        productRepository.upsert(product);
    }

    public void deleteProduct(final UUID productId) {
        logger.info("deleting product with id: {}", productId);
        productRepository.delete(productId);
    }
}
