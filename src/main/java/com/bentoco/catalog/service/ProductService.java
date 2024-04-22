package com.bentoco.catalog.service;

import com.bentoco.catalog.core.repositories.ProductRepository;
import com.bentoco.catalog.model.ProductImmutableBeanItem;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LogManager.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public String insertProduct(final ProductImmutableBeanItem product) {
        logger.info("inserting product: {}", product);
        return productRepository.insert(product);
    }

    public void updateProduct(final ProductImmutableBeanItem product) {
        logger.info("updating product: {}", product);
        productRepository.update(product);
    }


    public void deleteProduct(final String productId, final String ownerId) {
        logger.info("deleting product with id: {}", productId);
        productRepository.delete(productId, ownerId);
    }
}
