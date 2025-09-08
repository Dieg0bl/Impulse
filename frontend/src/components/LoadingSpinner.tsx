import React from 'react';

export const LoadingSpinner: React.FC = () => {
  return (
    <div className="loading-spinner">
      <div className="spinner-container">
        <div className="spinner"></div>
        <p>Loading...</p>
      </div>
    </div>
  );
};