import { useState } from 'react';
import { getEvidencias, uploadEvidencia, reportEvidencia } from '../services/evidenciaService';

export function useEvidencias(retoId: string) {
  const [evidencias, setEvidencias] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchEvidencias = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getEvidencias(retoId);
      setEvidencias(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error obteniendo evidencias');
    } finally {
      setLoading(false);
    }
  };

  const upload = async (file: File) => {
    setLoading(true);
    setError(null);
    try {
      await uploadEvidencia(retoId, file);
      await fetchEvidencias();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error subiendo evidencia');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const report = async (evidenciaId: string, motivo: string) => {
    setLoading(true);
    setError(null);
    try {
      await reportEvidencia(evidenciaId, motivo);
      await fetchEvidencias();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error reportando evidencia');
    } finally {
      setLoading(false);
    }
  };

  return { evidencias, loading, error, fetchEvidencias, upload, report };
}
