package com.bentoco.productcatalog.controller.exception;

public record BadRequestResponse(String field, String message) {
}
