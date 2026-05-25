#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
KEY_FILE="${KEY_FILE:-$ROOT_DIR/stock-key.pem}"
SERVER_USER="${SERVER_USER:-ec2-user}"
SERVER_HOST="${SERVER_HOST:-13.222.59.58}"

if [ ! -f "$KEY_FILE" ]; then
  echo "Key file not found: $KEY_FILE" >&2
  echo "Set KEY_FILE=/path/to/key.pem or place stock-key.pem in the repo root." >&2
  exit 1
fi

chmod 600 "$KEY_FILE"

ssh -i "$KEY_FILE" "$SERVER_USER@$SERVER_HOST"
