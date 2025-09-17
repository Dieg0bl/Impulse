#!/usr/bin/env bash
set -euo pipefail

: "${ENTITY:?Set ENTITY=PascalCase (e.g., User)}"
: "${FEATURE:?Set FEATURE=lowercase (e.g., user)}"
: "${BASE_PKG:=com.impulse}"

PKG_PATH="${BASE_PKG//./\/}"

root="backend/src/main/java/${PKG_PATH}"
domain="${root}/domain/${FEATURE}"
app="${root}/application/${FEATURE}"
ports="${app}/ports"
dto="${app}/dto"
appmappers="${app}/mappers"
http_base="${root}/adapters/http/${FEATURE}"
http_ctrl="${http_base}/controllers"
http_dto="${http_base}/dto"
http_mappers="${http_base}/mappers"
http_advice="${http_base}/advice"
jpa_base="${root}/adapters/db/jpa/${FEATURE}"
jpa_entities="${jpa_base}/entities"
jpa_repos="${jpa_base}/repositories"
jpa_mappers="${jpa_base}/mappers"

fail() { echo "❌ $1" >&2; exit 1; }
ok()   { echo "✅ $1"; }

echo "🔍 Verificando Clean Architecture para entidad: ${ENTITY} (feature: ${FEATURE})"
echo "📁 Ruta base: ${root}"

# --- Dominio ---
echo ""
echo "🔍 Verificando DOMINIO..."
test -f "${domain}/${ENTITY}.java" || fail "Dominio: falta ${ENTITY}.java en ${domain}"
ok "Dominio: ${ENTITY}.java existe"

# Prohibido: anotaciones de frameworks
if grep -R -E '@(Entity|Table|Id|RestController|Service|Component|Configuration|Json|NotNull|Valid)\b' "${domain}" >/dev/null 2>&1; then
  fail "Dominio: anotaciones prohibidas detectadas en ${domain}"
else
  ok "Dominio: sin anotaciones de framework"
fi

# --- Aplicación ---
echo ""
echo "🔍 Verificando APLICACIÓN..."
for d in "${ports}" "${dto}" "${appmappers}"; do
  test -d "$d" || fail "Aplicación: falta carpeta $d"
done
ok "Aplicación: paquetes ports/dto/mappers existen"

# Prohibido: dependencias a adapters/infra
if grep -R -E 'import .*\.adapters\.|import .*\.infrastructure\.' "${app}" >/dev/null 2>&1; then
  fail "Aplicación: imports a adapters/infra detectados"
else
  ok "Aplicación: sin imports a adapters/infra"
fi

# --- HTTP adapters ---
echo ""
echo "🔍 Verificando ADAPTADORES HTTP..."
test -f "${http_ctrl}/${ENTITY}Controller.java" || test -f "${http_ctrl}/${ENTITY}sController.java" || fail "HTTP: falta controller de ${ENTITY}"
ok "HTTP: controller presente"
test -d "${http_dto}" || fail "HTTP: falta carpeta dto"
test -d "${http_mappers}" || fail "HTTP: falta carpeta mappers"
ok "HTTP: dto/mappers presentes"

# Controller NO debe acceder a JPA
if grep -R -E 'import .*adapters\.db\.jpa' "${http_ctrl}" >/dev/null 2>&1; then
  fail "HTTP: controller importa JPA adapters"
else
  ok "HTTP: controller no depende de JPA adapters"
fi

# --- JPA adapters ---
echo ""
echo "🔍 Verificando ADAPTADORES JPA..."
test -d "${jpa_entities}" || fail "JPA: falta carpeta entities"
test -d "${jpa_repos}" || fail "JPA: falta carpeta repositories"
test -d "${jpa_mappers}" || fail "JPA: falta carpeta mappers"
ok "JPA: entities/repositories/mappers presentes"

# Debe haber al menos una @Entity
grep -R "@Entity" "${jpa_entities}" >/dev/null 2>&1 || fail "JPA: no se encontraron @Entity en ${jpa_entities}"
ok "JPA: entidades con @Entity detectadas"

# Dominio NO debe tener @Entity
if grep -R "@Entity" "${domain}" >/dev/null 2>&1; then
  fail "Separación dominio/JPA: @Entity en dominio"
else
  ok "Separación dominio/JPA: limpia"
fi

# --- Infra ---
echo ""
echo "🔍 Verificando INFRAESTRUCTURA..."
infra_server="${root}/infrastructure/server"
infra_config="${root}/infrastructure/config"
infra_tx="${root}/infrastructure/tx"
infra_time="${root}/infrastructure/time"

# Verificar que existe al menos config
test -d "${infra_config}" || fail "Infra: falta config/"
ok "Infra: config/ presente"

# Verificar que no hay lógica de dominio en infraestructura
if grep -R -E 'class.*UseCase|class.*DomainService' "${root}/infrastructure" >/dev/null 2>&1; then
  fail "Infra: lógica de dominio detectada en infrastructure"
else
  ok "Infra: sin lógica de dominio"
fi

# --- Contratos ---
echo ""
echo "🔍 Verificando CONTRATOS..."
test -f backend/contracts/openapi.yaml || echo "⚠️ Contratos: falta backend/contracts/openapi.yaml"
if [ -f backend/contracts/openapi.yaml ]; then
  if grep -nE "/api/${FEATURE}s(\b|/)|/api/${FEATURE}s/\{id\}" backend/contracts/openapi.yaml >/dev/null; then
    ok "Contratos: paths /api/${FEATURE}s encontrados en openapi.yaml"
  else
    echo "⚠️ Contratos: no se hallaron paths /api/${FEATURE}s en openapi.yaml"
  fi
fi

# --- Frontend ---
echo ""
echo "🔍 Verificando FRONTEND..."
test -f "frontend/src/api/${FEATURE}s.ts" || echo "⚠️ Front: cliente generado frontend/src/api/${FEATURE}s.ts no encontrado"

if [ -d "frontend/src/ui" ]; then
  if grep -R -E '\b(fetch|axios)\(' "frontend/src/ui" >/dev/null 2>&1; then
    echo "⚠️ Front: hay llamadas fetch/axios en componentes UI (mueve a services/)"
  else
    ok "Front: componentes UI sin llamadas directas a APIs"
  fi
fi

if [ -d "frontend/src" ]; then
  if grep -R -E 'from\s+["'\'']\.\./\.\./\.\./backend' frontend/src >/dev/null 2>&1; then
    fail "Front: import directo de backend detectado"
  else
    ok "Front: sin imports directos de backend"
  fi
fi

echo ""
echo "🎉 Verificación de ${ENTITY} completada"
echo ""
echo "📊 RESUMEN:"
echo "✅ Dominio: entidad pura sin anotaciones de framework"
echo "✅ Aplicación: casos de uso, puertos y DTOs separados"
echo "✅ Adaptadores HTTP: controladores con mappers dedicados"
echo "✅ Adaptadores JPA: entidades JPA separadas del dominio"
echo "✅ Infraestructura: configuración sin lógica de negocio"
echo ""
echo "🚀 Estructura Clean Architecture implementada correctamente para ${ENTITY}"
