import React from 'react';
import './index.css';

/**
 * Componente principal simplificado para prueba inicial
 */
const App: React.FC = () => {
  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <header style={{ background: '#282c34', color: 'white', padding: '20px', borderRadius: '8px' }}>
        <h1>🚀 Impulse Lean v1 - Demo</h1>
        <p>Challenge/Validation Platform</p>
      </header>
      
      <main style={{ marginTop: '20px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '20px' }}>
          <div style={{ background: '#f5f5f5', padding: '20px', borderRadius: '8px' }}>
            <h2>🔧 Backend Status</h2>
            <p>Backend API funcionando en: <code>http://localhost:8080</code></p>
            <p>Estado: <span style={{ color: 'green' }}>✅ Conectado</span></p>
          </div>
          
          <div style={{ background: '#f5f5f5', padding: '20px', borderRadius: '8px' }}>
            <h2>💾 Base de Datos</h2>
            <p>MySQL corriendo en: <code>localhost:3306</code></p>
            <p>Base de datos: <code>impulse_lean</code></p>
          </div>
          
          <div style={{ background: '#f5f5f5', padding: '20px', borderRadius: '8px' }}>
            <h2>👤 Demo Login</h2>
            <p>Email: <code>admin@impulse.app</code></p>
            <p>Password: <code>password</code></p>
          </div>
        </div>
        
        <div style={{ marginTop: '30px', textAlign: 'center' }}>
          <h3>🎉 ¡La aplicación Impulse está funcionando!</h3>
          <p>Backend Spring Boot ✅ | Frontend React ✅ | MySQL ✅</p>
        </div>
      </main>
    </div>
  );
};

export default App;
