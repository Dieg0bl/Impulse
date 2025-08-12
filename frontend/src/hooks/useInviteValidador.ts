import { useState } from 'react';
import { inviteValidador } from '../services/validadorService';

export function useInviteValidador() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const invite = async (email: string) => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      await inviteValidador(email);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error invitando validador');
    } finally {
      setLoading(false);
    }
  };

  return { invite, loading, error, success };
}
