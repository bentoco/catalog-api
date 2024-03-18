package com.bentoco.productcatalog.dynamodb.tables;

import com.bentoco.productcatalog.utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@ToString
@DynamoDbBean
@EqualsAndHashCode
public class CategoryTable {

    public final static String CATEGORY_PREFIX = "CategoryID#";

    private String categoryId;
    private String ownerId;
    private String title;
    private String description;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("CategoryID")
    public String getCategoryId() {
        return StringUtils.prefixedId(this.categoryId, CATEGORY_PREFIX);
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
