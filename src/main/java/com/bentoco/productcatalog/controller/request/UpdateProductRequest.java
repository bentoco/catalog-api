package com.bentoco.productcatalog.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @NotBlank @Size(min = 5, max = 50) String title,
        @NotBlank @Size(min = 5, max = 200) String description,
        @NotNull @Positive Double price
) {
}
