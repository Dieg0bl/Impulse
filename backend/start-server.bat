@echo off
echo.
echo ========================================
echo    IMPULSE Backend - Servidor de Aplicacion
echo ========================================
echo.
echo Iniciando servidor backend en puerto 8080...
echo Base de datos: H2 Memory Database
echo Perfil: demo
echo.

cd /d "C:\Users\Dieg0\Impulse\backend"

echo Verificando compilacion...
call mvn clean package -DskipTests -q

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: La compilacion fallo
    pause
    exit /b 1
)

echo.
echo ✅ Compilacion exitosa
echo 🚀 Iniciando servidor...
echo.
echo Abra su navegador en: http://localhost:8080/api/demo/status
echo Para detener el servidor presione Ctrl+C
echo.

java -jar target\backend-1.0.0.jar --spring.profiles.active=demo

pause
