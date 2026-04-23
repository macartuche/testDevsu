import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Error desconocido';

      if (error.error instanceof ErrorEvent) {
        errorMessage = error.error.message;
      } else {
        console.log('Status:', error.status, 'Error:', error.error);
        if (error.status === 0) {
          errorMessage = 'No se pudo conectar con el servidor';
        } else {
          errorMessage = error.error?.error || error.error?.details || error.message || `Error ${error.status}`;
        }
      }

      console.error('Error HTTP:', errorMessage, error);
      return throwError(() => new Error(errorMessage));
    })
  );
};