import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  ProductResponse,
  ProductRequest,
  ProductPagedResponse,
  ProductQueryParams,
  API_ENDPOINTS,
} from '../types';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}${API_ENDPOINTS.PRODUCTS}`;
  private http = inject(HttpClient);

  getProducts(
    queryParams: ProductQueryParams = {},
  ): Observable<ProductPagedResponse> {
    let params = new HttpParams();

    params = params.set('page', (queryParams.page ?? 0).toString());
    params = params.set('size', (queryParams.size ?? 10).toString());

    if (queryParams.sort) {
      queryParams.sort.forEach((sortParam) => {
        params = params.append('sort', sortParam);
      });
    } else {
      params = params.set('sort', 'id,asc');
    }
    if (queryParams.name?.trim()) {
      params = params.set('name', queryParams.name.trim());
    }
    if (queryParams.minPrice !== undefined) {
      params = params.set('minPrice', queryParams.minPrice.toString());
    }
    if (queryParams.maxPrice !== undefined) {
      params = params.set('maxPrice', queryParams.maxPrice.toString());
    }
    if (queryParams.minQuantity !== undefined) {
      params = params.set('minQuantity', queryParams.minQuantity.toString());
    }
    if (queryParams.maxQuantity !== undefined) {
      params = params.set('maxQuantity', queryParams.maxQuantity.toString());
    }

    return this.http.get<ProductPagedResponse>(this.apiUrl, { params });
  }

  getProduct(publicId: string): Observable<ProductResponse> {
    return this.http.get<ProductResponse>(
      `${environment.apiUrl}${API_ENDPOINTS.PRODUCT_BY_ID(publicId)}`,
    );
  }

  createProduct(product: ProductRequest): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(this.apiUrl, product);
  }

  updateProduct(
    publicId: string,
    product: ProductRequest,
  ): Observable<ProductResponse> {
    return this.http.put<ProductResponse>(
      `${environment.apiUrl}${API_ENDPOINTS.PRODUCT_BY_ID(publicId)}`,
      product,
    );
  }

  deleteProduct(publicId: string): Observable<void> {
    return this.http.delete<void>(
      `${environment.apiUrl}${API_ENDPOINTS.PRODUCT_BY_ID(publicId)}`,
    );
  }
}
