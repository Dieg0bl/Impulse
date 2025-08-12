import { useState, useEffect } from 'react';
import { getPrivacyRequests, createPrivacyRequest } from '../services/privacyService';

export function usePrivacyRequests() {
  const [requests, setRequests] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchRequests = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getPrivacyRequests();
      setRequests(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error obteniendo solicitudes');
    } finally {
      setLoading(false);
    }
  };

  const create = async (type: string) => {
    setLoading(true);
    setError(null);
    try {
      await createPrivacyRequest(type);
      await fetchRequests();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error creando solicitud');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchRequests(); }, []);

  return { requests, loading, error, fetchRequests, create };
}
