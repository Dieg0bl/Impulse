import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../services/useAuth';

export const Navigation: React.FC = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path: string) => location.pathname === path;

  return (
    <nav className="navigation">
      <div className="nav-container">
        <Link to="/dashboard" className="nav-brand">
          <h1>IMPULSE</h1>
        </Link>
        
        <div className="nav-links">
          <Link 
            to="/dashboard" 
            className={`nav-link ${isActive('/dashboard') ? 'active' : ''}`}
          >
            Dashboard
          </Link>
          <Link 
            to="/challenges" 
            className={`nav-link ${isActive('/challenges') ? 'active' : ''}`}
          >
            Challenges
          </Link>
          <Link 
            to="/profile" 
            className={`nav-link ${isActive('/profile') ? 'active' : ''}`}
          >
            Profile
          </Link>
        </div>
        
        <div className="nav-user">
          {user && (
            <div className="user-info">
              <span className="user-name">{user.email}</span>
              <button onClick={handleLogout} className="logout-btn">
                Logout
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};