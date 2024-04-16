package com.bentoco.catalog.controller.exception;

public record BadRequestResponse(String field, String message) {
}
