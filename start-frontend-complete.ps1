# Script para iniciar la aplicación frontend de Impulse

Write-Host "Iniciando aplicacion frontend de Impulse..." -ForegroundColor Green

# Cambiar al directorio del frontend
Set-Location "C:\Users\Dieg0\Impulse\frontend"

# Verificar que existe package.json
if (-not (Test-Path "package.json")) {
    Write-Host "Error: No se encontro package.json" -ForegroundColor Red
    exit 1
}

# Verificar que node_modules existe
if (-not (Test-Path "node_modules")) {
    Write-Host "Instalando dependencias..." -ForegroundColor Yellow
    npm install
}

# Iniciar la aplicación
Write-Host "Iniciando servidor de desarrollo React..." -ForegroundColor Blue
npm start
