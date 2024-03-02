package com.bentoco.productcatalog.dynamodb;

import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.mapper.ProductMapper;
import com.bentoco.productcatalog.model.Product;
import com.bentoco.productcatalog.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductPersistence implements ProductRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    private final static ProductMapper productMapper = ProductMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(ProductPersistence.class);

    @Override
    public void upsert(final Product product) {
        ProductItem productItem = productMapper.toItem(product);
        logger.info("inserting product item: {}", productItem);

        var productRequest = PutItemEnhancedRequest.builder(ProductItem.class)
                .item(productItem)
                .build();
        try {
            this.getTable().putItem(productRequest);
        } catch (DynamoDbException e) {
            logger.error("error inserting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID productId) {
        var deleteRequest = DeleteItemEnhancedRequest.builder()
                .key(generateKey(productId))
                .build();
        this.getTable().deleteItem(deleteRequest);
    }

    private static Key generateKey(UUID pk) {
        return Key.builder().partitionValue(String.valueOf(pk)).build();
    }

    private DynamoDbTable<ProductItem> getTable() {
        return dynamoDbEnhancedClient.table("productcatalog",
                TableSchema.fromBean(ProductItem.class));
    }
}
