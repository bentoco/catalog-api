package com.bentoco.catalog.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Value
@Builder
@DynamoDbImmutable(builder = CategoryImmutableBeanItem.CategoryImmutableBeanItemBuilder.class)
public class CategoryImmutableBeanItem {

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

}
