import { useState, useEffect } from 'react';
import { getRetoPermisos, updateRetoPermisos } from '../services/retoService';

export function useRetoPermisos(retoId: string) {
  const [permisos, setPermisos] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchPermisos = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getRetoPermisos(retoId);
      setPermisos(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error obteniendo permisos');
    } finally {
      setLoading(false);
    }
  };

  const update = async (nuevoPermiso: any) => {
    setLoading(true);
    setError(null);
    try {
      await updateRetoPermisos(retoId, nuevoPermiso);
      await fetchPermisos();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error actualizando permisos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPermisos(); }, [retoId]);

  return { permisos, loading, error, fetchPermisos, update };
}
