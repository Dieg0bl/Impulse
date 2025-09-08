// Authentication service
import api from './api';

export interface UserRegistrationDto {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
}

export interface UserLoginDto {
  emailOrUsername: string;
  password: string;
}

export interface UserResponseDto {
  id: string;
  email: string;
  username: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  createdAt: string;
  lastLoginAt?: string;
}

export interface AuthResponseDto {
  token: string;
  tokenType: string;
  user: UserResponseDto;
}

export const authService = {
  async register(data: UserRegistrationDto): Promise<UserResponseDto> {
    const response = await api.post('/auth/register', data);
    return response.data;
  },

  async login(data: UserLoginDto): Promise<AuthResponseDto> {
    const response = await api.post('/auth/login', data);
    const authData = response.data;
    
    // Store token and user data
    localStorage.setItem('impulse_token', authData.token);
    localStorage.setItem('impulse_user', JSON.stringify(authData.user));
    
    return authData;
  },

  logout() {
    localStorage.removeItem('impulse_token');
    localStorage.removeItem('impulse_user');
  },

  getCurrentUser(): UserResponseDto | null {
    const userData = localStorage.getItem('impulse_user');
    return userData ? JSON.parse(userData) : null;
  },

  getToken(): string | null {
    return localStorage.getItem('impulse_token');
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
};

export default authService;
