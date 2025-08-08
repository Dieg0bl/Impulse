#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR/.."
node tools/check-rest-duplicates.js
node tools/check-flyway-versions.js
node tools/check-flags-sync.js
node tools/check-event-taxonomy-sync.js
node tools/check-file-size.js
echo "✅ Anti-duplicados OK"
