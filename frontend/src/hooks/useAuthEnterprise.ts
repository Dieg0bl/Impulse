import { useState, useEffect, useCallback } from 'react';
import { useNavigation } from '../contexts/NavigationContext.tsx';

interface User {
  id: string;
  email: string;
  nombre: string;
  apellidos: string;
  rol: 'USUARIO' | 'ADMIN' | 'BACKUP' | 'SOLO_LECTURA';
  estado: 'ACTIVO' | 'PENDIENTE' | 'SUSPENDIDO' | 'BAJA';
  consentimientos: {
    marketing: boolean;
    cookies: boolean;
    analytics: boolean;
    comunicaciones: boolean;
    fechaAceptacion: string;
  };
}

interface LoginCredentials {
  email: string;
  password: string;
  rememberMe?: boolean;
}

interface RegisterData {
  email: string;
  password: string;
  nombre: string;
  apellidos: string;
  consentimientos: {
    marketing: boolean;
    cookies: boolean;
    analytics: boolean;
    comunicaciones: boolean;
  };
}

export interface AuthHook {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
  login: (credentials: LoginCredentials) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  refreshAuth: () => Promise<void>;
  clearError: () => void;
  updateUser: (userData: Partial<User>) => void;
}

/**
 * Hook de autenticación empresarial con cookies y gestión avanzada de estado.
 * Implementa autenticación segura con HttpOnly cookies y renovación automática.
 */
export const useEnterpriseAuth = (): AuthHook => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { navigateTo, setLoading: setNavLoading } = useNavigation();

  // Función para obtener el token CSRF
  const getCsrfToken = useCallback(async (): Promise<string | null> => {
    try {
      const response = await fetch('/api/auth/csrf', {
        method: 'GET',
        credentials: 'include'
      });
      
      if (response.ok) {
        const data = await response.json();
        return data.token;
      }
    } catch (error) {
      console.error('Error obteniendo token CSRF:', error);
    }
    return null;
  }, []);

  // Función para verificar el estado de autenticación
  const checkAuthStatus = useCallback(async (): Promise<User | null> => {
    try {
      const response = await fetch('/api/auth/status', {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const userData = await response.json();
        return userData;
      } else if (response.status === 401) {
        // Token expirado o inválido
        return null;
      } else {
        throw new Error('Error al verificar autenticación');
      }
    } catch (error) {
      console.error('Error verificando estado de autenticación:', error);
      return null;
    }
  }, []);

  // Inicializar autenticación
  useEffect(() => {
    const initAuth = async () => {
      setLoading(true);
      setNavLoading(true);
      
      try {
        const userData = await checkAuthStatus();
        setUser(userData);
      } catch (error) {
        console.error('Error inicializando autenticación:', error);
        setError('Error al cargar la sesión');
      } finally {
        setLoading(false);
        setNavLoading(false);
      }
    };

    initAuth();
  }, [checkAuthStatus, setNavLoading]);

  // Login con cookies
  const login = useCallback(async (credentials: LoginCredentials): Promise<void> => {
    setLoading(true);
    setError(null);
    setNavLoading(true);

    try {
      // Obtener token CSRF
      const csrfToken = await getCsrfToken();
      if (!csrfToken) {
        throw new Error('No se pudo obtener el token CSRF');
      }

      // Realizar login
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
          'X-XSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(credentials)
      });

      if (response.ok) {
        const userData = await response.json();
        setUser(userData);
        
        // Navegar al dashboard o página de destino
        const redirectTo = new URLSearchParams(window.location.search).get('redirect');
        navigateTo(redirectTo || '/dashboard', { replace: true });
      } else {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Error en el login');
      }
    } catch (error) {
      console.error('Error en login:', error);
      setError(error instanceof Error ? error.message : 'Error desconocido en login');
    } finally {
      setLoading(false);
      setNavLoading(false);
    }
  }, [getCsrfToken, navigateTo, setNavLoading]);

  // Registro de usuario
  const register = useCallback(async (data: RegisterData): Promise<void> => {
    setLoading(true);
    setError(null);
    setNavLoading(true);

    try {
      const csrfToken = await getCsrfToken();
      if (!csrfToken) {
        throw new Error('No se pudo obtener el token CSRF');
      }

      const response = await fetch('/api/auth/register', {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
          'X-XSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(data)
      });

      if (response.ok) {
        const userData = await response.json();
        setUser(userData);
        
        // Navegar al onboarding para nuevos usuarios
        navigateTo('/onboarding', { replace: true });
      } else {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Error en el registro');
      }
    } catch (error) {
      console.error('Error en registro:', error);
      setError(error instanceof Error ? error.message : 'Error desconocido en registro');
    } finally {
      setLoading(false);
      setNavLoading(false);
    }
  }, [getCsrfToken, navigateTo, setNavLoading]);

  // Logout con limpieza de cookies
  const logout = useCallback(async (): Promise<void> => {
    setLoading(true);
    setNavLoading(true);

    try {
      const csrfToken = await getCsrfToken();
      
      await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
          ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
        }
      });

      // Limpiar estado independientemente del resultado
      setUser(null);
      setError(null);
      
      // Navegar al home
      navigateTo('/', { replace: true });
    } catch (error) {
      console.error('Error en logout:', error);
      // Limpiar estado de todas formas
      setUser(null);
      navigateTo('/', { replace: true });
    } finally {
      setLoading(false);
      setNavLoading(false);
    }
  }, [getCsrfToken, navigateTo, setNavLoading]);

  // Refrescar autenticación
  const refreshAuth = useCallback(async (): Promise<void> => {
    try {
      const userData = await checkAuthStatus();
      setUser(userData);
    } catch (error) {
      console.error('Error refrescando autenticación:', error);
      setError('Error al refrescar la sesión');
    }
  }, [checkAuthStatus]);

  // Limpiar error
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  // Actualizar datos del usuario
  const updateUser = useCallback((userData: Partial<User>) => {
    setUser(prevUser => prevUser ? { ...prevUser, ...userData } : null);
  }, []);

  // Configurar renovación automática de tokens
  useEffect(() => {
    if (user) {
      const refreshInterval = setInterval(async () => {
        try {
          const response = await fetch('/api/auth/refresh', {
            method: 'POST',
            credentials: 'include',
            headers: {
              'Content-Type': 'application/json'
            }
          });

          if (!response.ok) {
            // Si falla el refresh, cerrar sesión
            console.warn('Fallo en renovación automática de token');
            await logout();
          }
        } catch (error) {
          console.error('Error en renovación automática:', error);
        }
      }, 14 * 60 * 1000); // Renovar cada 14 minutos (antes de que expire el token de 15 min)

      return () => clearInterval(refreshInterval);
    }
  }, [user, logout]);

  return {
    user,
    isAuthenticated: !!user,
    loading,
    error,
    login,
    register,
    logout,
    refreshAuth,
    clearError,
    updateUser
  };
};

export default useEnterpriseAuth;
