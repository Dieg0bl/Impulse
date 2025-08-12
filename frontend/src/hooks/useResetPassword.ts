import { useState } from 'react';
import { resetPassword } from '../services/authService';

export function useResetPassword() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleReset = async (token: string, password: string) => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      await resetPassword(token, password);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error restableciendo contraseña');
    } finally {
      setLoading(false);
    }
  };

  return { resetPassword: handleReset, loading, error, success };
}
