package com.bentoco.catalog.core.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public sealed class AbstractModel permits Category, Product {
}

