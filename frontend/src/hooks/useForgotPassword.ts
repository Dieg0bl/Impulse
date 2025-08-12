import { useState } from 'react';
import { forgotPassword } from '../services/authService';

export function useForgotPassword() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleForgot = async (email: string) => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      await forgotPassword(email);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error enviando email');
    } finally {
      setLoading(false);
    }
  };

  return { forgotPassword: handleForgot, loading, error, success };
}
