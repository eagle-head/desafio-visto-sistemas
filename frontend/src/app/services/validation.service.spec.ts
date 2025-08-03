import { TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { ValidationService } from './validation.service';

describe('ValidationService', () => {
  let service: ValidationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ValidationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('validateProductBusinessRules', () => {
    it('should return null for valid price-quantity combinations', () => {
      const validCombinations = [
        { price: 5.99, quantity: 50 }, // Low price, acceptable quantity
        { price: 15000, quantity: 5 }, // High price, low quantity
        { price: 100, quantity: 500 }, // Mid-range price, any quantity
        { price: 9.99, quantity: 100 }, // Edge case: price < 10, quantity = 100
      ];

      validCombinations.forEach(({ price, quantity }) => {
        const result = service.validateProductBusinessRules(price, quantity);
        expect(result).toBeNull();
      });
    });

    it('should detect low-value high-quantity violation', () => {
      const result = service.validateProductBusinessRules(5.99, 150);

      expect(result).not.toBeNull();
      expect(result!['lowValueHighQuantity']).toBeDefined();
      expect(result!['lowValueHighQuantity']['rule']).toBe(
        'LOW_VALUE_HIGH_QUANTITY',
      );
      expect(result!['lowValueHighQuantity']['maxAllowedQuantity']).toBe(100);
    });

    it('should detect high-value high-quantity violation', () => {
      const result = service.validateProductBusinessRules(15000, 20);

      expect(result).not.toBeNull();
      expect(result!['highValueHighQuantity']).toBeDefined();
      expect(result!['highValueHighQuantity']['rule']).toBe(
        'HIGH_VALUE_HIGH_QUANTITY',
      );
      expect(result!['highValueHighQuantity']['maxAllowedQuantity']).toBe(10);
    });

    it('should detect multiple violations simultaneously', () => {
      // This is a theoretical case - a product can't be both low and high value
      // But we test the service logic for completeness
      const lowValueHighQuantity = service.validateProductBusinessRules(5, 150);
      const highValueHighQuantity = service.validateProductBusinessRules(
        15000,
        20,
      );

      expect(lowValueHighQuantity).not.toBeNull();
      expect(highValueHighQuantity).not.toBeNull();
    });
  });

  describe('validateProductForm', () => {
    it('should validate complete valid form data', () => {
      const validFormData = {
        name: 'Test Product',
        price: 19.99,
        quantity: 50,
        description: 'A test product',
      };

      const result = service.validateProductForm(validFormData);

      expect(result.isValid).toBe(true);
      expect(result.data).toEqual(validFormData);
      expect(result.errors).toBeUndefined();
    });

    it('should detect invalid form data', () => {
      const invalidFormData = {
        name: 'AB', // Too short
        price: -10, // Negative
        quantity: 50.5, // Not integer
        description: 'A'.repeat(501), // Too long
      };

      const result = service.validateProductForm(invalidFormData);

      expect(result.isValid).toBe(false);
      expect(result.data).toBeUndefined();
      expect(result.errors).toBeDefined();
      expect(Object.keys(result.errors!).length).toBeGreaterThan(0);
    });
  });

  describe('validateField', () => {
    it('should validate individual fields correctly', () => {
      // Valid name
      expect(service.validateField('name', 'Valid Name')).toBeNull();

      // Invalid name (too short)
      const nameError = service.validateField('name', 'AB');
      expect(nameError).not.toBeNull();
      expect(nameError!['too_small']).toBeDefined();

      // Valid price
      expect(service.validateField('price', 19.99)).toBeNull();

      // Invalid price (negative)
      const priceError = service.validateField('price', -10);
      expect(priceError).not.toBeNull();
      expect(priceError!['too_small']).toBeDefined();
    });

    it('should handle unknown field names', () => {
      const result = service.validateField('unknownField' as any, 'value');

      expect(result).not.toBeNull();
      expect(result!['invalidField']).toBeDefined();
    });
  });

  describe('getBusinessRuleRecommendations', () => {
    it('should provide recommendations for risky combinations', () => {
      // Low-value product with high quantity
      const recommendations1 = service.getBusinessRuleRecommendations(5.99, 75);
      expect(recommendations1.length).toBeGreaterThan(0);
      expect(recommendations1[0]).toContain('low-value products');

      // High-value product with high quantity
      const recommendations2 = service.getBusinessRuleRecommendations(8000, 25);
      expect(recommendations2.length).toBeGreaterThan(0);
      expect(recommendations2[0]).toContain('High-value products');

      // Luxury item with too much quantity
      const recommendations3 = service.getBusinessRuleRecommendations(
        12000,
        15,
      );
      expect(recommendations3.length).toBeGreaterThan(0);
      expect(recommendations3[0]).toContain('Luxury items');
    });

    it('should return empty array for good combinations', () => {
      const recommendations = service.getBusinessRuleRecommendations(50.99, 30);
      expect(recommendations).toEqual([]);
    });

    it('should handle null values', () => {
      expect(service.getBusinessRuleRecommendations(null, 30)).toEqual([]);
      expect(service.getBusinessRuleRecommendations(50, null)).toEqual([]);
      expect(service.getBusinessRuleRecommendations(null, null)).toEqual([]);
    });
  });

  describe('validatePricePrecision', () => {
    it('should validate price precision correctly', () => {
      expect(service.validatePricePrecision(19.99)).toBe(true);
      expect(service.validatePricePrecision(19.9)).toBe(true);
      expect(service.validatePricePrecision(19)).toBe(true);
      expect(service.validatePricePrecision(19.999)).toBe(false);
    });
  });

  describe('formatValidationError', () => {
    it('should format single error', () => {
      const error = {
        field1: { message: 'Error message' },
      };

      const result = service.formatValidationError(error);
      expect(result).toBe('Error message');
    });

    it('should format multiple errors', () => {
      const error = {
        field1: { message: 'Error 1' },
        field2: { message: 'Error 2' },
      };

      const result = service.formatValidationError(error);
      expect(result).toContain('Error 1');
      expect(result).toContain('Error 2');
      expect(result).toContain(';');
    });

    it('should handle null/undefined errors', () => {
      expect(service.formatValidationError(null as any)).toBe('');
    });
  });

  describe('getFieldErrors', () => {
    it('should extract field errors from form group', () => {
      const formGroup = new FormGroup({
        name: new FormControl(''),
        price: new FormControl(''),
      });

      // Set errors manually for testing
      formGroup.get('name')?.setErrors({ required: true });

      const errors = service.getFieldErrors(formGroup, 'name');
      expect(errors).toEqual({ required: true });

      const noErrors = service.getFieldErrors(formGroup, 'price');
      expect(noErrors).toBeNull();
    });
  });

  describe('hasBusinessRuleViolations', () => {
    it('should detect business rule violations in form', () => {
      const formGroup = new FormGroup({
        name: new FormControl('Test'),
        price: new FormControl(5),
        quantity: new FormControl(150),
      });

      // Set business rule error
      formGroup.setErrors({
        lowValueHighQuantity: { message: 'Business rule violation' },
      });

      const result = service.hasBusinessRuleViolations(formGroup);
      expect(result).toBe(true);
    });

    it('should return false when no violations exist', () => {
      const formGroup = new FormGroup({
        name: new FormControl('Test'),
        price: new FormControl(50),
        quantity: new FormControl(30),
      });

      const result = service.hasBusinessRuleViolations(formGroup);
      expect(result).toBe(false);
    });
  });

  describe('getErrorMessage', () => {
    it('should return appropriate messages for error codes', () => {
      expect(service.getErrorMessage('too_small', { minimum: 10 })).toContain(
        'at least 10',
      );
      expect(service.getErrorMessage('too_big', { maximum: 100 })).toContain(
        'cannot exceed 100',
      );
      expect(service.getErrorMessage('invalid_type')).toContain(
        'Invalid data type',
      );
      expect(service.getErrorMessage('required')).toContain('required');
      expect(service.getErrorMessage('unknown_code')).toContain(
        'Validation error occurred',
      );
    });
  });
});
