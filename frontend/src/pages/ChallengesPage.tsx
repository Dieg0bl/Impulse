import React from 'react';

export const ChallengesPage: React.FC = () => {
  return (
    <div className="challenges-page">
      <div className="container">
        <div className="page-header">
          <h1>Challenges</h1>
          <button className="btn btn-primary">Create Challenge</button>
        </div>
        
        <div className="challenges-filters">
          <div className="filter-group">
            <label>Status:</label>
            <select>
              <option value="all">All</option>
              <option value="active">Active</option>
              <option value="completed">Completed</option>
              <option value="pending">Pending</option>
            </select>
          </div>
          
          <div className="filter-group">
            <label>Type:</label>
            <select>
              <option value="all">All Types</option>
              <option value="photo">Photo Verification</option>
              <option value="location">Location Check</option>
              <option value="identity">Identity Verification</option>
            </select>
          </div>
          
          <div className="search-group">
            <input 
              type="text" 
              placeholder="Search challenges..." 
              className="search-input"
            />
          </div>
        </div>
        
        <div className="challenges-grid">
          <div className="challenge-card">
            <div className="challenge-header">
              <h3>Photo Verification</h3>
              <span className="challenge-status active">Active</span>
            </div>
            <p className="challenge-description">
              Submit a clear photo of your government ID for verification.
            </p>
            <div className="challenge-meta">
              <span className="challenge-reward">€25.00</span>
              <span className="challenge-deadline">Expires in 3 days</span>
            </div>
            <div className="challenge-actions">
              <button className="btn btn-primary">Start Challenge</button>
            </div>
          </div>
          
          <div className="challenge-card">
            <div className="challenge-header">
              <h3>Location Verification</h3>
              <span className="challenge-status pending">Pending</span>
            </div>
            <p className="challenge-description">
              Verify your current location by taking a photo with GPS coordinates.
            </p>
            <div className="challenge-meta">
              <span className="challenge-reward">€15.00</span>
              <span className="challenge-deadline">Expires in 1 week</span>
            </div>
            <div className="challenge-actions">
              <button className="btn btn-secondary">View Details</button>
            </div>
          </div>
          
          <div className="challenge-card">
            <div className="challenge-header">
              <h3>Identity Check</h3>
              <span className="challenge-status completed">Completed</span>
            </div>
            <p className="challenge-description">
              Complete identity verification process with biometric data.
            </p>
            <div className="challenge-meta">
              <span className="challenge-reward">€50.00</span>
              <span className="challenge-deadline">Completed 2 days ago</span>
            </div>
            <div className="challenge-actions">
              <button className="btn btn-success" disabled>✓ Completed</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
