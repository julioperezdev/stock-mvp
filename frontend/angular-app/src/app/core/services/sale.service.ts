import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateSaleRequest, SaleResponse } from '../../shared/models/sale.model';

@Injectable({ providedIn: 'root' })
export class SaleService {
  private readonly baseUrl = `${environment.apiBaseUrl}/sales`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<SaleResponse[]> {
    return this.http.get<SaleResponse[]>(this.baseUrl);
  }

  create(request: CreateSaleRequest): Observable<SaleResponse> {
    return this.http.post<SaleResponse>(this.baseUrl, request);
  }
}
