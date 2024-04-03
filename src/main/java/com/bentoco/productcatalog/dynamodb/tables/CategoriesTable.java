package com.bentoco.productcatalog.dynamodb.tables;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@ToString
@EqualsAndHashCode
@DynamoDbImmutable(builder = CategoriesTable.Builder.class)
public class CategoriesTable {

    private final String pk;
    private final String sk;
    private final String title;
    private final String description;

    private CategoriesTable(Builder b) {
        this.pk = b.pk;
        this.sk = b.sk;
        this.title = b.title;
        this.description = b.description;
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

    public static final class Builder {
        private String pk;
        private String sk;
        private String title;
        private String description;

        // The private Builder constructor is visible to the enclosing Customer class.
        private Builder() {}

        public Builder pk(String pk) { this.pk = pk; return this; }
        public Builder sk(String sk) { this.sk = sk; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }

        // This method will be automatically discovered and used by the TableSchema.
        public CategoriesTable build() { return new CategoriesTable(this); }
    }
}
