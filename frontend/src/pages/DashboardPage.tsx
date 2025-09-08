import React from 'react';

export const DashboardPage: React.FC = () => {
  return (
    <div className="dashboard-page">
      <div className="container">
        <h1>Dashboard</h1>
        <div className="dashboard-grid">
          <div className="dashboard-card">
            <h3>Active Challenges</h3>
            <div className="stat-number">12</div>
            <p>Challenges available</p>
          </div>
          
          <div className="dashboard-card">
            <h3>Completed</h3>
            <div className="stat-number">5</div>
            <p>Challenges completed</p>
          </div>
          
          <div className="dashboard-card">
            <h3>Evidence</h3>
            <div className="stat-number">8</div>
            <p>Evidence submitted</p>
          </div>
          
          <div className="dashboard-card">
            <h3>Validated</h3>
            <div className="stat-number">3</div>
            <p>Evidence validated</p>
          </div>
        </div>
        
        <div className="recent-activity">
          <h2>Recent Activity</h2>
          <div className="activity-list">
            <div className="activity-item">
              <div className="activity-icon">✓</div>
              <div className="activity-content">
                <p>Challenge "Photo Verification" completed</p>
                <span className="activity-time">2 hours ago</span>
              </div>
            </div>
            
            <div className="activity-item">
              <div className="activity-icon">📷</div>
              <div className="activity-content">
                <p>Evidence submitted for "Location Check"</p>
                <span className="activity-time">5 hours ago</span>
              </div>
            </div>
            
            <div className="activity-item">
              <div className="activity-icon">🎯</div>
              <div className="activity-content">
                <p>Started challenge "Identity Verification"</p>
                <span className="activity-time">1 day ago</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
