import { useState } from 'react';
import { deleteValidador } from '../services/validadorService';

export function useDeleteValidador() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const remove = async (validadorId: string) => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      await deleteValidador(validadorId);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error eliminando validador');
    } finally {
      setLoading(false);
    }
  };

  return { remove, loading, error, success };
}
