import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import {
  ProcessedError,
  EnhancedError,
  ErrorType,
  isProblemDetail,
  hasValidationErrors,
  extractErrorType,
  validationErrorsToFieldMap,
  FieldErrorMap,
} from '../types';

/**
 * Enhanced error interceptor that handles RFC 7807 Problem Details from backend
 * and maintains compatibility with Zod validation
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unknown error occurred';
      let processedError: ProcessedError = {
        message: errorMessage,
        type: 'unknown',
        status: error.status,
      };

      if (error.error instanceof ErrorEvent) {
        errorMessage = `Error: ${error.error.message}`;
        processedError = {
          message: errorMessage,
          type: 'client',
          status: 0,
        };
      } else if (isProblemDetail(error.error)) {
        const problemDetail = error.error;
        errorMessage = problemDetail.detail || problemDetail.title;
        const errorType = extractErrorType(problemDetail.type);

        processedError = {
          message: errorMessage,
          type: errorType,
          status: problemDetail.status,
          originalError: problemDetail,
        };

        if (hasValidationErrors(problemDetail)) {
          processedError.fieldErrors = validationErrorsToFieldMap(
            problemDetail.errors,
          );
          errorMessage = 'Validation failed: Please check the form fields';
          processedError.message = errorMessage;
        }
      } else {
        processedError = handleLegacyError(error, errorMessage);
        errorMessage = processedError.message;
      }

      console.group('🚨 HTTP Error Details');
      console.error('Status:', error.status);
      console.error('URL:', error.url);
      console.error('Method:', req.method);
      console.error('Processed Error:', processedError);
      console.error('Original Error:', error);
      console.groupEnd();
      const enhancedError = new Error(errorMessage) as EnhancedError;
      enhancedError.status = error.status;
      enhancedError.details = processedError;
      enhancedError.originalError = error;

      return throwError(() => enhancedError);
    }),
  );

  function handleLegacyError(
    error: HttpErrorResponse,
    defaultMessage: string,
  ): ProcessedError {
    let errorMessage = defaultMessage;
    let errorType: ErrorType = 'unknown';
    let fieldErrors: FieldErrorMap | undefined;

    switch (error.status) {
      case 400:
        errorType = 'bad_request';
        if (error.error?.errors && Array.isArray(error.error.errors)) {
          fieldErrors = error.error.errors.reduce(
            (
              acc: FieldErrorMap,
              err: {
                field?: string;
                message?: string;
                defaultMessage?: string;
              },
            ) => {
              acc[err.field || 'general'] =
                err.message || err.defaultMessage || 'Validation error';
              return acc;
            },
            {} as FieldErrorMap,
          );
          errorMessage = 'Validation failed: Please check the form fields';
          errorType = 'validation';
        } else if (error.error?.message) {
          errorMessage = error.error.message;
          errorType = 'business_rule';
        } else {
          errorMessage = 'Bad Request: Invalid data provided';
        }
        break;

      case 404:
        errorType = 'not_found';
        errorMessage =
          error.error?.message || error.error?.detail || 'Resource not found';
        break;

      case 409:
        errorType = 'conflict';
        errorMessage =
          error.error?.message ||
          error.error?.detail ||
          'Resource already exists';
        break;

      case 422:
        errorType = 'validation';
        errorMessage =
          error.error?.message ||
          error.error?.detail ||
          'Invalid data provided';
        if (error.error?.errors) {
          fieldErrors = error.error.errors;
        }
        break;

      case 500:
        errorType = 'server_error';
        errorMessage = 'Internal server error occurred';
        break;

      default:
        errorMessage = `Error ${error.status}: ${error.error?.message || error.message}`;
        errorType = 'unknown';
    }

    return {
      message: errorMessage,
      type: errorType,
      status: error.status,
      fieldErrors,
      originalError: error.error,
    };
  }
};
