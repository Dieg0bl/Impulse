#!/usr/bin/env pwsh
# Script para lanzar el frontend de Impulse

Write-Host "==================================="
Write-Host "Iniciando Frontend Impulse..."
Write-Host "==================================="

# Cambiar al directorio del frontend
Set-Location "C:\Users\Dieg0\Impulse\frontend"

Write-Host "Directorio actual: $(Get-Location)"

# Verificar que Node.js funciona
Write-Host "Verificando Node.js..."
node --version

# Instalar dependencias si es necesario (solo para desarrollo)
Write-Host "Verificando dependencias principales..."
if (-Not (Test-Path "node_modules\react-scripts")) {
    Write-Host "Instalando dependencias principales..."
    npm install react react-dom react-scripts @types/react @types/react-dom axios react-router-dom --legacy-peer-deps
} else {
    Write-Host "✅ Dependencias principales ya están instaladas"
}

# Ejecutar el frontend
Write-Host "Iniciando React..."
npm start
