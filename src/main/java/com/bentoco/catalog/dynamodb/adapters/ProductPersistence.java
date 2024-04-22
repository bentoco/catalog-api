package com.bentoco.catalog.dynamodb.adapters;

import com.bentoco.catalog.core.repositories.ProductRepository;
import com.bentoco.catalog.model.ProductImmutableBeanItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductPersistence implements ProductRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    @Override
    public String insert(ProductImmutableBeanItem product) {
        return null;
    }

    private static ProductImmutableBeanItem buildProductTable(String pk, String sk) {
        return ProductImmutableBeanItem.builder()
                .pk(pk)
                .sk(sk)
                .build();
    }

    @Override
    public void update(ProductImmutableBeanItem product) {

    }

    @Override
    public void delete(String productId, String ownerId) {

    }

    @Override
    public Boolean hasAnyRelationship(String categoryId) {
        DynamoDbIndex<ProductImmutableBeanItem> index = this.getImmutableTable().index("category_id_index");
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(categoryId)
                        .build());
        SdkIterable<Page<ProductImmutableBeanItem>> results = index.query(QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build());
        return !results.iterator().next().items().isEmpty();
    }


    private DynamoDbTable<ProductImmutableBeanItem> getImmutableTable() {
        return enhancedClient.table("catalog", TableSchema.fromImmutableClass(ProductImmutableBeanItem.class));
    }
}
