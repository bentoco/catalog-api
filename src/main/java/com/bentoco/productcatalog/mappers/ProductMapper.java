package com.bentoco.productcatalog.mappers;

import com.bentoco.productcatalog.controller.request.ProductRequest;
import com.bentoco.productcatalog.controller.request.UpdateProductRequest;
import com.bentoco.productcatalog.dynamodb.tables.ProductTable;
import com.bentoco.productcatalog.core.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "category.id", source = "categoryId")
    Product toModel(ProductRequest productRequest);

    @Mapping(target = "id", source = "productId")
    Product toModel(UpdateProductRequest updateProductRequest, UUID productId);

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "categoryId", source = "category.id")
    ProductTable toTable(Product product);
}
