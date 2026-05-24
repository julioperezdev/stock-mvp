# Especificación Lambdas AWS: Reportes y Alertas para MVP de Gestión de Stock

## 1. Objetivo del componente

Crear funciones AWS Lambda simples para ejecutar procesos asíncronos o programados relacionados con stock.

En el MVP, las Lambdas no deben contener la lógica principal del negocio. Esa lógica debe vivir en el backend Spring Boot. Las Lambdas deben actuar como automatizadores de tareas.

## 2. Lambdas iniciales

Se proponen dos Lambdas:

1. `daily-stock-report`
2. `low-stock-alert`

## 3. Tecnología propuesta

Opciones válidas:

- Node.js 20.x.
- Java 21.

Para un MVP rápido, se recomienda Node.js por simplicidad operativa. Si se busca mantener consistencia con el backend, Java también es válido.

## 4. Arquitectura general

```text
EventBridge Scheduler
  |
  v
AWS Lambda
  |
  v
Backend Spring Boot API
  |
  v
PostgreSQL / RDS

AWS Lambda
  |
  v
CloudWatch Logs
```

## 5. Lambda 1: daily-stock-report

## Objetivo

Ejecutar diariamente un proceso que consulte el reporte de stock y deje trazabilidad del estado general.

## Trigger

EventBridge programado.

Ejemplo:

```text
cron(0 23 * * ? *)
```

Esto ejecuta la Lambda todos los días a las 23:00 UTC.

## Responsabilidades

- Invocar endpoint del backend:
  - `GET /api/reports/daily-stock`
- Registrar resumen en CloudWatch.
- Opcionalmente guardar el resultado en S3.
- Opcionalmente enviar email a fábrica o administrador.

## Alcance incluido

- Ejecución programada.
- Consumo de API backend.
- Log de resultado.
- Manejo básico de errores.

## Alcance excluido

- Generación de PDF.
- Reportes avanzados.
- Envío masivo de emails.
- Reintentos complejos.
- Orquestación con Step Functions.

## Variables de entorno

```bash
API_BASE_URL=https://api.tu-dominio.com/api
REPORT_ENDPOINT=/reports/daily-stock
AWS_REGION=us-east-1
```

## Input esperado

No requiere input manual. Se ejecuta por horario.

## Output esperado en logs

```json
{
  "event": "daily_stock_report_executed",
  "status": "SUCCESS",
  "totalProducts": 100,
  "totalStores": 5,
  "criticalItems": 12,
  "executedAt": "2026-05-23T23:00:00Z"
}
```

## Manejo de errores

Si el backend no responde:

```json
{
  "event": "daily_stock_report_failed",
  "status": "ERROR",
  "reason": "Backend API unavailable"
}
```

## 6. Lambda 2: low-stock-alert

## Objetivo

Consultar productos con stock crítico y generar una alerta simple.

## Trigger

EventBridge programado.

Ejemplo:

```text
cron(0 12 * * ? *)
```

Esto ejecuta la Lambda todos los días a las 12:00 UTC.

## Responsabilidades

- Invocar endpoint:
  - `GET /api/reports/low-stock`
- Revisar si existen productos críticos.
- Registrar alerta en CloudWatch.
- Opcionalmente enviar email o mensaje.

## Variables de entorno

```bash
API_BASE_URL=https://api.tu-dominio.com/api
LOW_STOCK_ENDPOINT=/reports/low-stock
AWS_REGION=us-east-1
ALERT_THRESHOLD=1
```

## Output esperado cuando hay stock crítico

```json
{
  "event": "low_stock_detected",
  "status": "WARNING",
  "criticalItems": 8,
  "storesAffected": 3,
  "executedAt": "2026-05-23T12:00:00Z"
}
```

## Output esperado cuando no hay stock crítico

```json
{
  "event": "low_stock_check_executed",
  "status": "SUCCESS",
  "criticalItems": 0,
  "executedAt": "2026-05-23T12:00:00Z"
}
```

## 7. Estructura sugerida

```text
lambdas/
├── daily-stock-report/
│   ├── src/
│   │   └── handler.ts
│   ├── package.json
│   ├── tsconfig.json
│   └── README.md
└── low-stock-alert/
    ├── src/
    │   └── handler.ts
    ├── package.json
    ├── tsconfig.json
    └── README.md
```

## 8. Ejemplo base en TypeScript

```typescript
const API_BASE_URL = process.env.API_BASE_URL;
const REPORT_ENDPOINT = process.env.REPORT_ENDPOINT ?? '/reports/daily-stock';

export const handler = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}${REPORT_ENDPOINT}`);

    if (!response.ok) {
      throw new Error(`Backend returned status ${response.status}`);
    }

    const data = await response.json();

    console.log(JSON.stringify({
      event: 'daily_stock_report_executed',
      status: 'SUCCESS',
      data,
      executedAt: new Date().toISOString()
    }));

    return {
      statusCode: 200,
      body: JSON.stringify({
        message: 'Daily stock report executed successfully'
      })
    };
  } catch (error) {
    console.error(JSON.stringify({
      event: 'daily_stock_report_failed',
      status: 'ERROR',
      reason: error instanceof Error ? error.message : 'Unknown error',
      executedAt: new Date().toISOString()
    }));

    throw error;
  }
};
```

## 9. IAM mínimo requerido

Para la versión básica que solo consume una API externa y escribe logs:

- Permiso para escribir logs en CloudWatch.
- Permiso de red si la API está dentro de una VPC privada.
- Permiso para Secrets Manager si se guarda un API key.
- Permiso para S3 si se decide guardar reportes.

## Política mínima conceptual

```json
{
  "Effect": "Allow",
  "Action": [
    "logs:CreateLogGroup",
    "logs:CreateLogStream",
    "logs:PutLogEvents"
  ],
  "Resource": "*"
}
```

## 10. CloudWatch Logs

Cada Lambda debe registrar:

- Inicio de ejecución.
- Resultado exitoso.
- Cantidad de productos críticos.
- Errores de conexión.
- Errores de respuesta del backend.
- Tiempo de ejecución si se desea medir performance.

## 11. Reintentos

Para el MVP:

- Usar la política estándar de reintentos de Lambda.
- Registrar errores claramente.
- No implementar DLQ al inicio, salvo que el proceso sea crítico.

Para una versión posterior:

- Agregar DLQ con SQS.
- Agregar alarmas de CloudWatch.
- Agregar notificaciones por SNS.

## 12. Seguridad

Recomendaciones:

- No hardcodear URLs sensibles.
- No hardcodear API keys.
- Usar variables de entorno.
- Usar Secrets Manager si se agrega autenticación entre Lambda y backend.
- Usar HTTPS para consumir la API.
- Restringir acceso si el backend está en red privada.

## 13. Criterios de aceptación

La Lambda `daily-stock-report` se considera correcta si:

- Se ejecuta por EventBridge.
- Consume el endpoint del backend.
- Registra resultado en CloudWatch.
- Registra error si el backend falla.

La Lambda `low-stock-alert` se considera correcta si:

- Se ejecuta por EventBridge.
- Consume el endpoint de stock crítico.
- Detecta si hay productos críticos.
- Registra alerta en CloudWatch.

## 14. Consideraciones futuras

- Enviar email con Amazon SES.
- Enviar notificaciones por SNS.
- Guardar reportes en S3.
- Generar CSV diario.
- Agregar Step Functions si el flujo crece.
- Agregar DLQ con SQS.
- Agregar alarmas de CloudWatch.
- Agregar autenticación entre Lambda y backend.
