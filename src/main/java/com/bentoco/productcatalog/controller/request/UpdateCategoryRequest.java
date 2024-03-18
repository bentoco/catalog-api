package com.bentoco.productcatalog.controller.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(max = 50) String title,
        @Size(max = 200) String description
) { }
