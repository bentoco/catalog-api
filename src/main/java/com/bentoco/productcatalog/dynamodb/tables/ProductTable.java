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
public class ProductTable {

    public static final String PRODUCT_PREFIX = "ProductID#";

    private String productId;
    private String categoryId;
    private String ownerId;
    private String title;
    private String description;
    private Double price;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("ProductID")
    public String getProductId() {
        return StringUtils.prefixedId(this.productId, PRODUCT_PREFIX);
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @DynamoDbAttribute("CategoryID")
    public String getCategoryId() {
        return categoryId;
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

    @DynamoDbAttribute("Price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
