package com.bentoco.productcatalog.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class ProductConverter implements AttributeConverter<ProductItem> {
    @Override
    public AttributeValue transformFrom(ProductItem input) {
        Map<String, AttributeValue> attributeValueMap = Map.of(
                "pk", AttributeValue.fromS(input.getOwnerId()),
                "sk", AttributeValue.fromS(input.getCategoryId()),
                "item_id", AttributeValue.fromS(input.getId()),
                "item_title", AttributeValue.fromS(input.getTitle()),
                "item_description", AttributeValue.fromS(input.getDescription()),
                "item_price", AttributeValue.fromN(input.getPrice())
        );

        return AttributeValue.fromM(attributeValueMap);
    }

    @Override
    public ProductItem transformTo(AttributeValue input) {
        Map<String, AttributeValue> m = input.m();
        ProductItem productItem = new ProductItem();
        productItem.setOwnerId(m.get("pk").s());
        productItem.setCategoryId(m.get("sk").s());
        productItem.setId(m.get("item_id").s());
        productItem.setTitle(m.get("item_title").s());
        productItem.setDescription(m.get("item_description").s());
        productItem.setPrice(m.get("item_price").n());

        return productItem;
    }

    @Override
    public EnhancedType<ProductItem> type() {
        return EnhancedType.of(ProductItem.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }
}
