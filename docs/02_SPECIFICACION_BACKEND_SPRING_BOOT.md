# Especificación Backend: Spring Boot API para MVP de Gestión de Stock

## 1. Objetivo del componente

Construir una API REST en Spring Boot que centralice la lógica de negocio del MVP:

- Productos.
- Locales.
- Stock.
- Ventas.
- Movimientos de stock.
- Reportes básicos.

El backend debe ser la fuente principal de reglas de negocio y persistencia.

## 2. Tecnología propuesta

- Java 21.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Bean Validation.
- PostgreSQL.
- Flyway o Liquibase.
- Docker.
- CloudWatch Logs.
- Nginx como reverse proxy.
- AWS RDS para ambiente cloud.

## 3. Alcance incluido

- CRUD mínimo de productos.
- CRUD mínimo de locales.
- Consulta de stock.
- Registro de ventas.
- Descuento automático de stock.
- Registro de movimientos de stock.
- Reporte de stock crítico.
- Reporte diario simple.
- Logs estructurados.
- Manejo centralizado de errores.
- Dockerfile.
- Configuración para PostgreSQL local y RDS.

## 4. Alcance excluido

- Autenticación avanzada.
- Permisos complejos.
- Facturación.
- Integración con pasarelas de pago.
- Contabilidad.
- Procesamiento de archivos.
- Predicción de demanda.
- Multi-tenant avanzado.

## 5. Arquitectura interna sugerida

```text
controller
  |
  v
application / usecase
  |
  v
domain
  |
  v
repository / infrastructure
  |
  v
database
```

## 6. Estructura de paquetes sugerida

```text
backend/spring-boot-api/
├── src/main/java/com/example/stockmvp/
│   ├── StockMvpApplication.java
│   ├── product/
│   │   ├── domain/
│   │   ├── application/
│   │   ├── infrastructure/
│   │   └── controller/
│   ├── store/
│   ├── stock/
│   ├── sale/
│   ├── report/
│   ├── shared/
│   │   ├── error/
│   │   ├── logging/
│   │   └── validation/
│   └── config/
├── src/main/resources/
│   ├── application.yml
│   ├── application-local.yml
│   ├── application-prod.yml
│   └── db/migration/
├── Dockerfile
└── pom.xml
```

## 7. Modelo de base de datos

## Tabla: products

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Tabla: stores

```sql
CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Tabla: stocks

```sql
CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    current_quantity INTEGER NOT NULL,
    minimum_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_stocks_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stocks_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT uk_stocks_product_store UNIQUE (product_id, store_id),
    CONSTRAINT chk_stock_current_quantity CHECK (current_quantity >= 0),
    CONSTRAINT chk_stock_minimum_quantity CHECK (minimum_quantity >= 0)
);
```

## Tabla: sales

```sql
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_sales_store FOREIGN KEY (store_id) REFERENCES stores(id)
);
```

## Tabla: sale_items

```sql
CREATE TABLE sale_items (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_sale_items_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
    CONSTRAINT fk_sale_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_sale_item_quantity CHECK (quantity > 0)
);
```

## Tabla: stock_movements

```sql
CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    sale_id BIGINT,
    movement_type VARCHAR(30) NOT NULL,
    quantity INTEGER NOT NULL,
    previous_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stock_movements_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT fk_stock_movements_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
    CONSTRAINT chk_stock_movement_quantity CHECK (quantity > 0)
);
```

## 8. Entidades principales

## Product

Atributos:

- id
- sku
- name
- description
- active
- createdAt
- updatedAt

## Store

Atributos:

- id
- code
- name
- active
- createdAt
- updatedAt

## Stock

Atributos:

- id
- product
- store
- currentQuantity
- minimumQuantity
- createdAt
- updatedAt

## Sale

Atributos:

- id
- store
- status
- createdAt
- createdBy
- items

## SaleItem

Atributos:

- id
- sale
- product
- quantity

## StockMovement

Atributos:

- id
- product
- store
- sale
- movementType
- quantity
- previousQuantity
- newQuantity
- createdAt
- createdBy

## 9. Endpoints REST

## Products

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

## Stores

```text
GET    /api/stores
GET    /api/stores/{id}
POST   /api/stores
PUT    /api/stores/{id}
DELETE /api/stores/{id}
```

## Stocks

```text
GET /api/stocks
GET /api/stocks/{id}
GET /api/stocks?storeId={storeId}
GET /api/stocks?productId={productId}
GET /api/stocks/low-stock
PUT /api/stocks/{id}
```

## Sales

```text
POST /api/sales
GET  /api/sales
GET  /api/sales/{id}
```

## Reports

```text
GET /api/reports/daily-stock
GET /api/reports/low-stock
GET /api/reports/stock-by-store/{storeId}
```

## 10. DTOs principales

## CreateSaleRequest

```json
{
  "storeId": 1,
  "items": [
    {
      "productId": 10,
      "quantity": 2
    }
  ]
}
```

## CreateSaleResponse

```json
{
  "saleId": 100,
  "storeId": 1,
  "status": "CONFIRMED",
  "createdAt": "2026-05-23T10:30:00",
  "items": [
    {
      "productId": 10,
      "productName": "Producto A",
      "quantity": 2
    }
  ]
}
```

## StockResponse

```json
{
  "stockId": 1,
  "productId": 10,
  "productName": "Producto A",
  "storeId": 1,
  "storeName": "Local Centro",
  "currentQuantity": 5,
  "minimumQuantity": 10,
  "critical": true
}
```

## 11. Reglas de negocio

## Registro de venta

- La venta debe pertenecer a un local existente y activo.
- Cada producto debe existir y estar activo.
- La cantidad debe ser mayor a cero.
- Debe existir stock para el producto en el local.
- No se puede vender más de la cantidad disponible.
- Al confirmar la venta:
  - Se crea un registro en `sales`.
  - Se crean registros en `sale_items`.
  - Se actualiza `stocks.current_quantity`.
  - Se crea un registro en `stock_movements`.

## Stock crítico

Un producto se considera crítico cuando:

```text
currentQuantity <= minimumQuantity
```

## Movimiento de stock por venta

Para una venta, el movimiento debe tener:

```text
movementType = SALE
quantity = cantidad vendida
previousQuantity = stock antes de la venta
newQuantity = stock luego de la venta
```

## 12. Manejo de errores

## Error de validación

HTTP 400.

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Invalid request data",
  "details": [
    {
      "field": "storeId",
      "message": "storeId is required"
    }
  ]
}
```

## Stock insuficiente

HTTP 409.

```json
{
  "code": "INSUFFICIENT_STOCK",
  "message": "No hay stock suficiente para completar la venta",
  "details": {
    "productId": 10,
    "requestedQuantity": 5,
    "availableQuantity": 2
  }
}
```

## Recurso no encontrado

HTTP 404.

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Product not found"
}
```

## 13. Logs estructurados

## Eventos mínimos a loguear

- Inicio de registro de venta.
- Venta confirmada.
- Venta rechazada por stock insuficiente.
- Consulta de reporte diario.
- Consulta de stock crítico.
- Error inesperado.

## Formato recomendado

```text
event=sale_created saleId=100 storeId=1 status=CONFIRMED items=2
event=insufficient_stock storeId=1 productId=10 requestedQuantity=5 availableQuantity=2
event=low_stock_report_generated totalItems=8
```

## 14. Configuración Spring Boot

## application-local.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/stock_mvp
    username: stock_user
    password: stock_password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
```

## application-prod.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
```

## 15. Dockerfile sugerido

```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 16. Docker Compose sugerido

```yaml
services:
  postgres:
    image: postgres:16
    container_name: stock-mvp-postgres
    environment:
      POSTGRES_DB: stock_mvp
      POSTGRES_USER: stock_user
      POSTGRES_PASSWORD: stock_password
    ports:
      - "5432:5432"
    volumes:
      - stock_postgres_data:/var/lib/postgresql/data

  backend:
    build: ./backend/spring-boot-api
    container_name: stock-mvp-backend
    environment:
      SPRING_PROFILES_ACTIVE: local
      POSTGRES_HOST: postgres
      POSTGRES_PORT: 5432
      POSTGRES_DB: stock_mvp
      POSTGRES_USER: stock_user
      POSTGRES_PASSWORD: stock_password
    ports:
      - "8080:8080"
    depends_on:
      - postgres

  nginx:
    image: nginx:alpine
    container_name: stock-mvp-nginx
    ports:
      - "80:80"
    volumes:
      - ./infra/nginx/nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - backend

volumes:
  stock_postgres_data:
```

## 17. Nginx sugerido

```nginx
events {}

http {
    server {
        listen 80;

        location /api/ {
            proxy_pass http://backend:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

## 18. Criterios de aceptación

- El backend inicia correctamente.
- Las migraciones crean las tablas.
- Se pueden crear productos.
- Se pueden crear locales.
- Se puede configurar stock inicial.
- Se puede registrar una venta.
- La venta descuenta stock.
- No se permite vender más stock del disponible.
- Se registran movimientos de stock.
- Se puede consultar stock crítico.
- Se generan logs relevantes.

## 19. Consideraciones futuras

- Agregar Spring Security.
- Agregar JWT.
- Agregar roles: ADMIN, STORE_USER, FACTORY_USER.
- Agregar paginación.
- Agregar Criteria o Specification para filtros.
- Agregar pruebas con Testcontainers.
- Agregar métricas con Actuator.
- Agregar trazabilidad con requestId.
- Agregar integración con S3 para reportes exportados.
