import React, { createContext, useContext, useState, ReactNode } from 'react';

interface SessionData {
  user: any;
  token: string | null;
  isAuthenticated: boolean;
}

interface SessionContextType {
  session: SessionData;
  login: (token: string, user: any) => void;
  logout: () => void;
}

const SessionContext = createContext<SessionContextType | undefined>(undefined);

export const useSession = () => {
  const context = useContext(SessionContext);
  if (!context) {
    throw new Error('useSession must be used within a SessionProvider');
  }
  return context;
};

interface SessionProviderProps {
  children: ReactNode;
}

export const SessionProvider: React.FC<SessionProviderProps> = ({ children }) => {
  const [session, setSession] = useState<SessionData>({
    user: null,
    token: localStorage.getItem('token'),
    isAuthenticated: !!localStorage.getItem('token')
  });

  const login = (token: string, user: any) => {
    localStorage.setItem('token', token);
    setSession({
      user,
      token,
      isAuthenticated: true
    });
  };

  const logout = () => {
    localStorage.removeItem('token');
    setSession({
      user: null,
      token: null,
      isAuthenticated: false
    });
  };

  const contextValue = React.useMemo(() => ({ session, login, logout }), [session]);

  return (
    <SessionContext.Provider value={contextValue}>
      {children}
    </SessionContext.Provider>
  );
};
