import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  MatPaginator,
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import {
  takeUntil,
  catchError,
  finalize,
  debounceTime,
  distinctUntilChanged,
} from 'rxjs/operators';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { NotificationService } from '../../services/notification.service';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { PriceUtils } from '../../utils/price.utils';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatInputModule,
    MatFormFieldModule,
    MatSnackBarModule,
  ],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss'],
})
export class ProductListComponent implements OnInit, OnDestroy {
  readonly displayedColumns: string[] = [
    'name',
    'price',
    'quantity',
    'description',
    'actions',
  ];
  dataSource = new MatTableDataSource<Product>([]);
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;
  loading = false;
  searchName = '';
  private searchSubject = new Subject<string>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private readonly destroy$ = new Subject<void>();
  private readonly productService = inject(ProductService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.loadProducts(0, this.pageSize);
    this.setupSearchSubscription();
  }

  private setupSearchSubscription(): void {
    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.currentPage = 0;
        this.loadProducts(0, this.pageSize);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadProducts(event.pageIndex, event.pageSize);
  }

  private loadProducts(pageIndex: number, pageSize: number): void {
    this.loading = true;
    this.cdr.detectChanges();

    const searchName = this.searchName?.trim() || undefined;

    this.productService
      .getProducts({
        page: pageIndex,
        size: pageSize,
        sort: ['id,asc'],
        name: searchName
      })
      .pipe(
        takeUntil(this.destroy$),
        catchError((error) => {
          this.notificationService.showError(
            'Failed to load products. Please try again.',
          );
          console.error('Error loading products:', error);
          return [
            {
              content: [],
              totalElements: 0,
              totalPages: 0,
              size: pageSize,
              number: pageIndex,
            },
          ];
        }),
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        }),
      )
      .subscribe((response) => {
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;

        if (this.paginator) {
          this.paginator.length = this.totalElements;
          this.paginator.pageIndex = pageIndex;
          this.paginator.pageSize = pageSize;
        }
      });
  }

  protected addProduct(): void {
    this.router.navigate(['/products/new']);
  }

  protected editProduct(product: Product): void {
    this.router.navigate(['/products/edit', product.publicId]);
  }

  protected deleteProduct(product: Product): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Deletion',
        message: `Are you sure you want to delete the product "${product.name}"?`,
      },
    });

    dialogRef
      .afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe((result) => {
        if (result && product.publicId) {
          this.loading = true;
          this.cdr.detectChanges();

          this.productService
            .deleteProduct(product.publicId)
            .pipe(
              takeUntil(this.destroy$),
              catchError((error) => {
                this.loading = false;
                this.cdr.detectChanges();
                this.notificationService.showError(
                  `Failed to delete product "${product.name}". Please try again.`,
                );
                console.error('Error deleting product:', error);
                return [];
              }),
            )
            .subscribe(() => {
              this.loadProducts(this.currentPage, this.pageSize);
              this.notificationService.showSuccess(
                `Product "${product.name}" deleted successfully!`,
              );
            });
        }
      });
  }

  protected formatPrice(price: number): string {
    return PriceUtils.formatPrice(price);
  }

  protected onSearchInput(): void {
    this.searchSubject.next(this.searchName);
  }

  protected clearSearch(): void {
    this.searchName = '';
    this.searchSubject.next('');
  }
}
