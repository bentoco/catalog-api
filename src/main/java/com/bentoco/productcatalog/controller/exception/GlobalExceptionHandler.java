package com.bentoco.productcatalog.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultErrorResponse<List<BadRequestResponse>>> customHandleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();
        List<BadRequestResponse> badRequestResponseList =  errors.stream()
                .map(error -> new BadRequestResponse(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new DefaultErrorResponse<>("ERR001", badRequestResponseList));
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(value = DynamoDbOperationsErrorException.class)
    public ResponseEntity<DefaultErrorResponse<String>> customHandleDynamoDbOperationsError(DynamoDbOperationsErrorException exception) {
        return ResponseEntity.unprocessableEntity().body(new DefaultErrorResponse<>("ERR002", exception.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<DefaultErrorResponse<String>> customHandleSecurityException(SecurityException exception) {
        return ResponseEntity.status(403).body(new DefaultErrorResponse<>("ERR003", exception.getMessage()));
    }
}
