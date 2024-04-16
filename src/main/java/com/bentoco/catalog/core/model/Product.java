package com.bentoco.catalog.core.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbImmutable(builder = Product.Builder.class)
public final class Product extends AbstractModel {

    private final String pk;
    private final String sk;
    private final String title;
    private final String description;
    private final Double price;
    private final String categoryId;

    public Product(Builder b) {
        this.pk = b.pk;
        this.sk = b.sk;
        this.title = b.title;
        this.description = b.description;
        this.price = b.price;
        this.categoryId = b.categoryId;
    }

    // This method will be automatically discovered and used by the TableSchema.
    public static Builder builder() { return new Builder(); }

    @DynamoDbPartitionKey
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }

    @DynamoDbAttribute("title")
    public String getTitle() {
        return title;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    @DynamoDbAttribute("price")
    public Double getPrice() {
        return price;
    }

    @DynamoDbAttribute("category_id")
    public String getCategoryId() {
        return categoryId;
    }

    public static final class Builder {
        private String pk;
        private String sk;
        private String title;
        private String description;
        private Double price;
        private String categoryId;

        // The private Builder constructor is visible to the enclosing Customer class.
        private Builder() {}

        public Product.Builder pk(String pk) { this.pk = pk; return this; }
        public Product.Builder sk(String sk) { this.sk = sk; return this; }
        public Product.Builder title(String title) { this.title = title; return this; }
        public Product.Builder description(String description) { this.description = description; return this; }
        public Product.Builder price(Double price) { this.price = price; return this; }
        public Product.Builder categoryId(String categoryId) { this.categoryId = categoryId; return this; }

        // This method will be automatically discovered and used by the TableSchema.
        public Product build() { return new Product(this); }
    }
}
