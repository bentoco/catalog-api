package com.bentoco.productcatalog.dynamodb;

import com.bentoco.productcatalog.dynamodb.tables.ProductTable;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class ProductConverter implements AttributeConverter<ProductTable> {

    @Override
    public AttributeValue transformFrom(ProductTable input) {
        Map<String, AttributeValue> attributeValueMap = Map.of(
                "ProductID", AttributeValue.fromS(input.getProductId()),
                "OwnerID", AttributeValue.fromS(input.getOwnerId()),
                "CategoryID", AttributeValue.fromS(input.getCategoryId()),
                "Title", AttributeValue.fromS(input.getTitle()),
                "Description", AttributeValue.fromS(input.getDescription()),
                "Price", AttributeValue.fromN(String.valueOf(input.getPrice().doubleValue()))
        );

        return AttributeValue.fromM(attributeValueMap);
    }

    @Override
    public ProductTable transformTo(AttributeValue input) {
        Map<String, AttributeValue> m = input.m();
        ProductTable productTable = new ProductTable();
        productTable.setProductId(m.get("ProductID").s());
        productTable.setOwnerId(m.get("OwnerID").s());
        productTable.setCategoryId(m.get("CategoryID").s());
        productTable.setTitle(m.get("Title").s());
        productTable.setDescription(m.get("Description").s());
        productTable.setPrice(Double.valueOf(m.get("Price").n()));

        return productTable;
    }

    @Override
    public EnhancedType<ProductTable> type() {
        return EnhancedType.of(ProductTable.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }
}
