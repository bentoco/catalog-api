package com.bentoco.catalog.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public abstract class SecurityException extends RuntimeException {
    public SecurityException(String message) {
        super(message);
    }
}
