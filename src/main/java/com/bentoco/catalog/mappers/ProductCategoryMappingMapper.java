package com.bentoco.catalog.mappers;

import com.bentoco.catalog.dynamodb.tables.ProductCategoryMapping;
import com.bentoco.catalog.utils.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(imports = StringUtils.class)
public interface ProductCategoryMappingMapper {

    ProductCategoryMappingMapper INSTANCE = Mappers.getMapper(ProductCategoryMappingMapper.class);
    @Mapping(target = "productId", expression = "java(StringUtils.prefixedId(String.valueOf(productId), \"#ProductID\"))")
    @Mapping(target = "categoryId", expression = "java(StringUtils.prefixedId(String.valueOf(categoryId), \"CategoryID\"))")
    ProductCategoryMapping toTable(UUID productId, UUID categoryId);
}
