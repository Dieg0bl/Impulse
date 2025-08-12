import { useState, useEffect } from 'react';
import { getConsents, updateConsent } from '../services/privacyService';

export function useConsents() {
  const [consents, setConsents] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchConsents = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getConsents();
      setConsents(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error obteniendo consents');
    } finally {
      setLoading(false);
    }
  };

  const update = async (consentId: string, value: boolean) => {
    setLoading(true);
    setError(null);
    try {
      await updateConsent(consentId, value);
      await fetchConsents();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error actualizando consent');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchConsents(); }, []);

  return { consents, loading, error, fetchConsents, update };
}
