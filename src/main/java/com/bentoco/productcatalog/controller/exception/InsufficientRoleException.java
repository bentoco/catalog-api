package com.bentoco.productcatalog.controller.exception;

public class InsufficientRoleException extends SecurityException {
    public InsufficientRoleException(String message) {
        super(message);
    }
}
