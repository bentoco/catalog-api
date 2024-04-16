package com.bentoco.catalog.dynamodb;

import com.bentoco.catalog.dynamodb.tables.CategoryTable;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class CategoryConverter implements AttributeConverter<CategoryTable> {

    @SneakyThrows
    @Override
    public AttributeValue transformFrom(CategoryTable input) {
        Map<String, AttributeValue> attributeValueMap = Map.of(
                "CategoryID", AttributeValue.fromS(input.getCategoryId()),
                "OwnerID", AttributeValue.fromS(input.getOwnerId()),
                "Title", AttributeValue.fromS(input.getTitle()),
                "Description", AttributeValue.fromS(input.getDescription()));
        return AttributeValue.fromM(attributeValueMap);
    }

    @SneakyThrows
    @Override
    public CategoryTable transformTo(AttributeValue input) {
        Map<String, AttributeValue> m = input.m();
        CategoryTable categoryTable = new CategoryTable();
        categoryTable.setCategoryId(m.get("CategoryID").s());
        categoryTable.setOwnerId(m.get("OwnerID").s());
        categoryTable.setTitle(m.get("Title").s());
        categoryTable.setDescription(m.get("Description").s());
        return categoryTable;
    }

    @Override
    public EnhancedType<CategoryTable> type() {
        return EnhancedType.of(CategoryTable.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }

}
