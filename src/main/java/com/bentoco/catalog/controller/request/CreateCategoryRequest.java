package com.bentoco.catalog.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank @Size(min = 5, max = 50) String title,
        @NotBlank @Size(min = 5, max = 200) String description
) { }
