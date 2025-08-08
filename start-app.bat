@echo off
echo 🚀 Iniciando IMPULSE...

echo [0/4] Cerrando instancias anteriores...
taskkill /f /im "java.exe" 2>nul
taskkill /f /im "node.exe" 2>nul
echo ✅ Procesos anteriores cerrados

echo [1/4] Iniciando Backend...
start "Impulse-Backend" cmd /k "cd backend && mvn spring-boot:run"

echo [2/4] Esperando Backend (8 segundos)...
timeout /t 8 /nobreak > nul

echo [3/4] Iniciando Frontend...
start "Impulse-Frontend" cmd /k "cd frontend && npm start"

echo [4/4] Abriendo navegador...
timeout /t 5 /nobreak > nul
start http://localhost:3000

echo ✅ IMPULSE INICIADO CORRECTAMENTE
echo Backend: http://localhost:8080 
echo Frontend: http://localhost:3000
pause
