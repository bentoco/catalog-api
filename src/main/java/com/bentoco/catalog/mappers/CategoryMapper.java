package com.bentoco.catalog.mappers;

import com.bentoco.catalog.controller.request.CreateCategoryRequest;
import com.bentoco.catalog.controller.request.UpdateCategoryRequest;
import com.bentoco.catalog.core.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "pk", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "sk", source = "ownerId")
    Category toModel(CreateCategoryRequest request, String ownerId);

    @Mapping(target = "pk", source = "categoryId")
    @Mapping(target = "sk", source = "ownerId")
    Category toModel(UpdateCategoryRequest request, String categoryId, String ownerId);

}
