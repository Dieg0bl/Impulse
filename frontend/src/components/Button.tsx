import * as React from 'react';

interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  type?: 'button' | 'submit' | 'reset';
  disabled?: boolean;
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning';
  size?: 'small' | 'medium' | 'large';
  className?: string;
  'aria-label'?: string;
}

const Button: React.FC<ButtonProps> = ({ 
  children, 
  onClick, 
  type = 'button', 
  disabled = false,
  variant = 'primary',
  size = 'medium',
  className = '',
  'aria-label': ariaLabel
}) => {
  const getVariantStyles = () => {
    const baseStyles = {
      padding: size === 'small' ? '0.25rem 0.5rem' : size === 'large' ? '0.75rem 1.5rem' : '0.5rem 1rem',
      borderRadius: 6,
      border: 'none',
      fontWeight: 600,
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.6 : 1,
      fontSize: size === 'small' ? '0.875rem' : size === 'large' ? '1.125rem' : '1rem',
      transition: 'all 0.2s ease'
    };

    const variantStyles = {
      primary: { background: '#007bff', color: '#fff' },
      secondary: { background: '#6c757d', color: '#fff' },
      success: { background: '#28a745', color: '#fff' },
      danger: { background: '#dc3545', color: '#fff' },
      warning: { background: '#ffc107', color: '#212529' }
    };

    return { ...baseStyles, ...variantStyles[variant] };
  };

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={className}
      aria-label={ariaLabel}
      style={getVariantStyles()}
    >
      {children}
    </button>
  );
};

export default Button;
