import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { LoginComponent } from './features/login/login.component';
import { ProductsComponent } from './features/products/products.component';
import { ReportsComponent } from './features/reports/reports.component';
import { SalesComponent } from './features/sales/sales.component';
import { StockComponent } from './features/stock/stock.component';
import { StoresComponent } from './features/stores/stores.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, title: 'Login' },
  { path: '', component: DashboardComponent, title: 'Dashboard', canActivate: [authGuard] },
  {
    path: 'sales/new',
    component: SalesComponent,
    title: 'Registrar venta',
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'STORE_USER'] }
  },
  { path: 'stock', component: StockComponent, title: 'Stock', canActivate: [authGuard] },
  {
    path: 'reports/low-stock',
    component: ReportsComponent,
    title: 'Stock critico',
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'FACTORY_USER'] }
  },
  {
    path: 'products',
    component: ProductsComponent,
    title: 'Productos',
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'stores',
    component: StoresComponent,
    title: 'Locales',
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },
  { path: '**', redirectTo: '' }
];
