package br.com.productmanagementsystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for cross-field business rules validation on ProductRequestDTO
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProductBusinessRulesValidator.class)
@Documented
public @interface ValidProductBusinessRules {
    String message() default "{validation.product.businessrules}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}