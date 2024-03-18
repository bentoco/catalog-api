package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.model.Product;
import com.bentoco.productcatalog.core.repositories.ProductRepository;
import com.bentoco.productcatalog.dynamodb.tables.CategoryTable;
import com.bentoco.productcatalog.dynamodb.tables.ProductTable;
import com.bentoco.productcatalog.mappers.ProductMapper;
import com.bentoco.productcatalog.utils.StringUtils;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Objects;
import java.util.UUID;

import static com.bentoco.productcatalog.dynamodb.tables.ProductTable.PRODUCT_PREFIX;

@Repository
@RequiredArgsConstructor
public class ProductPersistence implements ProductRepository {

    private final DynamoDbTemplate dynamoDbTemplate;
    private final RequestContext requestContext;

    private final static ProductMapper productMapper = ProductMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(ProductPersistence.class);

    @Override
    public UUID insert(final Product product) {
        var owner = new Owner(requestContext.getProfile().ownerId());
        var productTable = productMapper.toTable(product.withOwner(owner));
        logger.info("inserting product item: {}", productTable);
        try {
            var result = dynamoDbTemplate.save(productTable);
            return StringUtils.removePrefix(result.getProductId(), PRODUCT_PREFIX);
        } catch (DynamoDbException e) {
            logger.error("error inserting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void update(Product product) {
        Key key = getKey(product.getId());
        var entity = dynamoDbTemplate.load(key, ProductTable.class);
        if (Objects.isNull(entity)) {
            throw new DynamoDbOperationsErrorException("item not found.");
        }
        entity.setTitle(product.getTitle());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        dynamoDbTemplate.save(entity);
    }

    @Override
    public void delete(final UUID productId) {
        Key key = getKey(productId);
        try {
            dynamoDbTemplate.delete(key, CategoryTable.class);
        } catch (Exception e) {
            logger.error("error deleting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private static Key getKey(UUID productId) {
        String partitionKey = StringUtils.prefixedId(productId.toString(), PRODUCT_PREFIX);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }
}
