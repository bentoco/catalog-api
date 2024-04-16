package com.bentoco.catalog.dynamodb.adapters;

import com.bentoco.catalog.core.model.Product;
import com.bentoco.catalog.core.repositories.ProductRepository;
import com.bentoco.catalog.dynamodb.utils.DynamoDbUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import static com.bentoco.catalog.constants.AwsConstants.PRODUCT_TABLE_NAME;

@Repository
@RequiredArgsConstructor
public class ProductPersistence implements ProductRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    @Override
    public String insert(Product product) {
        return null;
    }

    private void doInsertionTransaction(Product productItem) {
        String uniquePk = DynamoDbUtils.getUniquenessPk(productItem.getSk(), productItem.getTitle());
        enhancedClient.transactWriteItems(i -> i
                .addPutItem(this.getTable(), DynamoDbUtils.transactPutItemRequest(productItem))
                .addPutItem(this.getTable(), DynamoDbUtils.transactPutItemRequest(buildProductTable(uniquePk, productItem.getSk())))
        );
    }

    private static Product buildProductTable(String pk, String sk) {
        return Product.builder()
                .pk(pk)
                .sk(sk)
                .build();
    }

    @Override
    public void update(Product product) {

    }

    @Override
    public void delete(String productId, String ownerId) {

    }

    private DynamoDbTable<Product> getTable() {
        return enhancedClient.table(PRODUCT_TABLE_NAME, TableSchema.fromImmutableClass(Product.class));
    }
}
