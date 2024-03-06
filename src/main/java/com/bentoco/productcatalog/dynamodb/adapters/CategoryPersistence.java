package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.repositories.CategoryRepository;
import com.bentoco.productcatalog.dynamodb.tables.CategoryTable;
import com.bentoco.productcatalog.mappers.CategoryMapper;
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
public class CategoryPersistence implements CategoryRepository {

    static final String CATEGORY_TABLE = "Category";
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final RequestContext requestContext;

    private final static CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(CategoryPersistence.class);

    @Override
    public UUID upsert(final Category category) {
        Owner owner = new Owner(requestContext.getProfile().ownerId());
        category.setOwner(owner);

        CategoryTable categoryTable = categoryMapper.toTable(category);
        var categoryRequest = PutItemEnhancedRequest.builder(CategoryTable.class)
                .item(categoryTable)
                .build();

        logger.info("inserting category item: {}", categoryTable);
        try {
            this.getTable().putItem(categoryRequest);
            return category.getId();
        } catch (DynamoDbException e) {
            logger.error("error creating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID categoryId) {
        String ownerId = String.valueOf(requestContext.getProfile().ownerId());
        String partitionKey = CategoryTable.prefixedId(String.valueOf(categoryId));
        var deleteRequest = DeleteItemEnhancedRequest.builder()
                .conditionExpression(Expression.builder()
                        .expression("OwnerID = :owner_id")
                        .putExpressionValue(":owner_id", AttributeValue.fromS(ownerId)).build())
                .key(k -> k.partitionValue(partitionKey))
                .build();
        try {
            this.getTable().deleteItem(deleteRequest);
        } catch (ConditionalCheckFailedException e) {
            logger.error("error deleting category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private DynamoDbTable<CategoryTable> getTable() {
        return dynamoDbEnhancedClient.table(CATEGORY_TABLE, TableSchema.fromBean(CategoryTable.class));
    }
}
