/**
 * Types for error handling based on RFC 7807 Problem Details
 * Compatible with backend GlobalExceptionHandler responses
 */

import type {
  ProblemDetail as APIProblemDetail,
  ValidationError as APIValidationError,
} from './api-response.types';
export type ProblemDetail = APIProblemDetail;
export type ValidationFieldError = APIValidationError;

/**
 * Field error map for frontend validation handling
 */
export type FieldErrorMap = Record<string, string>;

/**
 * Categorized error types based on backend responses
 */
export type ErrorType =
  | 'client'
  | 'validation'
  | 'business_rule'
  | 'bad_request'
  | 'not_found'
  | 'conflict'
  | 'server_error'
  | 'parsing_error'
  | 'constraint_violation'
  | 'type_mismatch'
  | 'missing_parameter'
  | 'unknown';

/**
 * Processed error for frontend consumption
 */
export interface ProcessedError {
  message: string;
  type: ErrorType;
  fieldErrors?: FieldErrorMap;
  status?: number;
  originalError?: ProblemDetail;
}

/**
 * Enhanced error object thrown by interceptor
 */
export interface EnhancedError extends Error {
  status: number;
  details: ProcessedError;
  originalError: unknown;
}

/**
 * Error type mapping based on backend problem types
 */
export const ERROR_TYPE_MAP: Record<string, ErrorType> = {
  'validation-error': 'validation',
  'product-not-found': 'not_found',
  'product-already-exists': 'conflict',
  'resource-not-found': 'not_found',
  'invalid-argument': 'bad_request',
  'constraint-violation': 'constraint_violation',
  'type-mismatch': 'type_mismatch',
  'database-constraint-violation': 'conflict',
  'parsing-error': 'parsing_error',
  'missing-parameter': 'missing_parameter',
  'internal-server-error': 'server_error',
} as const;

export { isProblemDetail } from './api-response.types';

/**
 * Type guard to check if error has validation errors
 */
export function hasValidationErrors(
  error: ProblemDetail,
): error is ProblemDetail & { errors: ValidationFieldError[] } {
  return (
    error.errors !== undefined &&
    Array.isArray(error.errors) &&
    error.errors.length > 0
  );
}

/**
 * Type guard to check if error has a message property
 */
export function hasMessage(error: unknown): error is { message: string } {
  return typeof error === 'object' && error !== null && 'message' in error;
}

/**
 * Type guard for enhanced error
 */
export function isEnhancedError(error: unknown): error is EnhancedError {
  return error instanceof Error && 'status' in error && 'details' in error;
}

/**
 * Extract error type from problem detail type URI
 */
export function extractErrorType(typeUri: string): ErrorType {
  const typeKey = typeUri.split('/').pop() || 'unknown';
  return ERROR_TYPE_MAP[typeKey] || 'unknown';
}

/**
 * Convert validation errors array to field error map
 */
export function validationErrorsToFieldMap(
  errors: ValidationFieldError[],
): FieldErrorMap {
  return errors.reduce((acc, error) => {
    acc[error.field || 'general'] = error.message;
    return acc;
  }, {} as FieldErrorMap);
}
