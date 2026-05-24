# Stock Locales Fabrica

MVP de gestion de stock para locales de venta y fabrica/centro de distribucion.

El objetivo del proyecto es validar el flujo principal del negocio:

- Crear productos y locales.
- Cargar stock por producto/local.
- Registrar ventas desde un local.
- Descontar stock automaticamente.
- Detectar stock critico.
- Consultar reportes basicos para fabrica.

## Stack

- Frontend: Angular + TypeScript.
- Backend: Java 21 + Spring Boot.
- Base de datos: PostgreSQL.
- Infraestructura local: Docker Compose + Nginx.
- Automatizaciones futuras: AWS Lambda + EventBridge + CloudWatch Logs.

## Estructura del monorepo

```text
backend/
  spring-boot-api/

frontend/
  angular-app/

lambdas/
  daily-stock-report/
  low-stock-alert/

infra/
  docker-compose.yml
  nginx/
  cloud/

docs/
  00_SPECIFICACION_GENERAL_MVP_STOCK.md
  01_SPECIFICACION_FRONTEND_ANGULAR.md
  02_SPECIFICACION_BACKEND_SPRING_BOOT.md
  03_SPECIFICACION_LAMBDAS_AWS.md
```

## Requisitos locales

- Docker Desktop o Docker daemon activo.
- Java 21 o superior.
- Maven.
- Node compatible con Angular 21.

En esta maquina, Maven puede tomar Java 17 por defecto. Para evitarlo, usar:

```bash
export JAVA_HOME=/Users/julio/Library/Java/JavaVirtualMachines/corretto-25.0.2/Contents/Home
```

El Node global detectado es `20.1.0`, pero Angular 21 requiere `20.19` o superior. Los comandos del frontend usan Node 22 via `npx` para no tocar la instalacion global.

## Levantar PostgreSQL

Desde la raiz del repo:

```bash
docker compose -f infra/docker-compose.yml up -d postgres
```

PostgreSQL queda disponible en:

```text
localhost:5432
database: stock_mvp
user: stock_user
password: stock_password
```

## Levantar Backend

```bash
cd backend/spring-boot-api
JAVA_HOME=/Users/julio/Library/Java/JavaVirtualMachines/corretto-25.0.2/Contents/Home mvn spring-boot:run -Dspring-boot.run.profiles=local
```

API local:

```text
http://localhost:8080/api
```

El backend tiene CORS habilitado para:

```text
http://localhost:4200
http://127.0.0.1:4200
```

Usuarios locales creados por migracion:

```text
admin@stock.local / admin123      rol: ADMIN
local@stock.local / local123      rol: STORE_USER
fabrica@stock.local / fabrica123  rol: FACTORY_USER
```

## Levantar Frontend

```bash
cd frontend/angular-app
npm install
npx -p node@22 node ./node_modules/@angular/cli/bin/ng serve --host 0.0.0.0 --port 4200
```

Frontend local:

```text
http://localhost:4200
```

El frontend consume:

```text
http://localhost:8080/api
```

## Build

Backend:

```bash
cd backend/spring-boot-api
JAVA_HOME=/Users/julio/Library/Java/JavaVirtualMachines/corretto-25.0.2/Contents/Home mvn test
```

Frontend:

```bash
cd frontend/angular-app
npx -p node@22 node ./node_modules/@angular/cli/bin/ng build
```

Build local de frontend contra `environment.ts`:

```bash
cd frontend/angular-app
npx -p node@22 node ./node_modules/@angular/cli/bin/ng build --configuration development
```

## Prueba End-to-End

Con PostgreSQL, backend y frontend levantados:

```bash
cd frontend/angular-app
npx playwright test e2e/closed-features.spec.ts --reporter=line --workers=1
```

La prueba cubre:

- Crear producto desde la UI.
- Crear local desde la UI.
- Crear stock inicial.
- Registrar venta.
- Ver descuento de stock.
- Ver stock critico.
- Ver reporte de stock critico.
- Validar error de stock insuficiente.

## Endpoints principales

Autenticacion:

```text
POST /api/auth/login
GET  /api/auth/me
```

Todos los endpoints de negocio requieren JWT en el header:

```text
Authorization: Bearer <token>
```

Productos:

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

Locales:

```text
GET    /api/stores
GET    /api/stores/{id}
POST   /api/stores
PUT    /api/stores/{id}
DELETE /api/stores/{id}
```

Stock:

```text
GET  /api/stocks
GET  /api/stocks/{id}
GET  /api/stocks?storeId={storeId}
GET  /api/stocks?productId={productId}
GET  /api/stocks/low-stock
POST /api/stocks
PUT  /api/stocks/{id}
```

Ventas:

```text
POST /api/sales
GET  /api/sales
GET  /api/sales/{id}
```

Reportes:

```text
GET /api/reports/daily-stock
GET /api/reports/low-stock
GET /api/reports/stock-by-store/{storeId}
```

## Estado del MVP

Listo:

- Backend Spring Boot con entidades JPA.
- Migraciones Flyway para PostgreSQL.
- CRUD minimo de productos y locales.
- Carga y consulta de stock.
- Registro transaccional de ventas.
- Descuento automatico de stock.
- Movimientos de stock por venta.
- Reporte diario.
- Reporte de stock critico.
- Manejo centralizado de errores.
- Frontend Angular funcional.
- Autenticacion JWT con roles `ADMIN`, `STORE_USER` y `FACTORY_USER`.
- Prueba E2E de flujo principal.

Pendiente:

- Lambdas AWS.
- Edicion avanzada desde frontend.
- Tests unitarios/integracion mas completos.
- Seed data formal.
- Preparacion de despliegue cloud.
