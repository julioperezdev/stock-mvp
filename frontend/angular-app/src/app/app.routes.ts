import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ProductsComponent } from './features/products/products.component';
import { ReportsComponent } from './features/reports/reports.component';
import { SalesComponent } from './features/sales/sales.component';
import { StockComponent } from './features/stock/stock.component';
import { StoresComponent } from './features/stores/stores.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent, title: 'Dashboard' },
  { path: 'sales/new', component: SalesComponent, title: 'Registrar venta' },
  { path: 'stock', component: StockComponent, title: 'Stock' },
  { path: 'reports/low-stock', component: ReportsComponent, title: 'Stock critico' },
  { path: 'products', component: ProductsComponent, title: 'Productos' },
  { path: 'stores', component: StoresComponent, title: 'Locales' },
  { path: '**', redirectTo: '' }
];
