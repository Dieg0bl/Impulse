import { useState } from 'react';
import { register } from '../services/authService';

export function useRegister() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleRegister = async (email: string, password: string, nombre: string) => {
    setLoading(true);
    setError(null);
    try {
      await register(email, password, nombre);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error en registro');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { register: handleRegister, loading, error };
}
