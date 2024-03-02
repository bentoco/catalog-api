package com.bentoco.productcatalog.dynamodb;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@ToString
@DynamoDbBean
@EqualsAndHashCode
public class OwnerItem {

    private static final String OWNER_PREFIX = "OWNER#";

    static String prefixedId(String id) {
        return OWNER_PREFIX + id;
    }

    private String id;
    private CategoryItem category;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("pk")
    public String getId() {
        return prefixedId(this.id);
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbConvertedBy(CategoryConverter.class)
    public CategoryItem getCategory() {
        return category;
    }

    public void setCategory(CategoryItem category) {
        this.category = category;
    }
}
