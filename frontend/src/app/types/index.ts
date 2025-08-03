/**
 * Centralized type exports to avoid circular dependencies and naming conflicts
 */

// API Response Types
export type {
  ProductResponse,
  ProductRequest,
  ProductPagedResponse,
  ProductQueryParams,
  ValidationError,
  ProblemDetail,
  PagedResponse,
  ApiSuccessResponse,
  HttpMethod,
} from './api-response.types';

export {
  isProductResponse,
  isPagedResponse,
  isProblemDetail,
  API_ENDPOINTS,
  HTTP_STATUS,
} from './api-response.types';

// Error Types
export type {
  ErrorType,
  ProcessedError,
  EnhancedError,
  FieldErrorMap,
  ValidationFieldError,
} from './error.types';

export {
  hasValidationErrors,
  extractErrorType,
  validationErrorsToFieldMap,
  isEnhancedError,
  hasMessage,
  ERROR_TYPE_MAP,
} from './error.types';
