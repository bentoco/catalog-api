package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.model.Product;
import com.bentoco.productcatalog.core.repositories.ProductRepository;
import com.bentoco.productcatalog.dynamodb.tables.ProductTable;
import com.bentoco.productcatalog.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductPersistence implements ProductRepository {

    static final String PRODUCT_TABLE = "Product";
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final RequestContext requestContext;

    private final static ProductMapper productMapper = ProductMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(ProductPersistence.class);

    @Override
    public UUID upsert(final Product product) {
        Owner owner = new Owner(requestContext.getProfile().ownerId());
        product.setOwner(owner);
        ProductTable productTable = productMapper.toTable(product);
        logger.info("inserting product item: {}", productTable);

        var productRequest = PutItemEnhancedRequest.builder(ProductTable.class)
                .item(productTable)
                .build();
        try {
            this.getTable().putItem(productRequest);
            return product.getId();
        } catch (DynamoDbException e) {
            logger.error("error inserting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void delete(final UUID productId) {
        String ownerId = String.valueOf(requestContext.getProfile().ownerId());
        String partitionKey = ProductTable.prefixedId(String.valueOf(productId));
        var deleteRequest = DeleteItemEnhancedRequest.builder()
                .conditionExpression(Expression.builder()
                        .expression("OwnerID = :owner_id")
                        .putExpressionValue(":owner_id", AttributeValue.fromS(ownerId)).build())
                .key(k -> k.partitionValue(partitionKey))
                .build();
        try {
            this.getTable().deleteItem(deleteRequest);
        } catch (ConditionalCheckFailedException e) {
            logger.error("error deleting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private DynamoDbTable<ProductTable> getTable() {
        return dynamoDbEnhancedClient.table(PRODUCT_TABLE, TableSchema.fromBean(ProductTable.class));
    }
}
