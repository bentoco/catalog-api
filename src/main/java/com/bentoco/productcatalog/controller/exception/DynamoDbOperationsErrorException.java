package com.bentoco.productcatalog.controller.exception;

public class DynamoDbOperationsErrorException extends RuntimeException {
    public DynamoDbOperationsErrorException(String message) {
        super(message);
    }
}
