# Stock MVP

MVP de gestion de stock para locales y fabrica.

## Estructura

```text
frontend/
backend/
lambdas/
infra/
docs/
```

## Backend local

Verificar que Maven use Java 21 o superior:

```bash
mvn -version
```

Si Maven toma otro JDK, exportar `JAVA_HOME` antes de ejecutar:

```bash
export JAVA_HOME=/path/to/jdk-21-or-newer
```

Levantar PostgreSQL:

```bash
docker compose -f infra/docker-compose.yml up -d postgres
```

Ejecutar Spring Boot:

```bash
cd backend/spring-boot-api
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

O levantar todo con Docker Compose:

```bash
docker compose -f infra/docker-compose.yml up --build
```

Docker Desktop o el daemon de Docker debe estar iniciado antes de ejecutar estos comandos.

## Endpoints principales

- `GET /api/products`
- `POST /api/products`
- `GET /api/stores`
- `POST /api/stores`
- `GET /api/stocks`
- `POST /api/stocks`
- `PUT /api/stocks/{id}`
- `POST /api/sales`
- `GET /api/reports/daily-stock`
- `GET /api/reports/low-stock`

## Frontend local

La app Angular esta en `frontend/angular-app` y consume `http://localhost:8080/api`.

```bash
cd frontend/angular-app
npm install
npx -p node@22 node ./node_modules/@angular/cli/bin/ng serve --host 0.0.0.0 --port 4200
```

Build:

```bash
cd frontend/angular-app
npx -p node@22 node ./node_modules/@angular/cli/bin/ng build
```

El Node global de esta maquina esta en `20.1.0`; Angular 21 requiere Node `20.19` o superior. Por eso los comandos usan Node 22 temporal via `npx`.
