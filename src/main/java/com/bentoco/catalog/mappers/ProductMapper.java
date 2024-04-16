package com.bentoco.catalog.mappers;

import com.bentoco.catalog.controller.request.ProductRequest;
import com.bentoco.catalog.controller.request.UpdateProductRequest;
import com.bentoco.catalog.core.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "pk", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "sk", source = "ownerId")
    Product toModel(ProductRequest request, String ownerId);

    @Mapping(target = "pk", source = "productId")
    @Mapping(target = "sk", source = "ownerId")
    Product toModel(UpdateProductRequest request, String productId, String ownerId);

}
