import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../services/useAuth';

export const RegisterPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
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
      // For demo purposes, we'll simulate a registration
      // In a real app, this would be an API call
      if (email && username && password) {
        const mockUser = {
          id: 1,
          email: email,
          username: username,
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
      console.error('Registration failed:', error);
      setError('Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <h2>Join IMPULSE</h2>
      
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
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
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
          {loading ? 'Creating account...' : 'Register'}
        </button>
      </form>
      
      <p>
        Already have an account? <Link to="/login">Login here</Link>
      </p>
    </div>
  );
};
