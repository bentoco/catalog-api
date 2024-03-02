package com.bentoco.productcatalog.dynamodb;

import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class CategoryConverter implements AttributeConverter<CategoryItem> {

    @SneakyThrows
    @Override
    public AttributeValue transformFrom(CategoryItem input) {
        Map<String, AttributeValue> attributeValueMap = Map.of(
                "sk", AttributeValue.fromS(input.getId()),
                "title", AttributeValue.fromS(input.getTitle()),
                "description", AttributeValue.fromS(input.getDescription()));

        return AttributeValue.fromM(attributeValueMap);
    }

    @SneakyThrows
    @Override
    public CategoryItem transformTo(AttributeValue input) {
        Map<String, AttributeValue> m = input.m();
        CategoryItem categoryItem = new CategoryItem();
        categoryItem.setId(m.get("sk").s());
        categoryItem.setTitle(m.get("title").s());
        categoryItem.setDescription(m.get("description").s());

        return categoryItem;
    }

    @Override
    public EnhancedType<CategoryItem> type() {
        return EnhancedType.of(CategoryItem.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }

}
