// Authentication hook
import { useState, useEffect } from 'react';
import { UserResponseDto } from '../services/authService';

interface AuthState {
  user: UserResponseDto | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export const useAuth = () => {
  const [authState, setAuthState] = useState<AuthState>({
    user: null,
    token: null,
    isAuthenticated: false,
    isLoading: true,
  });

  useEffect(() => {
    const token = localStorage.getItem('impulse_token');
    const userStr = localStorage.getItem('impulse_user');
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        setAuthState({
          user,
          token,
          isAuthenticated: true,
          isLoading: false,
        });
      } catch {
        localStorage.removeItem('impulse_token');
        localStorage.removeItem('impulse_user');
        setAuthState(prev => ({ ...prev, isLoading: false }));
      }
    } else {
      setAuthState(prev => ({ ...prev, isLoading: false }));
    }
  }, []);

  const login = (user: UserResponseDto, token: string) => {
    localStorage.setItem('impulse_token', token);
    localStorage.setItem('impulse_user', JSON.stringify(user));
    setAuthState({ user, token, isAuthenticated: true, isLoading: false });
  };

  const logout = () => {
    localStorage.removeItem('impulse_token');
    localStorage.removeItem('impulse_user');
    setAuthState({ user: null, token: null, isAuthenticated: false, isLoading: false });
  };

  return {
    ...authState,
    login,
    logout,
  };
};
