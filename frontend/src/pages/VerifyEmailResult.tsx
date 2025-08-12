import React, { useEffect, useState } from 'react';
import Button from '../components/Button';

const VerifyEmailResult: React.FC = () => {
  const [status, setStatus] = useState<'pending' | 'success' | 'error'>('pending');

  // En una app real, el token vendría de la URL
  useEffect(() => {
    const token = new URLSearchParams(window.location.search).get('token') || '';
    if (!token) {
      setStatus('error');
      return;
    }
    fetch('/api/auth/verify-email', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token })
    })
      .then(res => setStatus(res.ok ? 'success' : 'error'))
      .catch(() => setStatus('error'));
  }, []);

  return (
    <div className="verify-email-result-page">
      <div className="container mx-auto px-4 py-8 max-w-md text-center">
        {status === 'pending' && <p>Verificando correo electrónico...</p>}
        {status === 'success' && (
          <>
            <h1 className="text-2xl font-bold mb-4">¡Correo verificado!</h1>
            <p className="mb-4">Tu correo ha sido verificado correctamente. Ya puedes iniciar sesión.</p>
            <Button href="/login">Ir a iniciar sesión</Button>
          </>
        )}
        {status === 'error' && (
          <>
            <h1 className="text-2xl font-bold mb-4">Error de verificación</h1>
            <p className="mb-4">El enlace de verificación no es válido o ha expirado.</p>
            <Button href="/">Volver al inicio</Button>
          </>
        )}
      </div>
    </div>
  );
};

export default VerifyEmailResult;
