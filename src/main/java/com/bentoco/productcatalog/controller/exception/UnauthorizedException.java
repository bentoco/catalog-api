package com.bentoco.productcatalog.controller.exception;

public class UnauthorizedException extends SecurityException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
