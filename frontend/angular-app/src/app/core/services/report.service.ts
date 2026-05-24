import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DailyStockReport, LowStockReportItem } from '../../shared/models/report.model';
import { StockItem } from '../../shared/models/stock.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly baseUrl = `${environment.apiBaseUrl}/reports`;

  constructor(private readonly http: HttpClient) {}

  dailyStock(): Observable<DailyStockReport> {
    return this.http.get<DailyStockReport>(`${this.baseUrl}/daily-stock`);
  }

  lowStock(): Observable<LowStockReportItem[]> {
    return this.http.get<LowStockReportItem[]>(`${this.baseUrl}/low-stock`);
  }

  stockByStore(storeId: number): Observable<StockItem[]> {
    return this.http.get<StockItem[]>(`${this.baseUrl}/stock-by-store/${storeId}`);
  }
}
