package com.bentoco.catalog.configurations.interfaces;

import com.bentoco.catalog.core.model.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControl {
    Role[] value();
}
