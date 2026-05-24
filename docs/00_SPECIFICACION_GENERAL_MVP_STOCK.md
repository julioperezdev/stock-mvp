# Especificación General: MVP de Gestión de Stock para Locales y Fábrica

## 1. Contexto del negocio

El sistema está pensado para una empresa que trabaja con varios locales de venta y una fábrica o centro principal de producción/distribución.

Cada local necesita registrar ventas y mantener actualizado su stock. La fábrica necesita consultar el faltante consolidado de productos para planificar producción, reposición o distribución.

El objetivo inicial no es construir un ERP completo, sino un MVP simple que permita validar el flujo principal del negocio.

## 2. Problema que resuelve

Actualmente, el control de stock entre locales y fábrica puede depender de procesos manuales, mensajes, planillas o reportes informales. Esto genera:

- Falta de visibilidad centralizada.
- Dificultad para saber qué productos faltan en cada local.
- Errores al consolidar ventas y stock.
- Demoras para tomar decisiones de reposición.
- Poca trazabilidad sobre movimientos de productos.

## 3. Objetivo del MVP

Construir una aplicación básica que permita:

1. Registrar la venta de productos desde un local.
2. Descontar stock automáticamente.
3. Consultar el stock actual por local.
4. Generar un reporte diario simple de stock y faltantes.
5. Permitir que la fábrica visualice qué productos necesita reponer.

## 4. Alcance incluido

### Funcionalidades principales

- Gestión básica de productos.
- Gestión básica de locales.
- Registro de ventas.
- Descuento automático de stock.
- Consulta de stock por local.
- Reporte diario de stock.
- Reporte de productos con stock crítico.
- Logs básicos en CloudWatch.
- Backend desplegable en AWS.
- Frontend Angular desplegable en Vercel o servidor propio.
- Lambdas para tareas asíncronas simples.

### Componentes técnicos

- Frontend: Angular.
- Backend: Java + Spring Boot.
- Base de datos: PostgreSQL en local y RDS PostgreSQL en AWS.
- Reverse proxy: Nginx.
- Infraestructura inicial: EC2, Docker Compose, CloudWatch y AWS Lambda.
- Opcional para archivos futuros: S3.

## 5. Alcance excluido

Para mantener el MVP simple, quedan fuera:

- Login avanzado con roles complejos.
- Facturación fiscal.
- Integración con medios de pago.
- Gestión completa de caja.
- Gestión contable.
- Gestión de proveedores.
- Predicción automática de demanda.
- Machine Learning.
- Multi-tenant avanzado.
- Auditoría legal completa.
- Sistema avanzado de permisos.
- Integración con sistemas externos de inventario.

## 6. Casos de uso principales

## Caso de uso 1: Registrar venta de producto

### Actor principal

Usuario del local.

### Objetivo

Registrar una venta y descontar automáticamente la cantidad vendida del stock del local.

### Flujo principal

1. El usuario selecciona el local.
2. El usuario selecciona el producto.
3. El usuario ingresa la cantidad vendida.
4. El frontend envía la solicitud al backend.
5. El backend valida que exista stock suficiente.
6. El backend registra la venta.
7. El backend descuenta el stock.
8. El backend devuelve confirmación.
9. El frontend muestra el resultado.

### Reglas de negocio

- No se puede vender una cantidad mayor al stock disponible.
- Toda venta debe quedar registrada.
- Toda venta debe modificar el stock del local correspondiente.
- Si el stock queda por debajo del mínimo configurado, debe quedar disponible para el reporte de stock crítico.

## Caso de uso 2: Generar reporte diario de stock

### Actor principal

Fábrica o administrador.

### Objetivo

Consultar el stock disponible y detectar productos que requieren reposición.

### Flujo principal

1. El usuario accede al dashboard de reportes.
2. El frontend solicita el reporte al backend.
3. El backend consulta el stock actual.
4. El backend identifica productos por debajo del stock mínimo.
5. El backend devuelve el reporte consolidado.
6. El frontend muestra el reporte por local y producto.

### Variante con Lambda

1. Una Lambda se ejecuta diariamente.
2. La Lambda consulta el backend o la base de datos.
3. La Lambda genera un resumen del stock crítico.
4. La Lambda guarda logs en CloudWatch.
5. Opcionalmente, la Lambda puede enviar una notificación por email.

## 7. Arquitectura general

```text
Usuario
  |
  v
Frontend Angular
  |
  v
Nginx / API Gateway / Reverse Proxy
  |
  v
Backend Spring Boot
  |
  v
PostgreSQL / RDS

Eventos programados:
EventBridge
  |
  v
AWS Lambda
  |
  v
Backend Spring Boot o PostgreSQL
  |
  v
CloudWatch Logs
```

## 8. Estructura recomendada del monorepo

```text
stock-mvp/
├── frontend/
│   └── angular-app/
├── backend/
│   └── spring-boot-api/
├── lambdas/
│   ├── daily-stock-report/
│   └── low-stock-alert/
├── infra/
│   ├── docker-compose.yml
│   ├── nginx/
│   │   └── nginx.conf
│   └── cloud/
│       └── notes.md
├── docs/
│   ├── 00_SPECIFICACION_GENERAL_MVP_STOCK.md
│   ├── 01_SPECIFICACION_FRONTEND_ANGULAR.md
│   ├── 02_SPECIFICACION_BACKEND_SPRING_BOOT.md
│   └── 03_SPECIFICACION_LAMBDAS_AWS.md
└── README.md
```

## 9. Modelo de dominio inicial

### Entidades principales

- Product
- Store
- Stock
- Sale
- SaleItem
- StockMovement

## 10. Decisiones técnicas iniciales

## Frontend

- Angular.
- Formularios reactivos.
- Servicios HTTP.
- Dashboard simple.
- Deploy en Vercel o servidor propio.

## Backend

- Java 21.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- PostgreSQL.
- Validaciones con Bean Validation.
- Logs estructurados.
- Dockerfile propio.

## Base de datos

- PostgreSQL local para desarrollo.
- RDS PostgreSQL para ambiente AWS.
- Migraciones recomendadas con Flyway o Liquibase.

## Lambdas

- Node.js o Java.
- EventBridge para ejecución programada.
- CloudWatch para logs.
- Uso inicial: reportes y alertas.

## Nginx

- Reverse proxy para enrutar `/api` hacia Spring Boot.
- Posible servicio de frontend estático si no se usa Vercel.
- Configuración preparada para HTTPS en una etapa posterior.

## 11. Variables de entorno generales

```bash
APP_ENV=local
API_BASE_URL=http://localhost:8080
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=stock_mvp
POSTGRES_USER=stock_user
POSTGRES_PASSWORD=stock_password
AWS_REGION=us-east-1
```

## 12. Criterios de aceptación generales

El MVP se considera válido si:

- Un usuario puede registrar una venta desde el frontend.
- El backend descuenta correctamente el stock.
- El sistema impide ventas sin stock suficiente.
- Se puede consultar el stock por local.
- Se puede generar un reporte simple de stock.
- Los logs principales quedan registrados.
- Los componentes pueden ejecutarse localmente con Docker Compose.
- El frontend y backend pueden desplegarse de forma independiente.

## 13. Roadmap sugerido

## Iteración 1

- Crear entidades base.
- Crear endpoints CRUD mínimos.
- Registrar venta.
- Descontar stock.

## Iteración 2

- Crear dashboard Angular.
- Mostrar stock por local.
- Mostrar stock crítico.

## Iteración 3

- Agregar Lambda de reporte diario.
- Agregar logs en CloudWatch.
- Preparar despliegue en EC2.

## Iteración 4

- Agregar autenticación básica.
- Agregar roles simples.
- Mejorar reportes.
- Preparar notificaciones.
