package com.bentoco.productcatalog.mappers;

import com.bentoco.productcatalog.controller.request.ProductRequest;
import com.bentoco.productcatalog.core.model.Category;
import com.bentoco.productcatalog.core.model.Owner;
import com.bentoco.productcatalog.core.model.Product;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class ProductMapperTest {

    private final static ProductMapper mapper = ProductMapper.INSTANCE;

    @Test
    public void testToModelMethod() {
        ProductRequest productRequest = createProductRequest();

        var result = mapper.toModel(productRequest);

        assertThat(result).isNotNull().satisfies(it -> {
            assertThat(it.getId()).isInstanceOf(UUID.class);
            assertThat(it.getTitle()).isEqualTo(productRequest.title());
            assertThat(it.getDescription()).isEqualTo(productRequest.description());
            assertThat(it.getPrice()).isEqualTo(productRequest.price());
            assertThat(it.getCategory()).isNotNull().satisfies(category -> {
                assertThat(category.getId()).isEqualTo(UUID.fromString(productRequest.categoryId()));
            });
        });
    }

    @Test
    public void testToTableMethod() {
        Product product = createProduct();

        var result = mapper.toTable(product);

        assertThat(result).isNotNull().satisfies(it -> {
            assertThat(it.getProductId()).isEqualTo("ProductID#" + product.getId());
            assertThat(it.getTitle()).isEqualTo(product.getTitle());
            assertThat(it.getDescription()).isEqualTo(product.getDescription());
            assertThat(it.getPrice()).isEqualTo(product.getPrice());
            assertThat(it.getCategoryId()).isEqualTo(product.getCategory().getId().toString());
            assertThat(it.getOwnerId()).isEqualTo(product.getOwner().id().toString());
        });

    }

    private static Product createProduct() {
        Owner owner = new Owner(UUID.randomUUID());
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .title("bakeries")
                .description("bakeries & cakes")
                .build();
        return Product.builder()
                .id(UUID.randomUUID())
                .title("apple pie")
                .description("delicious grandma's apple pie")
                .price(20.10)
                .category(category)
                .owner(owner)
                .build();
    }

    private static ProductRequest createProductRequest() {
        return new ProductRequest(
                UUID.randomUUID().toString(),
                "apple pie",
                "delicious grandma's apple pie",
                20.10);
    }
}