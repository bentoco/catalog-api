package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.model.Product;
import com.bentoco.productcatalog.core.repositories.ProductRepository;
import com.bentoco.productcatalog.dynamodb.tables.CategoryTable;
import com.bentoco.productcatalog.dynamodb.tables.ProductCategoryMapping;
import com.bentoco.productcatalog.dynamodb.tables.ProductTable;
import com.bentoco.productcatalog.mappers.ProductCategoryMappingMapper;
import com.bentoco.productcatalog.mappers.ProductMapper;
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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Objects;
import java.util.UUID;

import static com.bentoco.productcatalog.dynamodb.tables.ProductTable.PRODUCT_PREFIX;

@Repository
@RequiredArgsConstructor
public class ProductPersistence implements ProductRepository {

    private final DynamoDbTemplate dynamoDbTemplate;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final RequestContext requestContext;
    private final DynamoDbTableSchemaResolver tableSchemaResolver;
    private final DefaultDynamoDbTableNameResolver tableNameResolver;

    private final static ProductMapper productMapper = ProductMapper.INSTANCE;
    private final static ProductCategoryMappingMapper productCategoryMappingMapper = ProductCategoryMappingMapper.INSTANCE;
    private final static Logger logger = LogManager.getLogger(ProductPersistence.class);

    @Override
    public UUID insert(final Product product) {
        var owner = new Owner(requestContext.getProfile().ownerId());
        var productItem = productMapper.toTable(product.withOwner(owner));
        var productCategoryMappingItem = productCategoryMappingMapper.toTable(product.getId(), product.getCategory().getId());

        logger.info("inserting product item: {}", productItem);
        try {
            var transactWriteItemsEnhancedRequest = transactionBuilder(productItem, productCategoryMappingItem);
            dynamoDbEnhancedClient.transactWriteItems(transactWriteItemsEnhancedRequest);
            return StringUtils.removePrefix(productItem.getProductId(), PRODUCT_PREFIX);
        } catch (DynamoDbException e) {
            logger.error("error inserting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void update(final Product product) {
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

    private static Key getKey(final UUID productId) {
        String partitionKey = StringUtils.prefixedId(productId.toString(), PRODUCT_PREFIX);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }

    private static Key getKey(final String productId) {
        String partitionKey = StringUtils.prefixedId(productId, PRODUCT_PREFIX);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }

    private TransactWriteItemsEnhancedRequest transactionBuilder(final ProductTable productItem, final ProductCategoryMapping productCategoryMappingItem) {
        DynamoDbTable<ProductTable> productDbTable = getTable(ProductTable.class);
        DynamoDbTable<ProductCategoryMapping> productCategoryDbTable = getTable(ProductCategoryMapping.class);

        return TransactWriteItemsEnhancedRequest.builder()
                .addPutItem(productDbTable, productItem)
                .addPutItem(productCategoryDbTable, productCategoryMappingItem)
                .addConditionCheck(productDbTable, getTitleConditionCheck(productItem))
                .build();
    }

    private static ConditionCheck<ProductTable> getTitleConditionCheck(final ProductTable productItem) {
        var expression = getMustBeUniqueTitleAndOwnerIdExpression(productItem);
        return ConditionCheck.builder()
                .key(getKey(productItem.getProductId()))
                .conditionExpression(expression)
                .build();
    }

    private static Expression getMustBeUniqueTitleAndOwnerIdExpression(final ProductTable productItem) {
        return Expression.builder()
                .expression("#t <> :tv AND #o <> :ov")
                .putExpressionName("#t", "Title")
                .putExpressionName("#o", "OwnerID")
                .putExpressionValue(":tv", AttributeValue.builder().s(productItem.getTitle()).build())
                .putExpressionValue(":ov", AttributeValue.builder().s(productItem.getOwnerId()).build())
                .build();
    }

    private <T> DynamoDbTable<T> getTable(final Class<T> clazz) {
        return dynamoDbEnhancedClient.table(
                tableNameResolver.resolve(clazz),
                tableSchemaResolver.resolve(clazz)
        );
    }
}
