import * as React from 'react';

interface LoadingSpinnerProps {
  message?: string;
  size?: 'small' | 'medium' | 'large';
  centered?: boolean;
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ 
  message = 'Cargando...', 
  size = 'medium',
  centered = true 
}) => {
  const sizeClasses = {
    small: 'spinner-small',
    medium: 'spinner-medium',
    large: 'spinner-large'
  };

  const containerClass = centered ? 'loading-container centered' : 'loading-container';

  return (
    <div className={containerClass}>
      <div className={`loading-spinner ${sizeClasses[size]}`} role="status" aria-label="Cargando">
        <div className="spinner-circle">
          <span className="spinner-icon">⚡</span>
        </div>
      </div>
      {message && (
        <p className="loading-message" aria-live="polite">
          {message}
        </p>
      )}
    </div>
  );
};

export default LoadingSpinner;
