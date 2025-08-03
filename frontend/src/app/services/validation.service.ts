import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import {
  productSchema,
  productFieldSchemas,
  ProductFormData,
} from '../schemas/product.schema';

/**
 * Centralized validation service that provides business logic validation
 * and integrates with Zod schemas
 */
@Injectable({
  providedIn: 'root',
})
export class ValidationService {
  /**
   * Validates product business rules based on price and quantity
   */
  validateProductBusinessRules(
    price: number,
    quantity: number,
  ): ValidationErrors | null {
    const errors: ValidationErrors = {};

    // Business rule: Low-value products (price < 10) cannot have high quantity (> 100)
    if (price < 10 && quantity > 100) {
      errors['lowValueHighQuantity'] = {
        message:
          'Low-value products (price < 10) cannot have quantity greater than 100',
        rule: 'LOW_VALUE_HIGH_QUANTITY',
        price,
        quantity,
        maxAllowedQuantity: 100,
      };
    }

    // Business rule: High-value products (price > 10000) must have limited quantity (<= 10)
    if (price > 10000 && quantity > 10) {
      errors['highValueHighQuantity'] = {
        message:
          'High-value products (price > 10000) must have quantity less than or equal to 10',
        rule: 'HIGH_VALUE_HIGH_QUANTITY',
        price,
        quantity,
        maxAllowedQuantity: 10,
      };
    }

    return Object.keys(errors).length > 0 ? errors : null;
  }

  /**
   * Validates a complete product form using Zod schema
   */
  validateProductForm(formData: unknown): {
    isValid: boolean;
    data?: ProductFormData;
    errors?: ValidationErrors;
  } {
    const result = productSchema.safeParse(formData);

    if (result.success) {
      return { isValid: true, data: result.data };
    }

    const errors: ValidationErrors = {};
    result.error.issues.forEach((issue) => {
      const path = issue.path.join('.') || 'root';
      errors[path] = {
        message: issue.message,
        code: issue.code,
        path: issue.path,
      };
    });

    return { isValid: false, errors };
  }

  /**
   * Validates individual field values
   */
  validateField(
    fieldName: keyof typeof productFieldSchemas,
    value: unknown,
  ): ValidationErrors | null {
    const schema = productFieldSchemas[fieldName];
    if (!schema) {
      return { invalidField: { message: `Unknown field: ${fieldName}` } };
    }

    const result = schema.safeParse(value);
    if (result.success) {
      return null;
    }

    const errors: ValidationErrors = {};
    result.error.issues.forEach((issue) => {
      errors[issue.code] = {
        message: issue.message,
        code: issue.code,
        received: value,
      };
    });

    return errors;
  }

  /**
   * Gets business rule recommendations based on current form values
   */
  getBusinessRuleRecommendations(
    price: number | null,
    quantity: number | null,
  ): string[] {
    const recommendations: string[] = [];

    if (price !== null && quantity !== null) {
      if (price < 10 && quantity > 50) {
        recommendations.push(
          'Consider reducing quantity for low-value products to improve inventory turnover',
        );
      }

      if (price > 5000 && quantity > 20) {
        recommendations.push(
          'High-value products typically have lower quantities in stock',
        );
      }

      if (price > 10000 && quantity > 10) {
        recommendations.push(
          'Luxury items should have limited quantities (max 10 units)',
        );
      }
    }

    return recommendations;
  }

  /**
   * Validates price precision (2 decimal places)
   */
  validatePricePrecision(price: number): boolean {
    const decimalPlaces = price.toString().split('.')[1]?.length || 0;
    return decimalPlaces <= 2;
  }

  /**
   * Formats validation error messages for display
   */
  formatValidationError(error: ValidationErrors): string {
    if (!error) return '';

    const messages = Object.values(error)
      .map((err) =>
        typeof err === 'object' && err !== null && 'message' in err
          ? err.message
          : err,
      )
      .filter((msg) => typeof msg === 'string');

    return messages.join('; ');
  }

  /**
   * Extracts field-specific errors from form group
   */
  getFieldErrors(
    form: AbstractControl,
    fieldName: string,
  ): ValidationErrors | null {
    const field = form.get(fieldName);
    if (!field || !field.errors) {
      return null;
    }

    return field.errors;
  }

  /**
   * Checks if form has any business rule violations
   */
  hasBusinessRuleViolations(form: AbstractControl): boolean {
    if (!form.errors) return false;

    const businessRuleErrors = [
      'lowValueHighQuantity',
      'highValueHighQuantity',
      'custom',
    ];
    return businessRuleErrors.some((errorKey) => form.errors![errorKey]);
  }

  /**
   * Gets user-friendly error message for specific validation codes
   */
  getErrorMessage(
    errorCode: string,
    context?: {
      minimum?: number;
      maximum?: number;
      multipleOf?: number;
      message?: string;
    },
  ): string {
    const errorMessages: Record<string, string> = {
      too_small: context?.minimum
        ? `Value must be at least ${context.minimum}`
        : 'Value is too small',
      too_big: context?.maximum
        ? `Value cannot exceed ${context.maximum}`
        : 'Value is too large',
      invalid_type: 'Invalid data type provided',
      not_multiple_of: context?.multipleOf
        ? `Value must be a multiple of ${context.multipleOf}`
        : 'Invalid decimal precision',
      not_integer: 'Value must be a whole number',
      custom: context?.message || 'Business rule violation',
      required: 'This field is required',
    };

    return errorMessages[errorCode] || 'Validation error occurred';
  }
}
