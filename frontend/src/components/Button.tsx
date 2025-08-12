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
  href?: string;
}

const Button: React.FC<ButtonProps> = ({ 
  children, 
  onClick, 
  type = 'button', 
  disabled = false,
  variant = 'primary',
  size = 'medium',
  className = '',
  'aria-label': ariaLabel,
  href
}) => {

  // Extracción de ternarios anidados para cumplir con SonarLint
  let padding = '0.5rem 1rem';
  if (size === 'small') padding = '0.25rem 0.5rem';
  else if (size === 'large') padding = '0.75rem 1.5rem';

  let fontSize = '1rem';
  if (size === 'small') fontSize = '0.875rem';
  else if (size === 'large') fontSize = '1.125rem';

  const baseStyles = {
    padding,
    borderRadius: 6,
    border: 'none',
    fontWeight: 600,
    cursor: disabled ? 'not-allowed' : 'pointer',
    opacity: disabled ? 0.6 : 1,
    fontSize,
    transition: 'all 0.2s ease'
  };

  const variantStyles = {
    primary: { background: '#007bff', color: '#fff' },
    secondary: { background: '#6c757d', color: '#fff' },
    success: { background: '#28a745', color: '#fff' },
    danger: { background: '#dc3545', color: '#fff' },
    warning: { background: '#ffc107', color: '#212529' }
  };

  const style = { ...baseStyles, ...variantStyles[variant] };

  if (href) {
    return (
      <a
        href={href}
        className={className}
        aria-label={ariaLabel}
        style={style}
        tabIndex={disabled ? -1 : 0}
        onClick={e => {
          if (disabled) e.preventDefault();
          if (onClick) onClick();
        }}
      >
        {children}
      </a>
    );
  }
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={className}
      aria-label={ariaLabel}
      style={style}
    >
      {children}
    </button>
  );
};

export default Button;
