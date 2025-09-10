#!/usr/bin/env pwsh
# Script completo para lanzar Impulse (Backend + Frontend + Base de datos)

Write-Host "======================================="
Write-Host "     🚀 INICIANDO IMPULSE LEAN v1     "
Write-Host "======================================="

# Función para verificar si un puerto está en uso
function Test-Port {
    param([int]$Port)
    try {
        $connection = Test-NetConnection -ComputerName localhost -Port $Port -WarningAction SilentlyContinue
        return $connection.TcpTestSucceeded
    } catch {
        return $false
    }
}

Write-Host ""
Write-Host "🔍 Verificando servicios necesarios..."

# Verificar Docker
Write-Host "📦 Verificando Docker..."
try {
    docker --version | Out-Null
    Write-Host "✅ Docker está disponible"
} catch {
    Write-Host "❌ Docker no está disponible. Por favor instala Docker."
    exit 1
}

# Verificar MySQL Container
Write-Host "🐬 Verificando MySQL..."
$mysqlRunning = docker ps --filter "name=impulse-lean-mysql" --format "table {{.Names}}" | Select-String "impulse-lean-mysql"
if ($mysqlRunning) {
    Write-Host "✅ MySQL está corriendo"
} else {
    Write-Host "⚠️  MySQL no está corriendo. Iniciando..."
    Set-Location "C:\Users\Dieg0\Impulse\infra\docker"
    docker-compose -f compose.yaml up -d mysql
    Write-Host "⏳ Esperando a que MySQL esté listo..."
    Start-Sleep -Seconds 10
}

# Verificar backend
Write-Host "🔧 Verificando Backend..."
if (Test-Port -Port 8080) {
    Write-Host "✅ Backend ya está corriendo en puerto 8080"
} else {
    Write-Host "⚠️  Backend no está corriendo. Iniciando..."
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "C:\Users\Dieg0\Impulse\start-backend.ps1"
    Write-Host "⏳ Esperando a que el backend esté listo..."
    Start-Sleep -Seconds 15
}

# Verificar frontend
Write-Host "⚛️  Verificando Frontend..."
if (Test-Port -Port 3000) {
    Write-Host "✅ Frontend ya está corriendo en puerto 3000"
} else {
    Write-Host "⚠️  Frontend no está corriendo. Iniciando..."
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "C:\Users\Dieg0\Impulse\start-frontend.ps1"
    Write-Host "⏳ Esperando a que el frontend esté listo..."
    Start-Sleep -Seconds 20
}

Write-Host ""
Write-Host "🎉 ¡IMPULSE LEAN ESTÁ INICIADO!"
Write-Host ""
Write-Host "📍 URLs disponibles:"
Write-Host "   🌐 Frontend:     http://localhost:3000"
Write-Host "   🔧 Backend API:  http://localhost:8080"
Write-Host "   💾 MySQL:        localhost:3306"
Write-Host "   📊 Actuator:     http://localhost:8080/actuator/health"
Write-Host ""
Write-Host "👤 Credenciales de demo:"
Write-Host "   📧 Email:    admin@impulse.app"
Write-Host "   🔑 Password: password"
Write-Host ""
Write-Host "✨ ¡La aplicación está lista para usar!"
Write-Host ""

# Abrir el navegador
Write-Host "🌐 Abriendo navegador..."
Start-Process "http://localhost:3000"
