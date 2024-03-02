package com.bentoco.productcatalog.configurations.interfaces;

import com.bentoco.productcatalog.security.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControl {
    Role[] value();
}
