package com.bentoco.catalog.mappers;

import com.bentoco.catalog.controller.request.ProductRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class ProductMapperTest {

    private final static ProductMapper mapper = ProductMapper.INSTANCE;

    @Test
    public void testToModelMethod() {
        ProductRequest productRequest = createProductRequest();

        var result = mapper.toModel(productRequest, UUID.randomUUID().toString());

        assertThat(result).isNotNull().satisfies(it -> {
            assertThat(it.getPk()).isNotBlank();
            assertThat(it.getSk()).isNotBlank();
            assertThat(it.getTitle()).isEqualTo(productRequest.title());
            assertThat(it.getDescription()).isEqualTo(productRequest.description());
            assertThat(it.getPrice()).isEqualTo(productRequest.price());
            assertThat(it.getCategoryId()).isNotBlank();
        });
    }

    private static ProductRequest createProductRequest() {
        return new ProductRequest(
                UUID.randomUUID().toString(),
                "apple pie",
                "delicious grandma's apple pie",
                20.10);
    }
}