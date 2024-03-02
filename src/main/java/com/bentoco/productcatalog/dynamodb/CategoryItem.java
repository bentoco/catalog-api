package com.bentoco.productcatalog.dynamodb;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@ToString
@DynamoDbBean
@EqualsAndHashCode
public class CategoryItem {

    private final static String CATEGORY_PREFIX = "#CATEGORY#";

    static String prefixedId(String id) {
        return CATEGORY_PREFIX + id;
    }

    private String id;
    private String title;
    private String description;

    @DynamoDbSortKey
    @DynamoDbAttribute("sk")
    public String getId() {
        return prefixedId(this.id);
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
