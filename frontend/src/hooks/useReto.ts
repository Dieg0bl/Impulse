import { useState, useEffect } from 'react';
import { getToken } from '../services/authService.ts';

export interface Reto {
  id: string;
  titulo: string;
  descripcion: string;
  fechaInicio: string;
  fechaFin: string;
  estado: 'ACTIVO' | 'COMPLETADO' | 'FALLIDO' | 'PAUSADO';
  categoria: 'SALUD' | 'EDUCACION' | 'PERSONAL' | 'PROFESIONAL' | 'SOCIAL';
  dificultad: 'FACIL' | 'MEDIO' | 'DIFICIL' | 'EXTREMO';
  progreso: number;
  validadores: string[];
  reportes: ReporteAvance[];
  recompensas: {
    puntos: number;
    badges: string[];
  };
}

export interface ReporteAvance {
  id: string;
  fecha: string;
  descripcion: string;
  evidencia?: {
    tipo: 'TEXTO' | 'IMAGEN' | 'VIDEO';
    url?: string;
    contenido: string;
  };
  validaciones: {
    validadorId: string;
    estado: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO';
    comentario?: string;
    fecha?: string;
  }[];
}

export interface RetoHook {
  retos: Reto[];
  loading: boolean;
  error: string | null;
  crearReto: (retoData: Partial<Reto>) => Promise<void>;
  actualizarReto: (id: string, retoData: Partial<Reto>) => Promise<void>;
  eliminarReto: (id: string) => Promise<void>;
  reportarAvance: (retoId: string, reporte: Partial<ReporteAvance>) => Promise<void>;
  obtenerRetos: () => Promise<void>;
}

export const useReto = (): RetoHook => {
  const [retos, setRetos] = useState<Reto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const makeAuthenticatedRequest = async (url: string, options: RequestInit = {}) => {
    const token = getToken();
    const response = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        ...options.headers,
      },
    });

    if (!response.ok) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }

    return response.json();
  };

  const obtenerRetos = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await makeAuthenticatedRequest('/api/retos');
      setRetos(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error obteniendo retos');
      console.error('Error obteniendo retos:', err);
    } finally {
      setLoading(false);
    }
  };

  const crearReto = async (retoData: Partial<Reto>) => {
    try {
      setLoading(true);
      setError(null);
      const nuevoReto = await makeAuthenticatedRequest('/api/retos', {
        method: 'POST',
        body: JSON.stringify(retoData),
      });
      setRetos(prev => [...prev, nuevoReto]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error creando reto');
      console.error('Error creando reto:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const actualizarReto = async (id: string, retoData: Partial<Reto>) => {
    try {
      setLoading(true);
      setError(null);
      const retoActualizado = await makeAuthenticatedRequest(`/api/retos/${id}`, {
        method: 'PUT',
        body: JSON.stringify(retoData),
      });
      setRetos(prev => prev.map(reto => reto.id === id ? retoActualizado : reto));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error actualizando reto');
      console.error('Error actualizando reto:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const eliminarReto = async (id: string) => {
    try {
      setLoading(true);
      setError(null);
      await makeAuthenticatedRequest(`/api/retos/${id}`, {
        method: 'DELETE',
      });
      setRetos(prev => prev.filter(reto => reto.id !== id));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error eliminando reto');
      console.error('Error eliminando reto:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const reportarAvance = async (retoId: string, reporte: Partial<ReporteAvance>) => {
    try {
      setLoading(true);
      setError(null);
      const nuevoReporte = await makeAuthenticatedRequest(`/api/retos/${retoId}/reportes`, {
        method: 'POST',
        body: JSON.stringify(reporte),
      });
      
      // Actualizar el reto con el nuevo reporte
      setRetos(prev => prev.map(reto => {
        if (reto.id === retoId) {
          return {
            ...reto,
            reportes: [...reto.reportes, nuevoReporte]
          };
        }
        return reto;
      }));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error reportando avance');
      console.error('Error reportando avance:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    obtenerRetos();
  }, []);

  return {
    retos,
    loading,
    error,
    crearReto,
    actualizarReto,
    eliminarReto,
    reportarAvance,
    obtenerRetos
  };
};
