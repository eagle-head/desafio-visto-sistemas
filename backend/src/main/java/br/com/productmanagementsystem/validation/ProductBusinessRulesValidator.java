package br.com.productmanagementsystem.validation;

import br.com.productmanagementsystem.dto.ProductRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Validator for cross-field business rules on ProductRequestDTO
 */
public class ProductBusinessRulesValidator implements ConstraintValidator<ValidProductBusinessRules, ProductRequestDTO> {
    
    @Override
    public boolean isValid(ProductRequestDTO product, ConstraintValidatorContext context) {
        if (product == null) {
            return true;
        }
        
        // Business rule: Low-value products (price < 10) cannot have high quantity (> 100)
        // This prevents inventory overload of low-margin items
        if (product.price() != null && product.quantity() != null) {
            if (product.price().compareTo(new BigDecimal("10.00")) < 0 && product.quantity() > 100) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Low-value products (price < 10) cannot have quantity greater than 100"
                ).addConstraintViolation();
                return false;
            }
        }
        
        // Business rule: High-value products (price > 10000) must have limited quantity (<= 10)
        // This is for exclusive/luxury items
        if (product.price() != null && product.quantity() != null) {
            if (product.price().compareTo(new BigDecimal("10000.00")) > 0 && product.quantity() > 10) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "High-value products (price > 10000) must have quantity less than or equal to 10"
                ).addConstraintViolation();
                return false;
            }
        }
        
        return true;
    }
}