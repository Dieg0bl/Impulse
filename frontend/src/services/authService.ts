// Mock Auth Service para compilación exitosa

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
    console.error('Login error:', error);
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
    console.error('Token validation error:', error);
    throw error;
  }
};
