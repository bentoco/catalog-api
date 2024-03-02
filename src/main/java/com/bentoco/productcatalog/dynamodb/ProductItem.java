package com.bentoco.productcatalog.dynamodb;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@ToString
@DynamoDbBean
@EqualsAndHashCode
public class ProductItem {

    static String OWNER_PREFIX = "OWNER#";
    static String CATEGORY_PREFIX = "#CATEGORY#";
    static String PRODUCT_PREFIX = "PRODUCT#";

    static String prefixedId(String prefix, String id) {
        return prefix + id;
    }

    private String ownerId;
    private String categoryId;
    private String id;
    private String title;
    private String description;
    private String price;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("pk")
    public String getOwnerId() {
        return prefixedId(OWNER_PREFIX, this.ownerId);
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("sk")
    public String getCategoryId() {
        return prefixedId(CATEGORY_PREFIX, this.categoryId);
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @DynamoDbAttribute("item_id")
    public String getId() {
        return prefixedId(PRODUCT_PREFIX, this.id);
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("item_title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDbAttribute("item_description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDbAttribute("item_price")
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
