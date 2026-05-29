package com.skymat.ecommerce.handler;

import com.skymat.ecommerce.exception.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomerNotFoundException.class})
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var errors = new HashMap<String, String>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            var fieldName = ((FieldError) error).getField();
            var message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(errors));
    }
}
