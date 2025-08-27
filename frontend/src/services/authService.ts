// Servicio de autenticación
// NOTA: Nunca exponer ni loguear contraseñas o tokens en frontend.
// Los tokens solo se almacenan bajo la clave 'authToken' en localStorage.
export interface SessionDTO {
  id: number;
  active: boolean;
}

export const getSessions = async (): Promise<SessionDTO[]> => {
  const res = await fetch('/api/auth/sessions');
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error obteniendo sesiones');
  }
  return res.json();
};

export const revokeSession = async (sessionId: string): Promise<string> => {
  const res = await fetch(`/api/auth/sessions/${sessionId}`, { method: 'DELETE' });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error revocando sesión');
  }
  return res.text();
};

export const register = async (email: string, password: string, nombre: string): Promise<any> => {
  const res = await fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password, nombre })
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error en registro');
  }
  return res.json();
};

export const forgotPassword = async (email: string): Promise<string> => {
  const res = await fetch('/api/auth/forgot-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error enviando email de recuperación');
  }
  return res.text();
};

export const resetPassword = async (token: string, password: string): Promise<string> => {
  const res = await fetch('/api/auth/reset-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token, password })
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error restableciendo contraseña');
  }
  return res.text();
};
// Mock Auth Service para compilación exitosa
import { logger } from '../utils/logger.ts';

export const getToken = (): string | null => {
  return localStorage.getItem('authToken');
};

export const setToken = (token: string): void => {
  localStorage.setItem('authToken', token);
};

export const removeToken = (): void => {
  localStorage.removeItem('authToken');
};

export const logout = (): void => {
  removeToken();
  // Aquí se haría la llamada al backend para invalidar el token
};

export const login = async (email: string, password: string) => {
  // Mock login - en producción se haría llamada al backend
  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });
    
    if (response.ok) {
      const data = await response.json();
      setToken(data.token);
      return data;
    } else {
      throw new Error('Login failed');
    }
  } catch (error) {
    logger.error('Error en login', 'AUTH', error);
    throw error;
  }
};

export const validateToken = async (token: string) => {
  // Mock token validation - en producción se haría llamada al backend
  try {
    const response = await fetch('/api/auth/validate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });
    
    if (response.ok) {
      return await response.json();
    } else {
      throw new Error('Token validation failed');
    }
  } catch (error) {
    logger.error('Error en validación de token', 'AUTH', error);
    throw error;
  }
};
