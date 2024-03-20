package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.repositories.CategoryRepository;
import com.bentoco.productcatalog.dynamodb.tables.CategoryTable;
import com.bentoco.productcatalog.dynamodb.utils.DynamoDbUtils;
import com.bentoco.productcatalog.mappers.CategoryMapper;
import com.bentoco.productcatalog.utils.StringUtils;
import io.awspring.cloud.dynamodb.DefaultDynamoDbTableNameResolver;
import io.awspring.cloud.dynamodb.DynamoDbTableSchemaResolver;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ConditionCheck;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Objects;
import java.util.UUID;

import static com.bentoco.productcatalog.dynamodb.tables.CategoryTable.CATEGORY_PREFIX;

@Repository
@RequiredArgsConstructor
public class CategoryPersistence implements CategoryRepository {

    private final DynamoDbTemplate dynamoDbTemplate;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final RequestContext requestContext;
    private final DynamoDbTableSchemaResolver tableSchemaResolver;
    private final DefaultDynamoDbTableNameResolver tableNameResolver;

    private final static CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(CategoryPersistence.class);

    @Override
    public UUID insert(Category category) {
        Owner owner = new Owner(requestContext.getProfile().ownerId());
        category.setOwner(owner);
        CategoryTable categoryItem = categoryMapper.toTable(category);

        logger.info("inserting category item: {}", categoryItem);
        try {
            TransactWriteItemsEnhancedRequest transactWriteItemsEnhancedRequest = buildTransactionRequest(categoryItem);
            dynamoDbEnhancedClient.transactWriteItems(transactWriteItemsEnhancedRequest);
            return StringUtils.removePrefix(categoryItem.getCategoryId(), CATEGORY_PREFIX);
        } catch (DynamoDbException e) {
            logger.error("error creating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private TransactWriteItemsEnhancedRequest buildTransactionRequest(CategoryTable categoryItem) {
        Key key = DynamoDbUtils.getKey(categoryItem.getCategoryId(), CATEGORY_PREFIX);
        Expression expression = DynamoDbUtils.getMustBeUniqueTitleAndOwnerIdExpression(categoryItem.getTitle(), categoryItem.getOwnerId());
        ConditionCheck<CategoryTable> conditionCheck = DynamoDbUtils.getConditionCheck(key, expression);

        return TransactWriteItemsEnhancedRequest.builder()
                .addPutItem(this.getTable(), categoryItem)
                .addConditionCheck(this.getTable(), conditionCheck)
                .build();
    }

    @Override
    public void update(final Category category) {
        Key key = DynamoDbUtils.getKey(category.getId(), CATEGORY_PREFIX);
        var entity = dynamoDbTemplate.load(key, CategoryTable.class);
        if (Objects.isNull(entity)) {
            throw new DynamoDbOperationsErrorException("item not found");
        }
        entity.setTitle(category.getTitle());
        entity.setDescription(category.getDescription());
        dynamoDbTemplate.save(entity);
    }

    @Override
    public void delete(final UUID categoryId) {
        Key key = DynamoDbUtils.getKey(categoryId, CATEGORY_PREFIX);
        try {
            dynamoDbTemplate.delete(key, CategoryTable.class);
        } catch (Exception e) {
            logger.error("error deleting category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }


    private DynamoDbTable<CategoryTable> getTable() {
        return dynamoDbEnhancedClient.table(
                tableNameResolver.resolve(CategoryTable.class),
                tableSchemaResolver.resolve(CategoryTable.class)
        );
    }
}
