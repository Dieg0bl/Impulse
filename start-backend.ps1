#!/usr/bin/env pwsh
# Script para lanzar el backend de Impulse

Write-Host "==================================="
Write-Host "Iniciando Backend Impulse..."
Write-Host "==================================="

# Cambiar al directorio del backend
Set-Location "C:\Users\Dieg0\Impulse\backend"

Write-Host "Directorio actual: $(Get-Location)"

# Verificar que Maven funciona
Write-Host "Verificando Maven..."
mvn --version

# Compilar si es necesario
Write-Host "Compilando proyecto..."
mvn clean compile -DskipTests

# Ejecutar Spring Boot
Write-Host "Iniciando Spring Boot..."
mvn spring-boot:run -DskipTests "-Dspring-boot.run.mainClass=com.impulse.lean.ImpulseLeanApplication"
