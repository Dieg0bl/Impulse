import { useCallback, useEffect, useState } from 'react';
import { useEnterpriseAuth as useAuth } from './useAuthEnterprise';
import { logger } from '../utils/logger';

/**
 * Hook personalizado para manejar permisos granulares de retos.
 * Implementa Object-Based Operations (OBO) en el frontend.
 */

export interface RetoPermissions {
  read: boolean;
  update: boolean;
  delete: boolean;
  submitEvidence: boolean;
  validate: boolean;
  comment: boolean;
  report: boolean;
  moderate: boolean;
}

export interface UseRetoSecurityResult {
  permissions: RetoPermissions | null;
  loading: boolean;
  error: string | null;
  checkPermission: (operation: keyof RetoPermissions) => boolean;
  refreshPermissions: () => Promise<void>;
  canPerformOperation: (operation: keyof RetoPermissions) => boolean;
}

/**
 * Hook para gestionar permisos de un reto específico
 */
export const useRetoSecurity = (retoId: string | number | null): UseRetoSecurityResult => {
  const { isAuthenticated } = useAuth();
  const [permissions, setPermissions] = useState<RetoPermissions | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Función para obtener token CSRF
  const getCsrfToken = useCallback(async (): Promise<string | null> => {
    try {
      const response = await fetch('/api/auth/csrf-token', {
        method: 'GET',
        credentials: 'include'
      });
      const data = await response.json();
      return data.token;
    } catch (error) {
      console.error('Error obteniendo token CSRF:', error);
      return null;
    }
  }, []);

  const fetchPermissions = useCallback(async () => {
    if (!retoId || !isAuthenticated) {
      setPermissions(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const csrfToken = await getCsrfToken();
      
      const response = await fetch(`/api/retos/${retoId}/permisos`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
        },
        credentials: 'include'
      });

      if (response.status === 403) {
        // Sin permisos de lectura básicos
        setPermissions({
          read: false,
          update: false,
          delete: false,
          submitEvidence: false,
          validate: false,
          comment: false,
          report: false,
          moderate: false
        });
        return;
      }

      if (!response.ok) {
        throw new Error(`Error al obtener permisos: ${response.status}`);
      }

      const permisos = await response.json();
      setPermissions(permisos);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error desconocido');
      logger.error(
        'Error al obtener permisos del reto',
        'useRetoSecurity',
        {
          retoId,
          error: err instanceof Error ? err.message : String(err)
        }
      );
    } finally {
      setLoading(false);
    }
  }, [retoId, isAuthenticated, getCsrfToken]);

  const checkPermission = useCallback((operation: keyof RetoPermissions): boolean => {
    return permissions?.[operation] ?? false;
  }, [permissions]);

  const canPerformOperation = useCallback((operation: keyof RetoPermissions): boolean => {
    if (!permissions) return false;
    return permissions[operation];
  }, [permissions]);

  const refreshPermissions = useCallback(async () => {
    await fetchPermissions();
  }, [fetchPermissions]);

  useEffect(() => {
    fetchPermissions();
  }, [fetchPermissions]);

  return {
    permissions,
    loading,
    error,
    checkPermission,
    refreshPermissions,
    canPerformOperation
  };
};

/**
 * Hook para filtrar listas de retos según permisos del usuario
 */
export const useRetoFiltering = () => {
  const { isAuthenticated } = useAuth();

  // Función para obtener token CSRF
  const getCsrfToken = useCallback(async (): Promise<string | null> => {
    try {
      const response = await fetch('/api/auth/csrf-token', {
        method: 'GET',
        credentials: 'include'
      });
      const data = await response.json();
      return data.token;
    } catch (error) {
      console.error('Error obteniendo token CSRF:', error);
      return null;
    }
  }, []);

  const fetchVisibleRetos = useCallback(async (filters: {
    estado?: string;
    page?: number;
    size?: number;
  } = {}) => {
    if (!isAuthenticated) {
      throw new Error('Usuario no autenticado');
    }

    const csrfToken = await getCsrfToken();
    const params = new URLSearchParams();
    if (filters.estado) params.append('estado', filters.estado);
    if (filters.page !== undefined) params.append('page', filters.page.toString());
    if (filters.size !== undefined) params.append('size', filters.size.toString());

    const response = await fetch(`/api/retos?${params.toString()}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error(`Error al obtener retos: ${response.status}`);
    }

    return await response.json();
  }, [isAuthenticated, getCsrfToken]);

  return { fetchVisibleRetos };
};

/**
 * Hook para operaciones seguras en retos
 */
export const useRetoOperations = () => {
  const { isAuthenticated } = useAuth();

  // Función para obtener token CSRF
  const getCsrfToken = useCallback(async (): Promise<string | null> => {
    try {
      const response = await fetch('/api/auth/csrf-token', {
        method: 'GET',
        credentials: 'include'
      });
      const data = await response.json();
      return data.token;
    } catch (error) {
      console.error('Error obteniendo token CSRF:', error);
      return null;
    }
  }, []);

  const updateReto = useCallback(async (retoId: string | number, data: any) => {
    if (!isAuthenticated) {
      throw new Error('Usuario no autenticado');
    }

    const csrfToken = await getCsrfToken();
    const response = await fetch(`/api/retos/${retoId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
      credentials: 'include',
      body: JSON.stringify(data)
    });

    if (response.status === 403) {
      throw new Error('No tienes permisos para actualizar este reto');
    }

    if (!response.ok) {
      throw new Error(`Error al actualizar reto: ${response.status}`);
    }

    return await response.json();
  }, [isAuthenticated, getCsrfToken]);

  const deleteReto = useCallback(async (retoId: string | number) => {
    if (!isAuthenticated) {
      throw new Error('Usuario no autenticado');
    }

    const csrfToken = await getCsrfToken();
    const response = await fetch(`/api/retos/${retoId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
      credentials: 'include'
    });

    if (response.status === 403) {
      throw new Error('No tienes permisos para eliminar este reto');
    }

    if (!response.ok) {
      throw new Error(`Error al eliminar reto: ${response.status}`);
    }

    return await response.json();
  }, [isAuthenticated, getCsrfToken]);

  const submitEvidence = useCallback(async (retoId: string | number, evidenceData: any) => {
    if (!isAuthenticated) {
      throw new Error('Usuario no autenticado');
    }

    const csrfToken = await getCsrfToken();
    const response = await fetch(`/api/retos/${retoId}/evidencia`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
      credentials: 'include',
      body: JSON.stringify(evidenceData)
    });

    if (response.status === 403) {
      throw new Error('No tienes permisos para subir evidencia a este reto');
    }

    if (!response.ok) {
      throw new Error(`Error al subir evidencia: ${response.status}`);
    }

    return await response.json();
  }, [isAuthenticated, getCsrfToken]);

  const validateEvidence = useCallback(async (retoId: string | number, validationData: any) => {
    if (!isAuthenticated) {
      throw new Error('Usuario no autenticado');
    }

    const csrfToken = await getCsrfToken();
    const response = await fetch(`/api/retos/${retoId}/validar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
      credentials: 'include',
      body: JSON.stringify(validationData)
    });

    if (response.status === 403) {
      throw new Error('No tienes permisos para validar este reto');
    }

    if (!response.ok) {
      throw new Error(`Error al validar evidencia: ${response.status}`);
    }

    return await response.json();
  }, [isAuthenticated, getCsrfToken]);

  const reportReto = useCallback(async (retoId: string | number, reason: string) => {
    if (!isAuthenticated) {
      throw new Error('Usuario no autenticado');
    }

    const csrfToken = await getCsrfToken();
    const response = await fetch(`/api/retos/${retoId}/reportar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
      credentials: 'include',
      body: JSON.stringify({ reason })
    });

    if (response.status === 403) {
      throw new Error('No tienes permisos para reportar este reto');
    }

    if (!response.ok) {
      throw new Error(`Error al reportar reto: ${response.status}`);
    }

    return await response.json();
  }, [isAuthenticated, getCsrfToken]);

  return {
    updateReto,
    deleteReto,
    submitEvidence,
    validateEvidence,
    reportReto
  };
};
