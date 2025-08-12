import React, { useState } from 'react';
import Button from '../components/Button';
import { useForgotPassword } from '../hooks/useForgotPassword';

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState('');
  const { forgotPassword, loading, error, success } = useForgotPassword();
  const [mensaje, setMensaje] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje(null);
    try {
      await forgotPassword(email);
      setMensaje('Si el email existe, recibirás instrucciones para restablecer tu contraseña.');
    } catch {
      setMensaje('Error al enviar la solicitud. Intenta de nuevo.');
    }
  };

  return (
    <div className="forgot-password-page">
      <div className="container mx-auto px-4 py-8 max-w-md">
        <h1 className="text-2xl font-bold mb-4">¿Olvidaste tu contraseña?</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="email"
            placeholder="Tu email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            className="w-full p-3 border rounded-lg"
          />
          <Button type="submit" disabled={loading || !email}>
            {loading ? 'Enviando...' : 'Enviar instrucciones'}
          </Button>
        </form>
        {mensaje && <div className="mt-4 text-center text-blue-700">{mensaje}</div>}
        {error && <div className="mt-2 text-center text-red-600">{error}</div>}
        {success && <div className="mt-2 text-center text-green-600">¡Correo enviado correctamente!</div>}
      </div>
    </div>
  );
};

export default ForgotPassword;
