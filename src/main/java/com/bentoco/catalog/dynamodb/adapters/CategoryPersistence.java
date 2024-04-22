package com.bentoco.catalog.dynamodb.adapters;

import com.bentoco.catalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.catalog.core.repositories.CategoryRepository;
import com.bentoco.catalog.dynamodb.utils.DynamoDbUtils;
import com.bentoco.catalog.model.CategoryImmutableBeanItem;
import com.bentoco.catalog.utils.StringUtils;
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

import static com.bentoco.catalog.constants.AwsConstants.PREFIX_CATEGORY;
import static com.bentoco.catalog.constants.AwsConstants.PREFIX_OWNER;

@Repository
@RequiredArgsConstructor
public class CategoryPersistence implements CategoryRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final ProductPersistence productPersistence;

    private static final Logger logger = LogManager.getLogger(CategoryPersistence.class);
    private static final Expression MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION = DynamoDbUtils.buildMustBeUniqueTitleAndOwnerIdExpression();

    @Override
    public String insert(CategoryImmutableBeanItem categoryItem) {
        logger.info("inserting category item: {}", categoryItem);
        try {
            doInsertionTransaction(categoryItem);
            return StringUtils.removePrefix(categoryItem.getSk(), PREFIX_CATEGORY);
        } catch (DynamoDbException e) {
            logger.error("error creating category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private void doInsertionTransaction(CategoryImmutableBeanItem categoryItem) {
        String uniquePk = DynamoDbUtils.getUniquenessConstraint(categoryItem.getPk(), categoryItem.getTitle());
        enhancedClient.transactWriteItems(i -> i
                .addPutItem(this.getImmutableTable(), this.transactPutImmutableItemRequest(categoryItem))
                .addPutItem(this.getImmutableTable(), this.transactPutImmutableItemRequest(uniquePk, categoryItem.getSk()))
        );
    }

    @Override
    public void update(CategoryImmutableBeanItem category) {
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

    private boolean hasValidFields(CategoryImmutableBeanItem category) {
        return Objects.isNull(category.getTitle()) && Objects.isNull(category.getDescription());
    }

    private void updateItem(CategoryImmutableBeanItem categoryItem) {
        this.getImmutableTable().updateItem(UpdateItemEnhancedRequest.builder(CategoryImmutableBeanItem.class)
                .item(categoryItem)
                .ignoreNulls(Boolean.TRUE)
                .build());
    }

    private void doUpdateTransaction(CategoryImmutableBeanItem categoryItem) {
        String sk = categoryItem.getSk();
        String pk = categoryItem.getPk();
        String titleOld = getOldTitleFromDatabase(pk, sk);
        String uniquePkOld = DynamoDbUtils.getUniquenessConstraint(pk, titleOld);
        String uniquePkNew = DynamoDbUtils.getUniquenessConstraint(pk, categoryItem.getTitle());

        enhancedClient.transactWriteItems(i -> i
                .addUpdateItem(this.getImmutableTable(), this.transactUpdateItemRequest(categoryItem))
                .addDeleteItem(this.getImmutableTable(), this.transactDeleteItemRequest(uniquePkOld, sk))
                .addPutItem(this.getImmutableTable(), this.transactPutImmutableItemRequest(uniquePkNew, sk))
        );
    }

    private String getOldTitleFromDatabase(String pk, String sk) {
        DynamoDbTable<CategoryImmutableBeanItem> table = this.getImmutableTable();
        CategoryImmutableBeanItem item = table.getItem(Key.builder()
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
            Boolean categoryIsBeingUsed = productPersistence.hasAnyRelationship(categoryId);
            if (categoryIsBeingUsed) {
                throw new DynamoDbOperationsErrorException("this category cannot be deleted due to existing product dependencies.");
            }
            CategoryImmutableBeanItem category = buildCategoryImmutableTable(
                    StringUtils.prefixedId(ownerId, PREFIX_OWNER),
                    StringUtils.prefixedId(categoryId, PREFIX_CATEGORY)
            );
            this.getImmutableTable().deleteItem(category);
        } catch (Exception e) {
            logger.error("error deleting category item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private TransactUpdateItemEnhancedRequest<CategoryImmutableBeanItem> transactUpdateItemRequest(CategoryImmutableBeanItem updateItem) {
        return TransactUpdateItemEnhancedRequest.builder(CategoryImmutableBeanItem.class)
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

    private TransactPutItemEnhancedRequest<CategoryImmutableBeanItem> transactPutImmutableItemRequest(String pk, String sk) {
        CategoryImmutableBeanItem categoryPutItem = buildCategoryImmutableTable(pk, sk);
        return TransactPutItemEnhancedRequest.builder(CategoryImmutableBeanItem.class)
                .item(categoryPutItem)
                .conditionExpression(MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION)
                .build();
    }

    private TransactPutItemEnhancedRequest<CategoryImmutableBeanItem> transactPutImmutableItemRequest(CategoryImmutableBeanItem categoryItem) {
        return TransactPutItemEnhancedRequest.builder(CategoryImmutableBeanItem.class)
                .item(categoryItem)
                .conditionExpression(MUST_BE_UNIQUE_TITLE_AND_OWNER_ID_EXPRESSION)
                .build();
    }

    private static CategoryImmutableBeanItem buildCategoryImmutableTable(String pk, String sk) {
        return CategoryImmutableBeanItem.builder()
                .pk(pk)
                .sk(sk)
                .build();
    }

    public DynamoDbTable<CategoryImmutableBeanItem> getImmutableTable() {
        return enhancedClient.table("catalog", TableSchema.fromImmutableClass(CategoryImmutableBeanItem.class));
    }
}
