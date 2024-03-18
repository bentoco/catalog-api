package com.bentoco.productcatalog.configurations;

import com.bentoco.productcatalog.dynamodb.tables.CategoryTable;
import com.bentoco.productcatalog.dynamodb.tables.ProductTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class DynamoTableSchemaConfiguration {

    @Bean
    public TableSchema<CategoryTable> categoryTableSchema() {
        return TableSchema.fromBean(CategoryTable.class);
    }

    @Bean
    public TableSchema<ProductTable> productTableSchema() {
        return TableSchema.fromBean(ProductTable.class);
    }
}
