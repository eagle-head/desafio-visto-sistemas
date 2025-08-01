export interface Product {
  publicId?: string;
  name: string;
  price: number;
  quantity: number;
  description?: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
