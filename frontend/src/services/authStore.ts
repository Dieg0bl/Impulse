// Global state management with Zustand
import { create } from 'zustand';
import { UserResponseDto } from '../services/authService';

interface AuthState {
  user: UserResponseDto | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (user: UserResponseDto, token: string) => void;
  logout: () => void;
  updateUser: (user: UserResponseDto) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  
  login: (user: UserResponseDto, token: string) => {
    localStorage.setItem('impulse_token', token);
    localStorage.setItem('impulse_user', JSON.stringify(user));
    set({ user, token, isAuthenticated: true });
  },
  
  logout: () => {
    localStorage.removeItem('impulse_token');
    localStorage.removeItem('impulse_user');
    set({ user: null, token: null, isAuthenticated: false });
  },
  
  updateUser: (user: UserResponseDto) => {
    localStorage.setItem('impulse_user', JSON.stringify(user));
    set({ user });
  },
}));

// Initialize auth state from localStorage
const initializeAuth = () => {
  const token = localStorage.getItem('impulse_token');
  const userStr = localStorage.getItem('impulse_user');
  
  if (token && userStr) {
    try {
      const user = JSON.parse(userStr);
      useAuthStore.getState().login(user, token);
    } catch (error) {
      // Clear invalid data
      localStorage.removeItem('impulse_token');
      localStorage.removeItem('impulse_user');
    }
  }
};

// Initialize on app load
initializeAuth();
