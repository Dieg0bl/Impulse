#!/usr/bin/env bash

# Script para crear estructura Clean Architecture para una entidad

ENTITY=$1
FEATURE=$2

if [ -z "$ENTITY" ] || [ -z "$FEATURE" ]; then
    echo "Uso: $0 <Entity> <feature>"
    echo "Ejemplo: $0 User user"
    exit 1
fi

BASE_PATH="C:/Users/Dieg0/Impulse/backend/src/main/java/com/impulse"

# Crear directorios de dominio
mkdir -p "${BASE_PATH}/domain/${FEATURE}"

# Crear directorios de aplicación
mkdir -p "${BASE_PATH}/application/${FEATURE}/ports"
mkdir -p "${BASE_PATH}/application/${FEATURE}/dto"
mkdir -p "${BASE_PATH}/application/${FEATURE}/mappers"

# Crear directorios de adaptadores HTTP
mkdir -p "${BASE_PATH}/adapters/http/${FEATURE}/controllers"
mkdir -p "${BASE_PATH}/adapters/http/${FEATURE}/dto"
mkdir -p "${BASE_PATH}/adapters/http/${FEATURE}/mappers"

# Crear directorios de adaptadores JPA
mkdir -p "${BASE_PATH}/adapters/db/jpa/${FEATURE}/entities"
mkdir -p "${BASE_PATH}/adapters/db/jpa/${FEATURE}/repositories"
mkdir -p "${BASE_PATH}/adapters/db/jpa/${FEATURE}/mappers"

echo "✅ Estructura creada para entidad ${ENTITY} (feature: ${FEATURE})"
