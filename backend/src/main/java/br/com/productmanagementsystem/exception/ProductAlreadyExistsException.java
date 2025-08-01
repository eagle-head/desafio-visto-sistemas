package br.com.productmanagementsystem.exception;

import lombok.Getter;

@Getter
public class ProductAlreadyExistsException extends RuntimeException {
    private final String productName;
    
    public ProductAlreadyExistsException(String productName) {
        super(); // Don't use hardcoded message - will be handled by GlobalExceptionHandler
        this.productName = productName;
    }
}