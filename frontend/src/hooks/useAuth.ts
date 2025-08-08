import { useState, useEffect } from 'react';
import { getToken, logout } from '../services/authService.ts';

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

export interface AuthHook {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  updateUser: (userData: Partial<User>) => void;
}

export const useAuth = (): AuthHook => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      try {
        const token = getToken();
        if (token) {
          // Validar token y obtener datos del usuario
          const response = await fetch('/api/auth/validate', {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          });
          
          if (response.ok) {
            const userData = await response.json();
            setUser(userData);
          } else {
            // Token inválido, limpiar localStorage
            logout();
          }
        }
      } catch (error) {
        console.error('Error validando autenticación:', error);
        logout();
      } finally {
        setLoading(false);
      }
    };

    initAuth();
  }, []);

  const handleLogin = async (email: string, password: string) => {
    try {
      setLoading(true);
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
      });

      if (!response.ok) {
        throw new Error('Credenciales inválidas');
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      setUser(data.user);
    } catch (error) {
      console.error('Error en login:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    setUser(null);
  };

  const updateUser = (userData: Partial<User>) => {
    if (user) {
      setUser({ ...user, ...userData });
    }
  };

  return {
    user,
    isAuthenticated: !!user,
    loading,
    login: handleLogin,
    logout: handleLogout,
    updateUser
  };
};
