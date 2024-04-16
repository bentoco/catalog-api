package com.bentoco.catalog.controller.exception;

public record DefaultErrorResponse<T>(String code, T data) {
}