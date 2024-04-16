package com.bentoco.catalog.controller.exception;

public class InsufficientRoleException extends SecurityException {
    public InsufficientRoleException(String message) {
        super(message);
    }
}
