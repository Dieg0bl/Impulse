import React from 'react';
import { Link } from 'react-router-dom';

export const HomePage: React.FC = () => {
  return (
    <div className="page">
      <h2>Welcome to IMPULSE</h2>
      <p>The minimalist platform for human validation and challenge completion.</p>
      
      <div className="actions">
        <Link to="/register" className="btn btn-primary">Get Started</Link>
        <Link to="/login" className="btn btn-secondary">Login</Link>
      </div>
      
      <section className="features">
        <h3>Core Features</h3>
        <ul>
          <li>Create validation challenges</li>
          <li>Submit evidence of completion</li>
          <li>Human validation process</li>
          <li>Minimal, focused experience</li>
        </ul>
      </section>
    </div>
  );
};
