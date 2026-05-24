import { StockItem } from './stock.model';

export interface DailyStockReport {
  totalProducts: number;
  totalStores: number;
  totalStockItems: number;
  criticalItems: number;
  generatedAt: string;
  items: StockItem[];
}

export interface LowStockReportItem {
  stockId: number;
  productId: number;
  productName: string;
  storeId: number;
  storeName: string;
  currentQuantity: number;
  minimumQuantity: number;
  suggestedReplenishment: number;
}

export interface ApiErrorResponse {
  code: string;
  message: string;
  details?: unknown;
  timestamp?: string;
}
