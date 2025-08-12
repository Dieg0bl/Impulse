import React, { useState } from 'react';
import Button from '../components/Button';
import { useResetPassword } from '../hooks/useResetPassword';

const ResetPassword: React.FC = () => {
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const { resetPassword, loading, error, success } = useResetPassword();
  const [mensaje, setMensaje] = useState<string | null>(null);
  const token = new URLSearchParams(window.location.search).get('token') || '';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje(null);
    if (password !== confirm) {
      setMensaje('Las contraseñas no coinciden.');
      return;
    }
    try {
      await resetPassword(token, password);
      setMensaje('Contraseña restablecida correctamente. Ya puedes iniciar sesión.');
    } catch {
      setMensaje('Error al restablecer la contraseña. El enlace puede haber expirado.');
    }
  };

  return (
    <div className="reset-password-page">
      <div className="container mx-auto px-4 py-8 max-w-md">
        <h1 className="text-2xl font-bold mb-4">Restablecer contraseña</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="password"
            placeholder="Nueva contraseña"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            className="w-full p-3 border rounded-lg"
          />
          <input
            type="password"
            placeholder="Confirmar contraseña"
            value={confirm}
            onChange={e => setConfirm(e.target.value)}
            required
            className="w-full p-3 border rounded-lg"
          />
          <Button type="submit" disabled={loading || !password || !confirm}>
            {loading ? 'Restableciendo...' : 'Restablecer contraseña'}
          </Button>
        </form>
        {mensaje && <div className="mt-4 text-center text-blue-700">{mensaje}</div>}
        {error && <div className="mt-2 text-center text-red-600">{error}</div>}
        {success && <div className="mt-2 text-center text-green-600">¡Contraseña restablecida!</div>}
      </div>
    </div>
  );
};

export default ResetPassword;
