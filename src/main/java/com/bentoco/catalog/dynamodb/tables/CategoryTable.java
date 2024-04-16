package com.bentoco.catalog.dynamodb.tables;

import com.bentoco.catalog.utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

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
    @DynamoDbSecondaryPartitionKey(indexNames = {"OwnerIDIndex", "TitleOwnerIDIndex"})
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @DynamoDbAttribute("Title")
    @DynamoDbSecondarySortKey(indexNames = "TitleOwnerIDIndex")
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
}
