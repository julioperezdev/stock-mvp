export interface StockItem {
  stockId: number;
  productId: number;
  productName: string;
  storeId: number;
  storeName: string;
  currentQuantity: number;
  minimumQuantity: number;
  critical: boolean;
}

export interface CreateStockRequest {
  productId: number;
  storeId: number;
  currentQuantity: number;
  minimumQuantity: number;
}

export interface UpdateStockRequest {
  currentQuantity: number;
  minimumQuantity: number;
}
