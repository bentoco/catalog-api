package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.repositories.CategoryRepository;
import com.bentoco.productcatalog.dynamodb.tables.CategoriesTable;
import com.bentoco.productcatalog.dynamodb.utils.DynamoDbUtils;
import com.bentoco.productcatalog.mappers.CategoryMapper;
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
import java.util.UUID;

import static com.bentoco.productcatalog.constants.AwsConstants.CATEGORIES_TABLE_NAME;

@Repository
@RequiredArgsConstructor
public class CategoryPersistence implements CategoryRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    private static final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private static final Logger logger = LogManager.getLogger(CategoryPersistence.class);
    private static final Expression MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION = DynamoDbUtils.buildMustBeUniqueTitleAndOwnerIdExpression();

    @Override
    public UUID insert(Category category) {
        CategoriesTable categoryItem = categoryMapper.toTable(category);
        logger.info("inserting category item: {}", categoryItem);
        try {
            doInsertionTransaction(categoryItem);
            return category.getId();
        } catch (DynamoDbException e) {
            logger.error("error creating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private void doInsertionTransaction(CategoriesTable categoryItem) {
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

        CategoriesTable categoriesTable = categoryMapper.toTable(category);
        try {
            if (Objects.isNull(category.getTitle())) {
                updateItem(categoriesTable);
            } else {
                doUpdateTransaction(categoriesTable);
            }
        } catch (DynamoDbException e) {
            logger.error("error updating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private boolean hasValidFields(Category category) {
        return Objects.isNull(category.getTitle()) && Objects.isNull(category.getDescription());
    }

    private void updateItem(CategoriesTable categoryItem) {
        this.getTable().updateItem(UpdateItemEnhancedRequest.builder(CategoriesTable.class)
                .item(categoryItem)
                .ignoreNulls(Boolean.TRUE)
                .build());
    }

    private void doUpdateTransaction(CategoriesTable categoryItem) {
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

    @Override
    public void delete(UUID categoryId, UUID ownerId) {
        try {
            String titleOld = getOldTitleFromDatabase(categoryId.toString(), ownerId.toString());
            String uniquePkOld = DynamoDbUtils.getUniquenessPk(ownerId.toString(), titleOld);

            enhancedClient.transactWriteItems(i -> i
                    .addDeleteItem(this.getTable(), transactDeleteItemRequest(categoryId.toString(), ownerId.toString()))
                    .addDeleteItem(this.getTable(), transactDeleteItemRequest(uniquePkOld, ownerId.toString()))
            );
        } catch (Exception e) {
            logger.error("error deleting category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private String getOldTitleFromDatabase(String pk, String sk) {
        DynamoDbTable<CategoriesTable> table = this.getTable();
        CategoriesTable item = table.getItem(Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build());

        if (Objects.isNull(item)) {
            logger.error(STR. "item not found to these keys: \{ pk } and \{ sk }" );
            throw new DynamoDbOperationsErrorException("item not found to these keys");
        }
        return item.getTitle();
    }

    private TransactUpdateItemEnhancedRequest<CategoriesTable> transactUpdateItemRequest(CategoriesTable updateItem) {
        return TransactUpdateItemEnhancedRequest.builder(CategoriesTable.class)
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

    private TransactPutItemEnhancedRequest<CategoriesTable> transactPutItemRequest(String pk, String sk) {
        CategoriesTable categoryPutItem = buildCategoriesTable(pk, sk);
        return TransactPutItemEnhancedRequest.builder(CategoriesTable.class)
                .item(categoryPutItem)
                .conditionExpression(MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION)
                .build();
    }

    private TransactPutItemEnhancedRequest<CategoriesTable> transactPutItemRequest(CategoriesTable categoryItem) {
        return TransactPutItemEnhancedRequest.builder(CategoriesTable.class)
                .item(categoryItem)
                .conditionExpression(MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION)
                .build();
    }

    private static CategoriesTable buildCategoriesTable(String pk, String sk) {
        return CategoriesTable.builder()
                .pk(pk)
                .sk(sk)
                .build();
    }

    private DynamoDbTable<CategoriesTable> getTable() {
        return enhancedClient.table(CATEGORIES_TABLE_NAME, TableSchema.fromImmutableClass(CategoriesTable.class));
    }
}
