package com.bentoco.productcatalog.mappers;

import com.bentoco.productcatalog.controller.request.CategoryRequest;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.dynamodb.tables.CategoryTable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    Category toModel(CategoryRequest categoryRequest);

    @Mapping(target = "categoryId", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    CategoryTable toTable(Category category);
}
