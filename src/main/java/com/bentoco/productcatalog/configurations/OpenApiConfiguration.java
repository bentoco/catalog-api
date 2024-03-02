package com.bentoco.productcatalog.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenApi() {
        Info productCatalogueInfo = new Info()
                .title("product-catalog")
                .description("Product management backend service.")
                .version("1.0.0");
        return new OpenAPI().info(productCatalogueInfo);
    }

}