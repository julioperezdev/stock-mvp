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

## Base de datos

Las migraciones Flyway estan preparadas para una base nueva:

```text
V1__create_schema.sql
V2__insert_default_users.sql
```

`V1` crea todas las tablas del MVP, incluyendo autenticacion/autorizacion. `V2` carga solo los usuarios iniciales para login.

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

Si estas probando localmente con un volumen viejo de PostgreSQL, recrear la base desde cero:

```bash
docker compose -f infra/docker-compose.yml down -v
docker compose -f infra/docker-compose.yml up -d postgres
```

En un servidor nuevo no hace falta este paso porque la base arranca vacia.

## Deploy Manual En Servidor

Flujo esperado para un servidor nuevo:

```bash
git clone <repo-url>
cd <repo>
cp infra/.env.example infra/.env
```

Editar `infra/.env` antes de levantar produccion:

```text
POSTGRES_PASSWORD=...
JWT_SECRET=...
HTTP_PORT=80
```

Levantar todo:

```bash
./scripts/deploy.sh
```

Esto construye y levanta:

- PostgreSQL.
- Backend Spring Boot.
- Frontend Angular servido por Nginx.
- Proxy `/api` hacia el backend.

Desde tu PC deberias poder abrir:

```text
http://IP_DEL_SERVIDOR
```

La API queda disponible via el mismo host:

```text
http://IP_DEL_SERVIDOR/api
```

Probar el deploy desde el servidor:

```bash
./scripts/smoke-test.sh http://localhost
```

Ver logs:

```bash
./scripts/logs.sh
./scripts/logs.sh backend
./scripts/logs.sh nginx
./scripts/logs.sh postgres
```

Detener servicios:

```bash
./scripts/stop.sh
```

## Deploy Demo En Amazon Linux

Para una demo simple en una instancia EC2 con Amazon Linux, se puede usar este `user data`.

Este script instala Docker, Git y Docker Compose, clona el repo y crea `/home/ec2-user/run.sh`. El deploy no se ejecuta automaticamente en el arranque; despues de conectar por SSH se corre `./run.sh`.

```bash
#!/bin/bash
set -e

dnf update -y

dnf install -y docker git docker-compose-plugin

systemctl enable docker
systemctl start docker

usermod -aG docker ec2-user

cd /home/ec2-user

if [ ! -d "/home/ec2-user/stock-mvp" ]; then
  git clone https://github.com/julioperezdev/stock-mvp.git
fi

cat << 'EOF' > /home/ec2-user/run.sh
#!/bin/bash
set -e

PROJECT_DIR="/home/ec2-user/stock-mvp"

cd "$PROJECT_DIR"

echo "Updating repository..."
git pull

echo "Writing demo env..."
cat << 'ENVEOF' > infra/.env
POSTGRES_DB=stock_mvp
POSTGRES_USER=stock_user
POSTGRES_PASSWORD=stock_password
POSTGRES_PORT_PUBLIC=5432

SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=local-development-secret-change-me-please-32
JWT_EXPIRATION_SECONDS=28800

HTTP_PORT=80
ENVEOF

echo "Deploying..."
./scripts/deploy.sh

echo "Done. Running containers:"
docker ps
EOF

chmod +x /home/ec2-user/run.sh

chown -R ec2-user:ec2-user /home/ec2-user/stock-mvp
chown ec2-user:ec2-user /home/ec2-user/run.sh

docker --version
docker compose version
git --version
systemctl status docker --no-pager
```

Luego entrar por SSH:

```bash
ssh -i <key.pem> ec2-user@<IP_PUBLICA>
./run.sh
```

Abrir desde tu PC:

```text
http://IP_PUBLICA
```

Usuarios demo:

```text
admin@stock.local / admin123
local@stock.local / local123
fabrica@stock.local / fabrica123
```

Para una demo, el security group debe permitir al menos:

```text
22  SSH   desde tu IP
80  HTTP  desde tu IP o 0.0.0.0/0
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
