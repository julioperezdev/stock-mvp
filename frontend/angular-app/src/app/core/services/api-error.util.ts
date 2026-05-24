import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorResponse } from '../../shared/models/report.model';

export function toUserMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const apiError = error.error as ApiErrorResponse | undefined;
    if (apiError?.code === 'INSUFFICIENT_STOCK') {
      return 'No hay stock suficiente para completar la venta.';
    }
    if (apiError?.message) {
      return apiError.message;
    }
    if (error.status === 0) {
      return 'No se pudo conectar con la API.';
    }
  }

  return 'Ocurrio un error inesperado.';
}
