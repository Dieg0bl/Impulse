import React from 'react';

export const ProfilePage: React.FC = () => {
  return (
    <div className="profile-page">
      <div className="container">
        <div className="profile-header">
          <div className="profile-avatar">
            <img src="/default-avatar.png" alt="Profile" />
          </div>
          <div className="profile-info">
            <h1>John Doe</h1>
            <p className="profile-email">john.doe@example.com</p>
            <span className="profile-status active">Active</span>
          </div>
          <button className="btn btn-outline">Edit Profile</button>
        </div>
        
        <div className="profile-stats">
          <div className="stat-card">
            <h3>Total Challenges</h3>
            <div className="stat-value">23</div>
          </div>
          <div className="stat-card">
            <h3>Completed</h3>
            <div className="stat-value">18</div>
          </div>
          <div className="stat-card">
            <h3>Success Rate</h3>
            <div className="stat-value">78%</div>
          </div>
          <div className="stat-card">
            <h3>Total Earned</h3>
            <div className="stat-value">€450</div>
          </div>
        </div>
        
        <div className="profile-sections">
          <div className="profile-section">
            <h2>Account Settings</h2>
            <div className="settings-group">
              <div className="setting-item">
                <label htmlFor="email">Email Address</label>
                <input 
                  type="email" 
                  id="email" 
                  value="john.doe@example.com" 
                  readOnly 
                />
              </div>
              
              <div className="setting-item">
                <label htmlFor="username">Username</label>
                <input 
                  type="text" 
                  id="username" 
                  value="johndoe" 
                  readOnly 
                />
              </div>
              
              <div className="setting-item">
                <label htmlFor="notifications">Email Notifications</label>
                <input 
                  type="checkbox" 
                  id="notifications" 
                  defaultChecked 
                />
              </div>
            </div>
          </div>
          
          <div className="profile-section">
            <h2>Security</h2>
            <div className="security-group">
              <button className="btn btn-outline">Change Password</button>
              <button className="btn btn-outline">Two-Factor Authentication</button>
              <button className="btn btn-outline">Download Data</button>
            </div>
          </div>
          
          <div className="profile-section">
            <h2>Recent Activity</h2>
            <div className="activity-feed">
              <div className="activity-item">
                <div className="activity-date">Today</div>
                <div className="activity-desc">Completed Photo Verification challenge</div>
              </div>
              <div className="activity-item">
                <div className="activity-date">Yesterday</div>
                <div className="activity-desc">Started Identity Check challenge</div>
              </div>
              <div className="activity-item">
                <div className="activity-date">2 days ago</div>
                <div className="activity-desc">Submitted evidence for Location Verification</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
