# Especificación Frontend: Angular para MVP de Gestión de Stock

## 1. Objetivo del componente

Construir una interfaz web simple que permita a los usuarios de los locales registrar ventas, consultar stock y visualizar reportes básicos.

El frontend debe consumir la API del backend Spring Boot y mantenerse independiente del despliegue del backend.

## 2. Tecnología propuesta

- Angular.
- TypeScript.
- Angular Router.
- Reactive Forms.
- HttpClient.
- Vercel como opción de despliegue.
- Nginx como alternativa si se sirve desde EC2.

## 3. Alcance incluido

- Pantalla principal tipo dashboard.
- Pantalla para registrar ventas.
- Pantalla para consultar stock por local.
- Pantalla para ver productos con stock crítico.
- Servicios HTTP para consumir backend.
- Manejo básico de errores.
- Configuración de environments.
- Diseño simple y funcional.

## 4. Alcance excluido

- Login complejo.
- Gestión avanzada de roles.
- Diseño visual avanzado.
- Internacionalización.
- Offline mode.
- Manejo avanzado de estados con NgRx.
- Facturación o impresión de comprobantes.

## 5. Rutas iniciales

```text
/
  Dashboard general

/sales/new
  Registrar venta

/stock
  Consultar stock por local

/reports/low-stock
  Ver productos con stock crítico

/products
  Listar productos

/stores
  Listar locales
```

## 6. Estructura sugerida

```text
frontend/angular-app/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── services/
│   │   │   │   ├── product.service.ts
│   │   │   │   ├── store.service.ts
│   │   │   │   ├── stock.service.ts
│   │   │   │   └── sale.service.ts
│   │   │   └── interceptors/
│   │   │       └── error.interceptor.ts
│   │   ├── features/
│   │   │   ├── dashboard/
│   │   │   ├── sales/
│   │   │   ├── stock/
│   │   │   ├── reports/
│   │   │   ├── products/
│   │   │   └── stores/
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   └── models/
│   │   ├── app.routes.ts
│   │   └── app.config.ts
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   └── main.ts
├── package.json
└── vercel.json
```

## 7. Variables de entorno

### environment.ts

```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api'
};
```

### environment.prod.ts

```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.tu-dominio.com/api'
};
```

## 8. Modelos TypeScript

## Product

```typescript
export interface Product {
  id: number;
  sku: string;
  name: string;
  description?: string;
  active: boolean;
}
```

## Store

```typescript
export interface Store {
  id: number;
  name: string;
  code: string;
  active: boolean;
}
```

## StockItem

```typescript
export interface StockItem {
  productId: number;
  productName: string;
  storeId: number;
  storeName: string;
  currentQuantity: number;
  minimumQuantity: number;
  critical: boolean;
}
```

## CreateSaleRequest

```typescript
export interface CreateSaleRequest {
  storeId: number;
  items: CreateSaleItemRequest[];
}

export interface CreateSaleItemRequest {
  productId: number;
  quantity: number;
}
```

## SaleResponse

```typescript
export interface SaleResponse {
  saleId: number;
  storeId: number;
  createdAt: string;
  status: string;
  items: SaleResponseItem[];
}

export interface SaleResponseItem {
  productId: number;
  productName: string;
  quantity: number;
}
```

## 9. Servicios HTTP

## ProductService

Responsabilidad:

- Obtener productos activos.
- Obtener producto por ID.

Endpoints consumidos:

```text
GET /api/products
GET /api/products/{id}
```

## StoreService

Responsabilidad:

- Obtener locales activos.
- Obtener local por ID.

Endpoints consumidos:

```text
GET /api/stores
GET /api/stores/{id}
```

## StockService

Responsabilidad:

- Consultar stock por local.
- Consultar stock crítico.
- Consultar stock consolidado.

Endpoints consumidos:

```text
GET /api/stocks
GET /api/stocks?storeId={storeId}
GET /api/stocks/low-stock
```

## SaleService

Responsabilidad:

- Registrar ventas.
- Consultar ventas recientes.

Endpoints consumidos:

```text
POST /api/sales
GET /api/sales
GET /api/sales/{id}
```

## 10. Pantalla: Registrar venta

## Objetivo

Permitir que un usuario registre una venta de uno o varios productos para un local.

## Campos

- Local.
- Producto.
- Cantidad.
- Lista de productos agregados.
- Botón para confirmar venta.

## Validaciones

- Local requerido.
- Producto requerido.
- Cantidad mayor a cero.
- La lista debe tener al menos un producto.
- Mostrar error si el backend responde stock insuficiente.

## Flujo

1. Cargar locales.
2. Cargar productos.
3. Usuario selecciona local.
4. Usuario agrega productos a la venta.
5. Usuario confirma.
6. Frontend envía POST `/api/sales`.
7. Mostrar confirmación o error.

## 11. Pantalla: Consultar stock

## Objetivo

Visualizar el stock actual por local.

## Filtros

- Local.
- Producto.
- Solo stock crítico.

## Datos mostrados

- Producto.
- Local.
- Cantidad actual.
- Cantidad mínima.
- Estado: normal o crítico.

## 12. Pantalla: Reporte stock crítico

## Objetivo

Mostrar a la fábrica qué productos requieren reposición.

## Datos mostrados

- Local.
- Producto.
- Stock actual.
- Stock mínimo.
- Cantidad sugerida a reponer.

## Regla sugerida

```text
cantidadSugerida = minimumQuantity - currentQuantity
```

Si el resultado es menor o igual a cero, no se muestra como crítico.

## 13. Manejo de errores

El frontend debe manejar:

- Error 400: datos inválidos.
- Error 404: recurso no encontrado.
- Error 409: conflicto de negocio, por ejemplo stock insuficiente.
- Error 500: error inesperado.

Mensaje recomendado para stock insuficiente:

```text
No hay stock suficiente para completar la venta.
```

## 14. Criterios de aceptación

- El usuario puede registrar una venta.
- El usuario puede consultar stock por local.
- El usuario puede ver productos con stock crítico.
- Los errores del backend se muestran de forma entendible.
- La URL del backend puede cambiarse por environment.
- La app puede desplegarse independientemente del backend.

## 15. Comandos sugeridos

```bash
npm install
npm start
npm run build
```

## 16. Despliegue en Vercel

Archivo sugerido `vercel.json`:

```json
{
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

## 17. Consideraciones futuras

- Agregar autenticación.
- Agregar roles: local, fábrica, admin.
- Agregar gráficos.
- Agregar exportación CSV.
- Agregar alertas visuales.
- Agregar permisos por local.
