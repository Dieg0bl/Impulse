@echo off
echo 🚀 IMPULSE - Iniciando aplicacion completa
echo.

echo 🔄 Cerrando procesos anteriores...
taskkill /f /im java.exe 2>nul
taskkill /f /im node.exe 2>nul
timeout /t 3 /nobreak >nul

echo 📱 Iniciando Backend...
start "Backend-Impulse" cmd /k "cd backend && mvn spring-boot:run"
timeout /t 8 /nobreak >nul

echo 🌐 Iniciando Frontend...
start "Frontend-Impulse" cmd /k "cd frontend && npm start"
timeout /t 10 /nobreak >nul

echo 🌍 Abriendo navegador...
start http://localhost:3000

echo.
echo 🎉 ¡IMPULSE COMPLETAMENTE INICIADO!
echo 📱 Backend:  http://localhost:8080
echo 🌐 Frontend: http://localhost:3000
echo.
echo 💡 Para parar: Cierra las ventanas "Backend-Impulse" y "Frontend-Impulse"
echo 👋 Script completado. Tu app sigue corriendo.

pause
