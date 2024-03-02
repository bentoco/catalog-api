package com.bentoco.productcatalog.controller.exception;

public record DefaultErrorResponse<T>(String code, T data) {
}