import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateStockRequest, StockItem, UpdateStockRequest } from '../../shared/models/stock.model';

@Injectable({ providedIn: 'root' })
export class StockService {
  private readonly baseUrl = `${environment.apiBaseUrl}/stocks`;

  constructor(private readonly http: HttpClient) {}

  list(filters: { storeId?: number; productId?: number } = {}): Observable<StockItem[]> {
    let params = new HttpParams();
    if (filters.storeId) {
      params = params.set('storeId', filters.storeId);
    }
    if (filters.productId) {
      params = params.set('productId', filters.productId);
    }
    return this.http.get<StockItem[]>(this.baseUrl, { params });
  }

  lowStock(): Observable<StockItem[]> {
    return this.http.get<StockItem[]>(`${this.baseUrl}/low-stock`);
  }

  create(request: CreateStockRequest): Observable<StockItem> {
    return this.http.post<StockItem>(this.baseUrl, request);
  }

  update(stockId: number, request: UpdateStockRequest): Observable<StockItem> {
    return this.http.put<StockItem>(`${this.baseUrl}/${stockId}`, request);
  }
}
