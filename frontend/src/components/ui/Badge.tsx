import React from 'react';
import { motion } from 'framer-motion';
import { cn } from '../../utils/cn';

export interface BadgeProps {
  className?: string;
  children: React.ReactNode;
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';
  size?: 'sm' | 'md' | 'lg';
  rounded?: 'sm' | 'md' | 'lg' | 'full';
  animate?: boolean;
  icon?: React.ReactNode;
  onClick?: () => void;
}

const variantClasses = {
  primary: 'bg-primary-100 text-primary-800 border-primary-200',
  secondary: 'bg-gray-100 text-gray-800 border-gray-200',
  success: 'bg-success-100 text-success-800 border-success-200',
  warning: 'bg-warning-100 text-warning-800 border-warning-200',
  error: 'bg-error-100 text-error-800 border-error-200',
  info: 'bg-blue-100 text-blue-800 border-blue-200'
};

const sizeClasses = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-2.5 py-0.5 text-xs',
  lg: 'px-3 py-1 text-sm'
};

const roundedClasses = {
  sm: 'rounded-sm',
  md: 'rounded-md',
  lg: 'rounded-lg',
  full: 'rounded-full'
};

export const Badge = React.forwardRef<HTMLSpanElement, BadgeProps>(
  ({
    className,
    children,
    variant = 'primary',
    size = 'md',
    rounded = 'full',
    animate = true,
    icon,
    onClick
  }, ref) => {
    const badgeClasses = cn(
      'inline-flex items-center font-medium border',
      variantClasses[variant],
      sizeClasses[size],
      roundedClasses[rounded],
      onClick && 'cursor-pointer hover:opacity-80 transition-opacity',
      className
    );

    const content = (
      <>
        {icon && <span className="mr-1">{icon}</span>}
        {children}
      </>
    );

    if (animate) {
      return (
        <motion.span
          ref={ref}
          className={badgeClasses}
          onClick={onClick}
          initial={{ scale: 0, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ type: 'spring', stiffness: 500, damping: 30 }}
          whileHover={onClick ? { scale: 1.05 } : undefined}
          whileTap={onClick ? { scale: 0.95 } : undefined}
        >
          {content}
        </motion.span>
      );
    }

    return (
      <span
        ref={ref}
        className={badgeClasses}
        onClick={onClick}
      >
        {content}
      </span>
    );
  }
);

Badge.displayName = 'Badge';
