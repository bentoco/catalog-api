package com.bentoco.catalog.dynamodb.adapters;

import com.bentoco.catalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.catalog.core.model.Category;
import com.bentoco.catalog.core.repositories.CategoryRepository;
import com.bentoco.catalog.dynamodb.utils.DynamoDbUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactDeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactUpdateItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Objects;

import static com.bentoco.catalog.constants.AwsConstants.CATEGORIES_TABLE_NAME;

@Repository
@RequiredArgsConstructor
public class CategoryPersistence implements CategoryRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    private static final Logger logger = LogManager.getLogger(CategoryPersistence.class);
    private static final Expression MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION = DynamoDbUtils.buildMustBeUniqueTitleAndOwnerIdExpression();

    @Override
    public String insert(Category categoryItem) {
        logger.info("inserting category item: {}", categoryItem);
        try {
            doInsertionTransaction(categoryItem);
            return categoryItem.getPk();
        } catch (DynamoDbException e) {
            logger.error("error creating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private void doInsertionTransaction(Category categoryItem) {
        String uniquePk = DynamoDbUtils.getUniquenessPk(categoryItem.getSk(), categoryItem.getTitle());
        enhancedClient.transactWriteItems(i -> i
                .addPutItem(this.getTable(), this.transactPutItemRequest(categoryItem))
                .addPutItem(this.getTable(), this.transactPutItemRequest(uniquePk, categoryItem.getSk()))
        );
    }

    @Override
    public void update(Category category) {
        if (hasValidFields(category)) {
            logger.error("title and description cannot be null in the same request");
            throw new DynamoDbOperationsErrorException("update operation interrupted, title and description cannot be null in the same request");
        }

        try {
            if (Objects.isNull(category.getTitle())) {
                updateItem(category);
            } else {
                doUpdateTransaction(category);
            }
        } catch (DynamoDbException e) {
            logger.error("error updating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private boolean hasValidFields(Category category) {
        return Objects.isNull(category.getTitle()) && Objects.isNull(category.getDescription());
    }

    private void updateItem(Category categoryItem) {
        this.getTable().updateItem(UpdateItemEnhancedRequest.builder(Category.class)
                .item(categoryItem)
                .ignoreNulls(Boolean.TRUE)
                .build());
    }

    private void doUpdateTransaction(Category categoryItem) {
        String sk = categoryItem.getSk();
        String titleOld = getOldTitleFromDatabase(categoryItem.getPk(), sk);
        String uniquePkOld = DynamoDbUtils.getUniquenessPk(sk, titleOld);
        String uniquePkNew = DynamoDbUtils.getUniquenessPk(sk, categoryItem.getTitle());

        enhancedClient.transactWriteItems(i -> i
                .addUpdateItem(this.getTable(), this.transactUpdateItemRequest(categoryItem))
                .addDeleteItem(this.getTable(), this.transactDeleteItemRequest(uniquePkOld, sk))
                .addPutItem(this.getTable(), this.transactPutItemRequest(uniquePkNew, sk))
        );
    }

    private String getOldTitleFromDatabase(String pk, String sk) {
        DynamoDbTable<Category> table = this.getTable();
        Category item = table.getItem(Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build());

        if (Objects.isNull(item)) {
            logger.error(STR. "item not found to these keys: \{ pk } and \{ sk }" );
            throw new DynamoDbOperationsErrorException("item not found to these keys");
        }
        return item.getTitle();
    }

    @Override
    public void delete(String categoryId, String ownerId) {
        try {
            Category category = buildCategoriesTable(categoryId, ownerId);
            this.getTable().deleteItem(category);
        } catch (Exception e) {
            logger.error("error deleting category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private TransactUpdateItemEnhancedRequest<Category> transactUpdateItemRequest(Category updateItem) {
        return TransactUpdateItemEnhancedRequest.builder(Category.class)
                .item(updateItem)
                .ignoreNulls(Boolean.TRUE)
                .build();
    }

    private TransactDeleteItemEnhancedRequest transactDeleteItemRequest(String pk, String sk) {
        return TransactDeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(pk)
                        .sortValue(sk)
                        .build())
                .build();
    }

    private TransactPutItemEnhancedRequest<Category> transactPutItemRequest(String pk, String sk) {
        Category categoryPutItem = buildCategoriesTable(pk, sk);
        return TransactPutItemEnhancedRequest.builder(Category.class)
                .item(categoryPutItem)
                .conditionExpression(MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION)
                .build();
    }

    private TransactPutItemEnhancedRequest<Category> transactPutItemRequest(Category categoryItem) {
        return TransactPutItemEnhancedRequest.builder(Category.class)
                .item(categoryItem)
                .conditionExpression(MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION)
                .build();
    }

    private static Category buildCategoriesTable(String pk, String sk) {
        return Category.builder()
                .pk(pk)
                .sk(sk)
                .build();
    }

    public DynamoDbTable<Category> getTable() {
        return enhancedClient.table(CATEGORIES_TABLE_NAME, TableSchema.fromImmutableClass(Category.class));
    }
}
