import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

export type NotificationType = 'success' | 'error' | 'info' | 'warning';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly snackBar = inject(MatSnackBar);

  showSuccess(message: string, duration = 3000): void {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['success-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  showError(message: string, duration = 5000): void {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['error-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  showInfo(message: string, duration = 3000): void {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['info-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  showWarning(message: string, duration = 4000): void {
    this.snackBar.open(message, 'Close', {
      duration,
      panelClass: ['warning-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  show(
    message: string,
    type: NotificationType = 'info',
    duration?: number,
  ): void {
    switch (type) {
      case 'success':
        this.showSuccess(message, duration);
        break;
      case 'error':
        this.showError(message, duration);
        break;
      case 'warning':
        this.showWarning(message, duration);
        break;
      default:
        this.showInfo(message, duration);
        break;
    }
  }
}
