import React from 'react';
import { motion } from 'framer-motion';
import { cn } from '../../utils/cn';

export interface ButtonProps {
  className?: string;
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'ghost' | 'outline';
  size?: 'sm' | 'md' | 'lg' | 'xl';
  loading?: boolean;
  icon?: React.ReactNode;
  iconPosition?: 'left' | 'right';
  fullWidth?: boolean;
  animate?: boolean;
  disabled?: boolean;
  onClick?: (e: React.MouseEvent<HTMLButtonElement>) => void;
  type?: 'button' | 'submit' | 'reset';
  children?: React.ReactNode;
}

const buttonVariants = {
  primary: 'btn-primary',
  secondary: 'btn-secondary',
  success: 'btn-success',
  warning: 'btn-warning',
  error: 'btn-error',
  ghost: 'btn-ghost',
  outline: 'btn-outline'
};

const buttonSizes = {
  sm: 'btn-sm',
  md: 'btn-md',
  lg: 'btn-lg',
  xl: 'btn-xl'
};

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({
    className,
    variant = 'primary',
    size = 'md',
    loading = false,
    icon,
    iconPosition = 'left',
    fullWidth = false,
    animate = true,
    children,
    disabled,
    onClick,
    type = 'button'
  }, ref) => {
    const buttonClasses = cn(
      'btn',
      buttonVariants[variant],
      buttonSizes[size],
      fullWidth && 'w-full',
      loading && 'cursor-wait',
      className
    );

    const buttonContent = (
      <>
        {loading ? (
          <div className="flex items-center gap-2">
            <div className="spinner w-4 h-4"></div>
            {children && <span>Loading...</span>}
          </div>
        ) : (
          <div className="flex items-center gap-2">
            {icon && iconPosition === 'left' && (
              <span className="flex-shrink-0">{icon}</span>
            )}
            {children && <span>{children}</span>}
            {icon && iconPosition === 'right' && (
              <span className="flex-shrink-0">{icon}</span>
            )}
          </div>
        )}
      </>
    );

    if (animate) {
      return (
        <motion.button
          className={buttonClasses}
          ref={ref}
          disabled={disabled || loading}
          onClick={onClick}
          type={type}
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          transition={{ type: "spring", stiffness: 400, damping: 17 }}
        >
          {buttonContent}
        </motion.button>
      );
    }

    return (
      <button
        className={buttonClasses}
        ref={ref}
        disabled={disabled || loading}
        onClick={onClick}
        type={type}
      >
        {buttonContent}
      </button>
    );
  }
);

Button.displayName = 'Button';
