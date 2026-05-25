#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="$ROOT_DIR/infra/.env"
ENV_EXAMPLE="$ROOT_DIR/infra/.env.example"
COMPOSE_FILE="$ROOT_DIR/infra/docker-compose.yml"

cd "$ROOT_DIR"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed or not available in PATH." >&2
  exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
  cp "$ENV_EXAMPLE" "$ENV_FILE"
  echo "Created infra/.env from infra/.env.example."
  echo "Edit infra/.env before production use, especially POSTGRES_PASSWORD and JWT_SECRET."
fi

docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" pull postgres
docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up -d --build

HTTP_PORT_VALUE="$(grep '^HTTP_PORT=' "$ENV_FILE" | cut -d= -f2 || true)"
HTTP_PORT_VALUE="${HTTP_PORT_VALUE:-80}"

echo
echo "Deploy finished."
echo "Frontend: http://SERVER_IP_OR_DOMAIN:$HTTP_PORT_VALUE"
echo "API:      http://SERVER_IP_OR_DOMAIN:$HTTP_PORT_VALUE/api"
echo
echo "Run smoke test from the server:"
echo "  ./scripts/smoke-test.sh http://localhost:$HTTP_PORT_VALUE"
