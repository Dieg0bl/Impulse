import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { getToken, setToken, removeToken } from '../services/authService';

interface SessionContextProps {
  token: string | null;
  setSessionToken: (token: string | null) => void;
  logout: () => void;
  refresh: () => Promise<void>;
  loading: boolean;
}

const SessionContext = createContext<SessionContextProps | undefined>(undefined);

export const useSession = () => {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error('useSession debe usarse dentro de SessionProvider');
  return ctx;
};

export const SessionProvider = ({ children }: { children: ReactNode }) => {
  const [token, setTokenState] = useState<string | null>(getToken());
  const [loading, setLoading] = useState(false);

  const setSessionToken = (t: string | null) => {
    if (t) {
      setToken(t);
      setTokenState(t);
    } else {
      removeToken();
      setTokenState(null);
    }
  };

  const logout = () => {
    removeToken();
    setTokenState(null);
    // Aquí podrías limpiar más estado global, cookies, etc.
  };

  const refresh = async () => {
    setLoading(true);
    try {
      if (!token) return;
      const res = await fetch('/api/auth/refresh', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setSessionToken(data.token);
      } else {
        logout();
      }
    } catch {
      logout();
    } finally {
      setLoading(false);
    }
  };

  // Refresh silencioso cada 10 minutos
  useEffect(() => {
    if (!token) return;
    const interval = setInterval(refresh, 10 * 60 * 1000);
    return () => clearInterval(interval);
  }, [token]);

  return (
    <SessionContext.Provider value={{ token, setSessionToken, logout, refresh, loading }}>
      {children}
    </SessionContext.Provider>
  );
};
