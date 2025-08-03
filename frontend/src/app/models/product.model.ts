export type {
  ProductResponse as Product,
  ProductRequest as ProductForm,
  ProductPagedResponse as PagedResponse,
  ProductQueryParams,
} from '../types';

export type {
  ProductFormData,
  CreateProductData,
  UpdateProductData,
} from '../schemas/product.schema';
export interface ProductLegacy {
  publicId?: string;
  name: string;
  price: number;
  quantity: number;
  description?: string;
}
