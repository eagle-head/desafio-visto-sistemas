import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, PagedResponse } from '../models/product.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/v1/products';
  private http = inject(HttpClient);

  getProducts(
    page = 0,
    size = 10,
    sort = 'id,asc',
    name?: string,
  ): Observable<PagedResponse<Product>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (name && name.trim()) {
      params = params.set('name', name.trim());
    }

    return this.http.get<PagedResponse<Product>>(this.apiUrl, { params });
  }

  getProduct(publicId: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${publicId}`);
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  updateProduct(publicId: string, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${publicId}`, product);
  }

  deleteProduct(publicId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${publicId}`);
  }
}
