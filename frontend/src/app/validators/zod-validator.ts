import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { z, ZodError } from 'zod';

/**
 * Creates an Angular validator from a Zod schema
 * Compatible with Angular 20+ and Zod 4.x
 */
export function zodValidator<T>(schema: z.ZodType<T>): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value && control.value !== 0) {
      return null; // Let required validator handle empty values
    }

    const result = schema.safeParse(control.value);

    if (result.success) {
      return null;
    }

    // Convert Zod errors to Angular validation errors
    const errors: ValidationErrors = {};

    result.error.issues.forEach((issue) => {
      const path = issue.path.join('.');
      const key = path || 'root';

      errors[key] = {
        zodError: issue.message,
        code: issue.code,
        received: issue.path.length > 0 ? control.value : undefined,
      };
    });

    return errors;
  };
}

/**
 * Creates an Angular validator for form groups from a Zod schema
 * Useful for cross-field validation
 */
export function zodFormValidator<T>(schema: z.ZodType<T>): ValidatorFn {
  return (formGroup: AbstractControl): ValidationErrors | null => {
    if (!formGroup.value) {
      return null;
    }

    const result = schema.safeParse(formGroup.value);

    if (result.success) {
      return null;
    }

    // Group errors by field for better UX
    const fieldErrors: Record<string, ValidationErrors> = {};
    const formErrors: ValidationErrors = {};

    result.error.issues.forEach((issue) => {
      if (issue.path.length > 0) {
        // Field-specific error
        const fieldName = issue.path[0].toString();
        if (!fieldErrors[fieldName]) {
          fieldErrors[fieldName] = {};
        }
        fieldErrors[fieldName][issue.code] = {
          zodError: issue.message,
          code: issue.code,
        };
      } else {
        // Form-level error
        formErrors[issue.code] = {
          zodError: issue.message,
          code: issue.code,
        };
      }
    });

    // Set field errors on individual controls
    Object.keys(fieldErrors).forEach((fieldName) => {
      const control = formGroup.get(fieldName);
      if (control) {
        control.setErrors({ ...control.errors, ...fieldErrors[fieldName] });
      }
    });

    // Return form-level errors
    return Object.keys(formErrors).length > 0 ? formErrors : null;
  };
}

/**
 * Utility to format Zod errors for display
 */
export function formatZodError(error: ZodError): string {
  return error.issues.map((issue) => issue.message).join('; ');
}

/**
 * Type-safe form value extractor using Zod schema
 */
export function extractTypedFormValue<T>(
  formGroup: AbstractControl,
  schema: z.ZodType<T>,
): T | null {
  const result = schema.safeParse(formGroup.value);
  return result.success ? result.data : null;
}
