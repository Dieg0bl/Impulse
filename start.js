const { exec } = require('child_process');

console.log('🚀 IMPULSE - Iniciando aplicación completa');
console.log('🔧 DEBUG: Script iniciado correctamente\n');

// 1. Cerrar procesos anteriores
console.log('🔄 Cerrando instancias anteriores...');
exec('taskkill /f /im java.exe 2>nul', (error) => {
  if (!error) console.log('✅ Java cerrado');
});
exec('taskkill /f /im node.exe 2>nul', (error) => {
  if (!error) console.log('✅ Node cerrado');
});

setTimeout(() => {
  console.log('✅ Procesos anteriores cerrados\n');
  
  // 2. Iniciar Backend
  console.log('📱 Iniciando Backend (Spring Boot)...');
  exec('start "Backend-Impulse" cmd /k "cd backend && mvn spring-boot:run"', { cwd: __dirname }, (error) => {
    if (error) {
      console.log('❌ Error iniciando backend:', error.message);
    } else {
      console.log('✅ Backend iniciando en nueva ventana');
    }
  });
  
  setTimeout(() => {
    console.log('⏳ Backend iniciando... (puede tomar un poco más)\n');
    
    // 3. Iniciar Frontend
    console.log('🌐 Iniciando Frontend (React)...');
    exec('start "Frontend-Impulse" cmd /k "cd frontend && npm start"', { cwd: __dirname }, (error) => {
      if (error) {
        console.log('❌ Error iniciando frontend:', error.message);
      } else {
        console.log('✅ Frontend iniciando en nueva ventana');
      }
    });
    
    setTimeout(() => {
      console.log('⏳ Frontend compilando...\n');
      
      // 4. Abrir Chrome después de que compile
      setTimeout(() => {
        console.log('🌍 Abriendo Chrome...');
        
        // Intentar Chrome
        exec('"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" http://localhost:3000', (error) => {
          if (error) {
            console.log('⚠️  Chrome 64-bit no encontrado, probando 32-bit...');
            // Intentar Chrome x86
            exec('"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe" http://localhost:3000', (error2) => {
              if (error2) {
                console.log('⚠️  Chrome no encontrado, usando navegador por defecto...');
                // Usar navegador por defecto
                exec('start http://localhost:3000', (error3) => {
                  if (!error3) {
                    console.log('✅ Navegador por defecto abierto');
                  }
                });
              } else {
                console.log('✅ Chrome abierto (x86)');
              }
            });
          } else {
            console.log('✅ Chrome abierto (64-bit)');
          }
        });
        
        setTimeout(() => {
          console.log('\n🎉 ¡IMPULSE COMPLETAMENTE INICIADO!');
          console.log('📱 Backend:  http://localhost:8080');
          console.log('🌐 Frontend: http://localhost:3000');
          console.log('\n💡 Para parar: Cierra las ventanas "Backend-Impulse" y "Frontend-Impulse"');
          console.log('👋 Script completado. Tu app sigue corriendo en ventanas separadas.');
          console.log('🔧 DEBUG: Finalizando script en 1 segundo...');
          
          // Terminar este script (las apps siguen corriendo)
          setTimeout(() => {
            process.exit(0);
          }, 1000);
          
        }, 2000);
        
      }, 10000); // 10 segundos para que frontend compile
      
    }, 3000);
    
  }, 8000); // 8 segundos para backend
  
}, 2000);
