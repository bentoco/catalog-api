package com.bentoco.productcatalog.dynamodb.utils;

import com.bentoco.productcatalog.utils.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ConditionCheck;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

import static com.bentoco.productcatalog.constants.AwsConstants.PREFIX_CATEGORY_OWNER_ID;
import static com.bentoco.productcatalog.constants.AwsConstants.PREFIX_CATEGORY_TITLE;

public class DynamoDbUtils {

    public static <T> ConditionCheck<T> getConditionCheck(final Key key, final Expression expression) {
        return ConditionCheck.builder()
                .key(key)
                .conditionExpression(expression)
                .build();
    }

    public static Expression buildMustBeUniqueTitleAndOwnerIdExpression(final String title, final String ownerId) {
        return Expression.builder()
                .expression("#t <> :tv AND #o <> :ov")
                .putExpressionName("#t", "Title")
                .putExpressionName("#o", "OwnerID")
                .putExpressionValue(":tv", AttributeValue.builder().s(title).build())
                .putExpressionValue(":ov", AttributeValue.builder().s(ownerId).build())
                .build();
    }

    public static Expression buildMustBeUniqueTitleAndOwnerIdExpression() {
        return Expression.builder()
                .expression("attribute_not_exists(#pk)")
                .expressionNames(Map.of("#pk", "pk"))
                .build();
    }

    public static Expression buildMustExistsExpression() {
        return Expression.builder()
                .expression("attribute_exists(#pk)")
                .expressionNames(Map.of("#pk", "pk"))
                .build();
    }

    public static Key getKey(final String id) {
        return Key.builder()
                .partitionValue(id)
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

    /**
     * This is an example of result: owner_id#1a2b3c#title#Foo
     */
    public static String getUniquenessPk(String ownerId, String title) {
        return STR."\{PREFIX_CATEGORY_OWNER_ID}\{ownerId}\{PREFIX_CATEGORY_TITLE}\{title}" ;
    }
}
