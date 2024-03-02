package com.bentoco.productcatalog.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UUID;

public record ProductRequest(
      @NotBlank @UUID String ownerId,
      @NotBlank @UUID String categoryId,
      @NotBlank @Size(min = 5, max = 50) String title,
      @NotBlank @Size(min = 5, max = 200) String description,
      @NotNull @Positive Double price
) {}
