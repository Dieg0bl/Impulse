# IMPULSE Backend - Script de Inicio (PowerShell)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   IMPULSE Backend - Servidor de Aplicacion" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Iniciando servidor backend en puerto 8080..." -ForegroundColor Green
Write-Host "Base de datos: H2 Memory Database" -ForegroundColor White
Write-Host "Perfil: demo" -ForegroundColor White
Write-Host ""

Set-Location "C:\Users\Dieg0\Impulse\backend"

Write-Host "Verificando compilacion..." -ForegroundColor Yellow
$compileResult = & mvn clean package -DskipTests -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: La compilacion fallo" -ForegroundColor Red
    Read-Host "Presione Enter para continuar"
    exit 1
}

Write-Host ""
Write-Host "✅ Compilacion exitosa" -ForegroundColor Green
Write-Host "🚀 Iniciando servidor..." -ForegroundColor Cyan
Write-Host ""
Write-Host "URLs disponibles:" -ForegroundColor Yellow
Write-Host "  - API Status: http://localhost:8080/api/demo/status" -ForegroundColor White
Write-Host "  - Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "  - H2 Console: http://localhost:8080/h2-console" -ForegroundColor White
Write-Host "  - Cliente Web: file:///C:/Users/Dieg0/Impulse/frontend/public/index.html" -ForegroundColor White
Write-Host ""
Write-Host "Para detener el servidor presione Ctrl+C" -ForegroundColor Red
Write-Host ""

& java -jar target\backend-1.0.0.jar --spring.profiles.active=demo

Write-Host ""
Read-Host "Presione Enter para continuar"
