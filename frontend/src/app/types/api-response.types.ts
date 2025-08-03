/**
 * Precise API response types based on backend DTOs and test expectations
 */

/**
 * Product response as returned by backend ProductResponseDTO
 */
export interface ProductResponse {
  publicId: string;
  name: string;
  price: number; // Backend returns BigDecimal as number in JSON
  description: string | null; // Can be null from backend
  quantity: number;
}

/**
 * Product request payload for creating/updating products
 * Based on backend ProductRequestDTO
 */
export interface ProductRequest {
  name: string;
  price: number;
  description?: string; // Optional in request
  quantity: number;
}

/**
 * Paginated response structure as returned by Spring Data
 * Based on controller test expectations
 */
export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // Current page number (0-based)
  first?: boolean;
  last?: boolean;
  numberOfElements?: number;
  empty?: boolean;
}

/**
 * Product paginated response
 */
export type ProductPagedResponse = PagedResponse<ProductResponse>;

/**
 * Query parameters for product filtering
 * Based on backend ProductQueryDTO
 */
export interface ProductQueryParams {
  name?: string;
  minPrice?: number;
  maxPrice?: number;
  minQuantity?: number;
  maxQuantity?: number;
  page?: number;
  size?: number;
  sort?: string[];
}

/**
 * Error response structures based on GlobalExceptionHandler
 */

/**
 * Individual validation error in RFC 7807 format
 */
export interface ValidationError {
  field: string;
  message: string;
  invalidValue?: string;
}

/**
 * RFC 7807 Problem Detail response
 * Exact structure returned by GlobalExceptionHandler
 */
export interface ProblemDetail {
  type: string; // URI like "https://api.productmanagement.com.br/validation-error"
  title: string; // Human-readable title
  status: number; // HTTP status code
  detail: string; // Human-readable explanation
  instance?: string; // URI reference to specific occurrence
  errors?: ValidationError[]; // Field validation errors
  productId?: string; // For product-specific errors
  productName?: string; // For product-specific errors
}

/**
 * Success response wrapper (if used)
 */
export interface ApiSuccessResponse<T> {
  data: T;
  message?: string;
  timestamp?: string;
}

/**
 * Type guards for runtime type checking
 */

export function isProductResponse(obj: unknown): obj is ProductResponse {
  if (typeof obj !== 'object' || obj === null) {
    return false;
  }

  const candidate = obj as Record<string, unknown>;

  return (
    'publicId' in candidate &&
    typeof candidate['publicId'] === 'string' &&
    'name' in candidate &&
    typeof candidate['name'] === 'string' &&
    'price' in candidate &&
    typeof candidate['price'] === 'number' &&
    'quantity' in candidate &&
    typeof candidate['quantity'] === 'number' &&
    ('description' in candidate
      ? candidate['description'] === null ||
        typeof candidate['description'] === 'string'
      : true)
  );
}

export function isPagedResponse<T>(obj: unknown): obj is PagedResponse<T> {
  if (typeof obj !== 'object' || obj === null) {
    return false;
  }

  const candidate = obj as Record<string, unknown>;

  return (
    'content' in candidate &&
    Array.isArray(candidate['content']) &&
    'totalElements' in candidate &&
    typeof candidate['totalElements'] === 'number' &&
    'totalPages' in candidate &&
    typeof candidate['totalPages'] === 'number' &&
    'size' in candidate &&
    typeof candidate['size'] === 'number' &&
    'number' in candidate &&
    typeof candidate['number'] === 'number'
  );
}

export function isProblemDetail(obj: unknown): obj is ProblemDetail {
  if (typeof obj !== 'object' || obj === null) {
    return false;
  }

  const candidate = obj as Record<string, unknown>;

  return (
    'type' in candidate &&
    typeof candidate['type'] === 'string' &&
    'title' in candidate &&
    typeof candidate['title'] === 'string' &&
    'status' in candidate &&
    typeof candidate['status'] === 'number' &&
    'detail' in candidate &&
    typeof candidate['detail'] === 'string'
  );
}

/**
 * HTTP method types for API calls
 */
export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

/**
 * API endpoint paths (based on controller mappings)
 */
export const API_ENDPOINTS = {
  PRODUCTS: '/products',
  PRODUCT_BY_ID: (publicId: string) => `/products/${publicId}`,
} as const;

/**
 * HTTP status codes commonly returned by the API
 */
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  NOT_FOUND: 404,
  CONFLICT: 409,
  UNPROCESSABLE_ENTITY: 422,
  INTERNAL_SERVER_ERROR: 500,
} as const;
