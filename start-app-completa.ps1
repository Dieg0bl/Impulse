# Script para ejecutar la aplicación IMPULSE completa
# Frontend React (puerto 3000) + Backend Spring Boot (puerto 8080)

Write-Host "==================================================================" -ForegroundColor Green
Write-Host "INICIANDO APLICACIÓN IMPULSE COMPLETA" -ForegroundColor Green
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "Backend API: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "==================================================================" -ForegroundColor Green
Write-Host ""

# Función para verificar si un puerto está en uso
function Test-Port {
    param([int]$Port)
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.ConnectAsync("localhost", $Port).Wait(1000)
        $connection.Close()
        return $true
    } catch {
        return $false
    }
}

# Verificar puertos antes de iniciar
Write-Host "Verificando puertos..." -ForegroundColor Yellow
if (Test-Port 3000) {
    Write-Host "⚠️  Puerto 3000 ya está en uso. El frontend podría estar ejecutándose." -ForegroundColor Yellow
}
if (Test-Port 8080) {
    Write-Host "⚠️  Puerto 8080 ya está en uso. El backend podría estar ejecutándose." -ForegroundColor Yellow
}

# Directorio base
$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Cambiar al directorio frontend y iniciar en nueva ventana
Write-Host "🚀 Iniciando Frontend React..." -ForegroundColor Green
$frontendCmd = "cd '$baseDir\frontend'; npm run dev; Read-Host 'Frontend terminado. Presiona Enter para cerrar'"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $frontendCmd

# Esperar un momento para que el frontend inicie
Start-Sleep -Seconds 3

# Cambiar al directorio backend y iniciar en nueva ventana
Write-Host "🚀 Iniciando Backend Spring Boot..." -ForegroundColor Green
$backendCmd = "cd '$baseDir\backend'; mvn spring-boot:run '-Dspring-boot.run.profiles=test'; Read-Host 'Backend terminado. Presiona Enter para cerrar'"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $backendCmd

Write-Host ""
Write-Host "==================================================================" -ForegroundColor Green
Write-Host "✅ APLICACIÓN IMPULSE INICIADA EN DOS VENTANAS SEPARADAS" -ForegroundColor Green
Write-Host ""
Write-Host "📱 Frontend React: http://localhost:3000" -ForegroundColor Cyan
Write-Host "🔧 Backend Spring Boot: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host ""
Write-Host "💡 AMBOS SERVICIOS ESTÁN EJECUTÁNDOSE EN VENTANAS SEPARADAS" -ForegroundColor Yellow
Write-Host "💡 Para detener los servicios, cierra las ventanas correspondientes o usa Ctrl+C en cada una" -ForegroundColor Yellow
Write-Host "==================================================================" -ForegroundColor Green

# Verificar que los servicios estén ejecutándose
Write-Host ""
Write-Host "Verificando servicios en 10 segundos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host ""
Write-Host "📊 Estado de los servicios:" -ForegroundColor Cyan
if (Test-Port 3000) {
    Write-Host "✅ Frontend: EJECUTÁNDOSE en puerto 3000" -ForegroundColor Green
} else {
    Write-Host "❌ Frontend: NO DETECTADO en puerto 3000" -ForegroundColor Red
}

if (Test-Port 8080) {
    Write-Host "✅ Backend: EJECUTÁNDOSE en puerto 8080" -ForegroundColor Green
} else {
    Write-Host "❌ Backend: NO DETECTADO en puerto 8080" -ForegroundColor Red
}

Write-Host ""
Write-Host "🌟 ¡YA PUEDES USAR LA APLICACIÓN COMPLETA!" -ForegroundColor Green
Write-Host "Visita http://localhost:3000 para comenzar" -ForegroundColor Cyan

Read-Host "Presiona Enter para salir de este script (los servicios seguirán ejecutándose)"
