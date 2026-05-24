export interface CreateSaleRequest {
  storeId: number;
  createdBy?: string;
  items: CreateSaleItemRequest[];
}

export interface CreateSaleItemRequest {
  productId: number;
  quantity: number;
}

export interface SaleResponse {
  saleId: number;
  storeId: number;
  storeName: string;
  createdAt: string;
  status: string;
  items: SaleResponseItem[];
}

export interface SaleResponseItem {
  productId: number;
  productName: string;
  quantity: number;
}
