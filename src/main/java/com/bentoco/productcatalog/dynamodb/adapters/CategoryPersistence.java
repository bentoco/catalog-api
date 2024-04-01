package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.repositories.CategoryRepository;
import com.bentoco.productcatalog.dynamodb.tables.CategoriesTable;
import com.bentoco.productcatalog.dynamodb.utils.DynamoDbUtils;
import com.bentoco.productcatalog.mappers.CategoryMapper;
import io.awspring.cloud.dynamodb.DefaultDynamoDbTableNameResolver;
import io.awspring.cloud.dynamodb.DynamoDbTableSchemaResolver;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactDeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactUpdateItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.util.Objects;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryPersistence implements CategoryRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTableSchemaResolver tableSchemaResolver;
    private final DefaultDynamoDbTableNameResolver tableNameResolver;

    private final static CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(CategoryPersistence.class);

    @Override
    public UUID insert(Category category) {
        CategoriesTable categoryItem = categoryMapper.toTable(category);
        logger.info("inserting category item: {}", categoryItem);
        try {
            doTransaction(categoryItem);
            return category.getId();
        } catch (DynamoDbException e) {
            logger.error("error creating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void update(Category category) {
        try {
            doTransactionUpdate(categoryMapper.toTable(category));
        } catch (DynamoDbException e) {
            logger.error("error updating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID categoryId, UUID ownerId) {
        try {
            CategoriesTable categoriesTable = new CategoriesTable(categoryId.toString(), ownerId.toString());
            this.getTable().deleteItem(categoriesTable);
        } catch (Exception e) {
            logger.error("error deleting category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private DynamoDbTable<CategoriesTable> getTable() {
        return dynamoDbEnhancedClient.table(
                tableNameResolver.resolve(CategoriesTable.class),
                tableSchemaResolver.resolve(CategoriesTable.class)
        );
    }

    private void doTransaction(CategoriesTable categoryItem) {
        Expression expression = DynamoDbUtils.buildMustBeUniqueTitleAndOwnerIdExpression();
        String uniquenessPk = DynamoDbUtils.getUniquenessPk(categoryItem.getSk(), categoryItem.getTitle());
        CategoriesTable uniquenessItem = new CategoriesTable(uniquenessPk, categoryItem.getSk());

        dynamoDbEnhancedClient.transactWriteItems(i -> i
                .addPutItem(this.getTable(), TransactPutItemEnhancedRequest.builder(CategoriesTable.class)
                        .item(categoryItem)
                        .conditionExpression(expression)
                        .build())
                .addPutItem(this.getTable(), TransactPutItemEnhancedRequest.builder(CategoriesTable.class)
                        .item(uniquenessItem)
                        .conditionExpression(expression)
                        .build()));
    }

    private void doTransactionUpdate(CategoriesTable categoryItem) {
        try {
            String ownerIdSk = categoryItem.getSk();
            String oldTitle = getOldTitleFromDatabase(categoryItem.getPk(), ownerIdSk);
            String oldUniquePk = DynamoDbUtils.getUniquenessPk(ownerIdSk, oldTitle);
            String newUniquePk = DynamoDbUtils.getUniquenessPk(ownerIdSk, categoryItem.getTitle());

            dynamoDbEnhancedClient.transactWriteItems(i -> i
                    .addUpdateItem(this.getTable(), this.updateItemRequest(categoryItem))
                    .addDeleteItem(this.getTable(), this.deleteItemRequest(oldUniquePk, ownerIdSk))
                    .addPutItem(this.getTable(), this.putItemRequest(newUniquePk, ownerIdSk))
            );
        } catch (TransactionCanceledException | ConditionalCheckFailedException e) {
            logger.error("transaction failed: " + e.getMessage());
        }
    }

    private String getOldTitleFromDatabase(String pk, String sk) {
        DynamoDbTable<CategoriesTable> table = this.getTable();
        CategoriesTable item = table.getItem(Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build());

        if (Objects.isNull(item)) {
            throw new DynamoDbOperationsErrorException(STR."item not found to these keys");
        }
        return item.getTitle();
    }

    private TransactUpdateItemEnhancedRequest<CategoriesTable> updateItemRequest(CategoriesTable updateItem) {
        return TransactUpdateItemEnhancedRequest.builder(CategoriesTable.class)
                .item(updateItem)
                .ignoreNulls(true)
                .build();
    }

    private TransactDeleteItemEnhancedRequest deleteItemRequest(String pk, String sk) {
        return TransactDeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(pk)
                        .sortValue(sk)
                        .build())
                .build();
    }

    private TransactPutItemEnhancedRequest<CategoriesTable> putItemRequest(String newUniquePk, String ownerIdSk) {
        CategoriesTable categoryPutItem = new CategoriesTable(newUniquePk, ownerIdSk); // todo: add builder
        Expression expression = DynamoDbUtils.buildMustBeUniqueTitleAndOwnerIdExpression();

        return TransactPutItemEnhancedRequest.builder(CategoriesTable.class)
                .item(categoryPutItem)
                .conditionExpression(expression)
                .build();
    }
}
