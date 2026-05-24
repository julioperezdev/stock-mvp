import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateStoreRequest, Store } from '../../shared/models/store.model';

@Injectable({ providedIn: 'root' })
export class StoreService {
  private readonly baseUrl = `${environment.apiBaseUrl}/stores`;

  constructor(private readonly http: HttpClient) {}

  list(): Observable<Store[]> {
    return this.http.get<Store[]>(this.baseUrl);
  }

  create(request: CreateStoreRequest): Observable<Store> {
    return this.http.post<Store>(this.baseUrl, request);
  }
}
