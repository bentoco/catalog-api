package com.bentoco.catalog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Value
@Builder
@DynamoDbImmutable(builder = ProductImmutableBeanItem.ProductImmutableBeanItemBuilder.class)
public class ProductImmutableBeanItem {

    @NonNull
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    String pk;

    @NonNull
    @Getter(onMethod_ = @DynamoDbSortKey)
    String sk;

    @Getter(onMethod_ = @DynamoDbAttribute("title"))
    String title;

    @Getter(onMethod_ = @DynamoDbAttribute("description"))
    String description;

    @Getter(onMethod_ = @DynamoDbAttribute("price"))
    Double price;

    @Getter(onMethod_ = {
            @DynamoDbAttribute("category_id"),
            @DynamoDbSecondaryPartitionKey(indexNames = "category_id_index")
    })
    String categoryId;

}
