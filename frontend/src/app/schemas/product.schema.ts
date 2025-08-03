import { z } from 'zod';

/**
 * Product validation schema that mirrors backend ProductRequestDTO validations
 * Includes all Bean Validation constraints and business rules
 */
export const productSchema = z
  .object({
    name: z
      .string()
      .trim()
      .min(3, 'Name must be between 3 and 100 characters')
      .max(100, 'Name must be between 3 and 100 characters')
      .refine((val) => val.length > 0, 'Name is required'),

    price: z
      .number({
        message: 'Price must be a valid number',
      })
      .min(0.01, 'Price must be greater than zero')
      .max(999999.99, 'Price cannot exceed 999,999.99')
      .multipleOf(0.01, 'Price must have at most 2 decimal places'),

    quantity: z
      .number({
        message: 'Quantity must be a valid number',
      })
      .int('Quantity must be a whole number')
      .min(0, 'Quantity cannot be negative')
      .max(999999, 'Quantity cannot exceed 999,999'),

    description: z
      .string()
      .max(500, 'Description cannot exceed 500 characters')
      .optional()
      .or(z.literal('')), // Allow empty string
  })
  .refine(
    (data) => {
      // Business rule: Low-value products (price < 10) cannot have high quantity (> 100)
      if (data.price < 10 && data.quantity > 100) {
        return false;
      }
      return true;
    },
    {
      message:
        'Low-value products (price < 10) cannot have quantity greater than 100',
      path: ['quantity'], // Associate error with quantity field
    },
  )
  .refine(
    (data) => {
      // Business rule: High-value products (price > 10000) must have limited quantity (<= 10)
      if (data.price > 10000 && data.quantity > 10) {
        return false;
      }
      return true;
    },
    {
      message:
        'High-value products (price > 10000) must have quantity less than or equal to 10',
      path: ['quantity'], // Associate error with quantity field
    },
  );

/**
 * Schema for product creation (excludes publicId)
 */
export const createProductSchema = productSchema;

/**
 * Schema for product updates (all fields optional)
 */
export const updateProductSchema = productSchema.partial();

/**
 * Schema for product response (includes publicId)
 */
export const productResponseSchema = productSchema.extend({
  publicId: z.string().uuid('Invalid product ID format').optional(),
});

/**
 * Individual field schemas for granular validation
 */
export const productFieldSchemas = {
  name: z
    .string()
    .trim()
    .min(3, 'Name must be between 3 and 100 characters')
    .max(100, 'Name must be between 3 and 100 characters'),

  price: z
    .number()
    .min(0.01, 'Price must be greater than zero')
    .max(999999.99, 'Price cannot exceed 999,999.99')
    .multipleOf(0.01, 'Price must have at most 2 decimal places'),

  quantity: z
    .number()
    .int('Quantity must be a whole number')
    .min(0, 'Quantity cannot be negative')
    .max(999999, 'Quantity cannot exceed 999,999'),

  description: z
    .string()
    .max(500, 'Description cannot exceed 500 characters')
    .optional(),
};

/**
 * Type inference from schemas
 */
export type ProductFormData = z.infer<typeof productSchema>;
export type CreateProductData = z.infer<typeof createProductSchema>;
export type UpdateProductData = z.infer<typeof updateProductSchema>;
export type ProductResponseData = z.infer<typeof productResponseSchema>;

/**
 * Validation error messages mapping
 */
export const productValidationMessages = {
  name: {
    required: 'Name is required',
    minLength: 'Name must be at least 3 characters',
    maxLength: 'Name cannot exceed 100 characters',
  },
  price: {
    required: 'Price is required',
    min: 'Price must be greater than zero',
    max: 'Price cannot exceed 999,999.99',
    multipleOf: 'Price must have at most 2 decimal places',
    type: 'Price must be a valid number',
  },
  quantity: {
    required: 'Quantity is required',
    min: 'Quantity cannot be negative',
    max: 'Quantity cannot exceed 999,999',
    int: 'Quantity must be a whole number',
    type: 'Quantity must be a valid number',
  },
  description: {
    maxLength: 'Description cannot exceed 500 characters',
  },
  businessRules: {
    lowValueHighQuantity:
      'Low-value products (price < 10) cannot have quantity greater than 100',
    highValueHighQuantity:
      'High-value products (price > 10000) must have quantity less than or equal to 10',
  },
} as const;
