import { FormControl, FormGroup } from '@angular/forms';
import { zodValidator, zodFormValidator } from './zod-validator';
import { productSchema, productFieldSchemas } from '../schemas/product.schema';

describe('ZodValidator', () => {
  describe('zodValidator', () => {
    it('should return null for valid values', () => {
      const nameValidator = zodValidator(productFieldSchemas.name);
      const control = new FormControl('Valid Product Name');

      const result = nameValidator(control);

      expect(result).toBeNull();
    });

    it('should return validation errors for invalid values', () => {
      const nameValidator = zodValidator(productFieldSchemas.name);
      const control = new FormControl('AB'); // Too short

      const result = nameValidator(control);

      expect(result).not.toBeNull();
      expect(result?.['too_small']).toBeDefined();
      expect(result?.['too_small']['zodError']).toContain(
        'Name must be between 3 and 100 characters',
      );
    });

    it('should validate price with correct decimal precision', () => {
      const priceValidator = zodValidator(productFieldSchemas.price);
      const validControl = new FormControl(19.99);
      const invalidControl = new FormControl(19.999); // Too many decimals

      expect(priceValidator(validControl)).toBeNull();

      const result = priceValidator(invalidControl);
      expect(result).not.toBeNull();
      expect(result?.['not_multiple_of']).toBeDefined();
    });

    it('should validate quantity as integer', () => {
      const quantityValidator = zodValidator(productFieldSchemas.quantity);
      const validControl = new FormControl(50);
      const invalidControl = new FormControl(50.5); // Not an integer

      expect(quantityValidator(validControl)).toBeNull();

      const result = quantityValidator(invalidControl);
      expect(result).not.toBeNull();
      expect(result?.['not_integer']).toBeDefined();
    });

    it('should handle null/undefined values gracefully', () => {
      const nameValidator = zodValidator(productFieldSchemas.name);
      const nullControl = new FormControl(null);
      const undefinedControl = new FormControl(undefined);
      const emptyControl = new FormControl('');

      expect(nameValidator(nullControl)).toBeNull();
      expect(nameValidator(undefinedControl)).toBeNull();
      expect(nameValidator(emptyControl)).toBeNull();
    });
  });

  describe('zodFormValidator', () => {
    let formGroup: FormGroup;

    beforeEach(() => {
      formGroup = new FormGroup({
        name: new FormControl('Test Product'),
        price: new FormControl(15.99),
        quantity: new FormControl(50),
        description: new FormControl('Test description'),
      });
    });

    it('should return null for valid form data', () => {
      const validator = zodFormValidator(productSchema);

      const result = validator(formGroup);

      expect(result).toBeNull();
    });

    it('should validate business rule: low-value high-quantity', () => {
      const validator = zodFormValidator(productSchema);

      // Set price < 10 and quantity > 100 (violates business rule)
      formGroup.patchValue({
        price: 5.99,
        quantity: 150,
      });

      const result = validator(formGroup);

      expect(result).not.toBeNull();
      expect(result?.['custom']).toBeDefined();
      expect(result?.['custom']['zodError']).toContain('Low-value products');
    });

    it('should validate business rule: high-value high-quantity', () => {
      const validator = zodFormValidator(productSchema);

      // Set price > 10000 and quantity > 10 (violates business rule)
      formGroup.patchValue({
        price: 15000,
        quantity: 20,
      });

      const result = validator(formGroup);

      expect(result).not.toBeNull();
      expect(result?.['custom']).toBeDefined();
      expect(result?.['custom']['zodError']).toContain('High-value products');
    });

    it('should allow valid business rule combinations', () => {
      const validator = zodFormValidator(productSchema);

      // Test valid combinations
      const validCombinations = [
        { price: 5.99, quantity: 50 }, // Low price, acceptable quantity
        { price: 15000, quantity: 5 }, // High price, low quantity
        { price: 100, quantity: 500 }, // Mid-range price, any quantity
      ];

      validCombinations.forEach((combination) => {
        formGroup.patchValue(combination);
        const result = validator(formGroup);
        expect(result).toBeNull();
      });
    });

    it('should set field-specific errors on form controls', () => {
      const validator = zodFormValidator(productSchema);

      // Set invalid values
      formGroup.patchValue({
        name: 'AB', // Too short
        price: -10, // Negative price
        quantity: -5, // Negative quantity
      });

      validator(formGroup);

      // Check that field errors are set on individual controls
      expect(formGroup.get('name')?.errors).toBeTruthy();
      expect(formGroup.get('price')?.errors).toBeTruthy();
      expect(formGroup.get('quantity')?.errors).toBeTruthy();
    });
  });

  describe('Edge Cases', () => {
    it('should handle boundary values correctly', () => {
      const priceValidator = zodValidator(productFieldSchemas.price);
      const quantityValidator = zodValidator(productFieldSchemas.quantity);

      // Test minimum values
      expect(priceValidator(new FormControl(0.01))).toBeNull();
      expect(quantityValidator(new FormControl(0))).toBeNull();

      // Test maximum values
      expect(priceValidator(new FormControl(999999.99))).toBeNull();
      expect(quantityValidator(new FormControl(999999))).toBeNull();

      // Test values just outside boundaries
      expect(priceValidator(new FormControl(0.009))).not.toBeNull();
      expect(priceValidator(new FormControl(1000000))).not.toBeNull();
      expect(quantityValidator(new FormControl(-1))).not.toBeNull();
      expect(quantityValidator(new FormControl(1000000))).not.toBeNull();
    });

    it('should handle description field correctly', () => {
      const descSchema = productFieldSchemas.description;
      if (!descSchema) {
        fail('Description schema should be defined');
        return;
      }

      const descValidator = zodValidator(descSchema);

      // Empty description should be valid (optional)
      expect(descValidator(new FormControl(''))).toBeNull();
      expect(descValidator(new FormControl(null))).toBeNull();

      // Description within limit should be valid
      const validDesc = 'A'.repeat(500);
      expect(descValidator(new FormControl(validDesc))).toBeNull();

      // Description exceeding limit should be invalid
      const invalidDesc = 'A'.repeat(501);
      const result = descValidator(new FormControl(invalidDesc));
      expect(result).not.toBeNull();
      expect(result?.['too_big']).toBeDefined();
    });
  });
});
