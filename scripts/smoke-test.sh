#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://localhost}"

echo "Testing frontend at $BASE_URL ..."
curl -fsS "$BASE_URL/" >/dev/null

echo "Testing login ..."
LOGIN_RESPONSE="$(curl -fsS -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@stock.local","password":"admin123"}')"

TOKEN="$(printf '%s' "$LOGIN_RESPONSE" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')"
if [ -z "$TOKEN" ]; then
  echo "Could not read accessToken from login response." >&2
  exit 1
fi

echo "Testing authenticated API ..."
curl -fsS "$BASE_URL/api/auth/me" \
  -H "Authorization: Bearer $TOKEN" >/dev/null

curl -fsS "$BASE_URL/api/products" \
  -H "Authorization: Bearer $TOKEN" >/dev/null

echo "Smoke test passed."
