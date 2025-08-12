import { useState, useEffect } from 'react';
import { getSessions, revokeSession } from '../services/authService';

export function useSessions() {
  const [sessions, setSessions] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchSessions = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getSessions();
      setSessions(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error obteniendo sesiones');
    } finally {
      setLoading(false);
    }
  };

  const revoke = async (sessionId: string) => {
    setLoading(true);
    setError(null);
    try {
      await revokeSession(sessionId);
      await fetchSessions();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error revocando sesión');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchSessions(); }, []);

  return { sessions, loading, error, fetchSessions, revoke };
}
