import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subject, of } from 'rxjs';
import { takeUntil, catchError } from 'rxjs/operators';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss'],
})
export class ProductFormComponent implements OnInit, OnDestroy {
  productForm!: FormGroup;
  isEditMode = false;
  productId: string | null = null;
  loading = false;
  submitting = false;

  private readonly destroy$ = new Subject<void>();
  private readonly fb = inject(FormBuilder);
  private readonly productService = inject(ProductService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  initForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required]],
      price: ['', [Validators.required, Validators.min(0)]],
      quantity: ['', [Validators.required, Validators.min(0)]],
      description: [''],
    });
  }

  checkEditMode(): void {
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe((params) => {
      if (params['id']) {
        this.isEditMode = true;
        this.productId = params['id'];
        this.loadProduct();
      }
    });
  }

  loadProduct(): void {
    if (this.productId) {
      this.loading = true;
      this.productService
        .getProduct(this.productId)
        .pipe(
          takeUntil(this.destroy$),
          catchError((error) => {
            console.error('Error loading product:', error);
            this.loading = false;
            this.router.navigate(['/products']);
            return of(null);
          }),
        )
        .subscribe((product) => {
          this.loading = false;
          if (product) {
            this.productForm.patchValue(product);
          }
        });
    }
  }

  protected onSubmit(): void {
    if (this.productForm.valid && !this.submitting) {
      this.submitting = true;
      const product: Product = this.productForm.value;

      if (this.isEditMode && this.productId) {
        this.productService
          .updateProduct(this.productId, product)
          .pipe(
            takeUntil(this.destroy$),
            catchError((error) => {
              console.error('Error updating product:', error);
              this.submitting = false;
              return of(null);
            }),
          )
          .subscribe((result) => {
            this.submitting = false;
            if (result !== null) {
              this.router.navigate(['/products']);
            }
          });
      } else {
        this.productService
          .createProduct(product)
          .pipe(
            takeUntil(this.destroy$),
            catchError((error) => {
              console.error('Error creating product:', error);
              this.submitting = false;
              return of(null);
            }),
          )
          .subscribe((result) => {
            this.submitting = false;
            if (result !== null) {
              this.router.navigate(['/products']);
            }
          });
      }
    }
  }

  protected onCancel(): void {
    this.router.navigate(['/products']);
  }
}
