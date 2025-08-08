#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
shopt -s nullglob
files=(backend/src/main/resources/db/migration/V*.sql)
declare -A seen
dup=0
for f in "${files[@]}"; do
  b=$(basename "$f")
  if [[ $b =~ ^V([0-9]+)__.*\.sql$ ]]; then
    v=${BASH_REMATCH[1]}
    if [[ -n ${seen[$v]:-} ]]; then echo "❌ Versión duplicada: V$v ($f y ${seen[$v]})"; dup=1; else seen[$v]=$f; fi
  fi
done
if [[ $dup -eq 1 ]]; then exit 1; else echo "✅ Flyway OK (sin duplicados)"; fi
