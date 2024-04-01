package com.bentoco.productcatalog.dynamodb.adapters;

import com.bentoco.productcatalog.configurations.middlewares.RequestContext;
import com.bentoco.productcatalog.controller.exception.DynamoDbOperationsErrorException;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.model.Product;
import com.bentoco.productcatalog.core.repositories.ProductRepository;
import com.bentoco.productcatalog.dynamodb.tables.ProductCategoryMapping;
import com.bentoco.productcatalog.dynamodb.tables.ProductTable;
import com.bentoco.productcatalog.dynamodb.utils.DynamoDbUtils;
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
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

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
    public UUID insert(Product product) {
        var owner = new Owner(requestContext.getProfile().ownerId());
        var productItem = productMapper.toTable(product.withOwner(owner));
        var productCategoryMappingItem = productCategoryMappingMapper.toTable(product.getId(), product.getCategory().getId());

        logger.info("inserting product item: {}", productItem);
        try {
            var transactWriteItemsEnhancedRequest = buildTransactionRequest(productItem, productCategoryMappingItem);
            dynamoDbEnhancedClient.transactWriteItems(transactWriteItemsEnhancedRequest);
            return StringUtils.removePrefix(productItem.getProductId(), PRODUCT_PREFIX);
        } catch (DynamoDbException e) {
            logger.error("error inserting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void update(Product product) {
        Key key = DynamoDbUtils.getKey(product.getId(), PRODUCT_PREFIX);

        try {
            ProductTable entity = dynamoDbTemplate.load(key, ProductTable.class);

            if (entity == null) {
                throw new DynamoDbOperationsErrorException("item not found.");
            }

            entity.setTitle(product.getTitle());
            entity.setDescription(product.getDescription());
            entity.setPrice(product.getPrice());

            dynamoDbTemplate.save(entity);
        } catch (DynamoDbException e) {
            logger.error("error updating product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID productId) {
        Key key = DynamoDbUtils.getKey(productId, PRODUCT_PREFIX);

        try {
            dynamoDbTemplate.delete(key, ProductTable.class);
        } catch (DynamoDbException e) {
            logger.error("error deleting product item: {}", e.getMessage());
            throw new DynamoDbOperationsErrorException(e.getMessage());
        }
    }

    private TransactWriteItemsEnhancedRequest buildTransactionRequest(ProductTable productItem, ProductCategoryMapping productCategoryMappingItem) {
        DynamoDbTable<ProductTable> productDbTable = getTable(ProductTable.class);
        DynamoDbTable<ProductCategoryMapping> productCategoryDbTable = getTable(ProductCategoryMapping.class);

        Key key = DynamoDbUtils.getKey(productItem.getProductId(), PRODUCT_PREFIX);
        Expression expression = DynamoDbUtils.buildMustBeUniqueTitleAndOwnerIdExpression(productItem.getTitle(), productItem.getOwnerId());
        ConditionCheck<ProductTable> conditionCheck = DynamoDbUtils.getConditionCheck(key, expression);

        TransactWriteItemsEnhancedRequest.Builder requestBuilder = TransactWriteItemsEnhancedRequest.builder();

        requestBuilder.addPutItem(productDbTable, productItem)
                .addPutItem(productCategoryDbTable, productCategoryMappingItem);

        requestBuilder.addConditionCheck(productDbTable, conditionCheck);

        return requestBuilder.build();
    }

    private <T> DynamoDbTable<T> getTable(Class<T> clazz) {
        return dynamoDbEnhancedClient.table(
                tableNameResolver.resolve(clazz),
                tableSchemaResolver.resolve(clazz)
        );
    }
}
