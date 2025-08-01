import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unknown error occurred';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Server-side error
        switch (error.status) {
          case 400:
            errorMessage =
              'Bad Request: ' +
              (error.error?.message || 'Invalid data provided');
            break;
          case 404:
            errorMessage =
              'Not Found: ' + (error.error?.message || 'Resource not found');
            break;
          case 409:
            errorMessage =
              'Conflict: ' +
              (error.error?.message || 'Resource already exists');
            break;
          case 500:
            errorMessage =
              'Server Error: ' +
              (error.error?.message || 'Internal server error');
            break;
          default:
            errorMessage = `Error ${error.status}: ${error.error?.message || error.message}`;
        }
      }

      console.error('HTTP Error:', errorMessage);
      return throwError(() => new Error(errorMessage));
    }),
  );
};
