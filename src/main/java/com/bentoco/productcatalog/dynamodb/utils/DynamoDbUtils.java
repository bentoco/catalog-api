package com.bentoco.productcatalog.dynamodb.utils;

import com.bentoco.productcatalog.utils.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ConditionCheck;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.UUID;

public class DynamoDbUtils {

    public static <T> ConditionCheck<T> getConditionCheck(final Key key, final Expression expression) {
        return ConditionCheck.builder()
                .key(key)
                .conditionExpression(expression)
                .build();
    }

    public static Expression getMustBeUniqueTitleAndOwnerIdExpression(final String title, final String ownerId) {
        return Expression.builder()
                .expression("#t <> :tv AND #o <> :ov")
                .putExpressionName("#t", "Title")
                .putExpressionName("#o", "OwnerID")
                .putExpressionValue(":tv", AttributeValue.builder().s(title).build())
                .putExpressionValue(":ov", AttributeValue.builder().s(ownerId).build())
                .build();
    }

    public static Key getKey(final UUID id, final String prefix) {
        String partitionKey = StringUtils.prefixedId(id.toString(), prefix);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }

    public static Key getKey(final String id, final String prefix) {
        String partitionKey = StringUtils.prefixedId(id, prefix);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }
}
