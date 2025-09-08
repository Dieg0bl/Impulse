import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../services/useAuth';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      // For demo purposes, we'll simulate a login
      // In a real app, this would be an API call
      if (email && password) {
        const mockUser = {
          id: 1,
          email: email,
          username: email.split('@')[0],
          firstName: 'Demo',
          lastName: 'User',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        const mockToken = 'demo-jwt-token';
        
        login(mockUser, mockToken);
        navigate('/dashboard');
      } else {
        setError('Please fill in all fields');
      }
    } catch (error) {
      console.error('Login failed:', error);
      setError('Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <h2>Login to IMPULSE</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        
        <button type="submit" disabled={loading} className="btn btn-primary">
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
      
      <p>
        Don't have an account? <Link to="/register">Register here</Link>
      </p>
    </div>
  );
};
