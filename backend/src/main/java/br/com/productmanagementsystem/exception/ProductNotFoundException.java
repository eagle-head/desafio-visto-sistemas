package br.com.productmanagementsystem.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final Long productId;
    
    public ProductNotFoundException(Long productId) {
        super(); // Don't use hardcoded message - will be handled by GlobalExceptionHandler
        this.productId = productId;
    }
}