const { exec } = require('child_process');

console.log('🚀 IMPULSE - Configuración Ultra Simple');
console.log('🔧 Iniciando...\n');

// Variables para controlar el flujo
let step = 0;

function nextStep() {
  step++;
  
  switch(step) {
    case 1:
      console.log('🔄 Paso 1: Limpiando procesos...');
      exec('taskkill /f /im java.exe 2>nul', () => {});
      exec('taskkill /f /im node.exe 2>nul', () => {});
      setTimeout(nextStep, 3000);
      break;
      
    case 2:
      console.log('📱 Paso 2: Iniciando Backend...');
      exec('start "Backend" cmd /k "cd backend && mvn spring-boot:run"', (error) => {
        if (error) {
          console.log('❌ Error backend:', error.message);
        } else {
          console.log('✅ Backend iniciando...');
        }
      });
      setTimeout(nextStep, 8000);
      break;
      
    case 3:
      console.log('🌐 Paso 3: Iniciando Frontend...');
      exec('start "Frontend" cmd /k "cd frontend && npm start"', (error) => {
        if (error) {
          console.log('❌ Error frontend:', error.message);
        } else {
          console.log('✅ Frontend iniciando...');
        }
      });
      setTimeout(nextStep, 10000);
      break;
      
    case 4:
      console.log('🌍 Paso 4: Abriendo navegador...');
      exec('start http://localhost:3000', (error) => {
        if (error) {
          console.log('❌ Error navegador:', error.message);
        } else {
          console.log('✅ Navegador abierto');
        }
      });
      setTimeout(nextStep, 2000);
      break;
      
    case 5:
      console.log('\n🎉 ¡IMPULSE COMPLETAMENTE INICIADO!');
      console.log('📱 Backend:  http://localhost:8080');
      console.log('🌐 Frontend: http://localhost:3000');
      console.log('\n� Para parar: Cierra las ventanas "Backend" y "Frontend"');
      console.log('👋 Script completado exitosamente.');
      
      // Mantener el proceso activo por un momento para ver el mensaje
      setTimeout(() => {
        console.log('🔚 Cerrando script...');
        process.exit(0);
      }, 3000);
      break;
  }
}

// Iniciar la secuencia
nextStep();
