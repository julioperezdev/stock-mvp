export interface Product {
  id: number;
  sku: string;
  name: string;
  description?: string;
  active: boolean;
}

export interface CreateProductRequest {
  sku: string;
  name: string;
  description?: string;
}