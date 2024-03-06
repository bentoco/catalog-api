package com.bentoco.productcatalog.dynamodb.tables;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@ToString
@DynamoDbBean
@EqualsAndHashCode
public class CategoryTable {

    private final static String CATEGORY_PREFIX = "CategoryID#";

    public static String prefixedId(String id) {
        return CATEGORY_PREFIX + id;
    }

    private String categoryId;
    private String ownerId;
    private String title;
    private String description;

    @DynamoDbSortKey
    @DynamoDbAttribute("CategoryID")
    public String getCategoryId() {
        return prefixedId(this.categoryId);
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @DynamoDbAttribute("OwnerID")
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @DynamoDbAttribute("Title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDbAttribute("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = "OwnerIDIndex")
    public String getOwnerIDForIndex() {
        return this.ownerId;
    }

    public void setOwnerIDForIndex(String ownerId) {
        this.ownerId = ownerId;
    }
}
