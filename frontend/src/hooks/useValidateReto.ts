import { useState } from 'react';
import { validateReto } from '../services/retoService';

export function useValidateReto() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const validate = async (retoId: string, validacion: any) => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      await validateReto(retoId, validacion);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error validando reto');
    } finally {
      setLoading(false);
    }
  };

  return { validate, loading, error, success };
}
