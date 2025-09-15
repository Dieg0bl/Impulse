import React from 'react';
import { motion } from 'framer-motion';
import { cn } from '../../utils/cn';

export interface ProgressProps {
  value: number;
  max?: number;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
  variant?: 'primary' | 'success' | 'warning' | 'error';
  showValue?: boolean;
  animate?: boolean;
  striped?: boolean;
  label?: string;
}

const sizeClasses = {
  sm: 'h-2',
  md: 'h-3',
  lg: 'h-4'
};

const variantClasses = {
  primary: 'bg-primary-600',
  success: 'bg-success-600',
  warning: 'bg-warning-500',
  error: 'bg-error-600'
};

export const Progress = React.forwardRef<HTMLDivElement, ProgressProps>(
  ({
    value,
    max = 100,
    className,
    size = 'md',
    variant = 'primary',
    showValue = false,
    animate = true,
    striped = false,
    label
  }, ref) => {
    const percentage = Math.min(Math.max((value / max) * 100, 0), 100);

    const containerClasses = cn(
      'w-full bg-gray-200 rounded-full overflow-hidden',
      sizeClasses[size],
      className
    );

    const barClasses = cn(
      'h-full transition-all duration-300 ease-out',
      variantClasses[variant],
      striped && 'bg-stripes'
    );

    const ProgressBar = animate ? motion.div : 'div';

    return (
      <div className="space-y-2">
        {(label || showValue) && (
          <div className="flex justify-between items-center">
            {label && (
              <span className="text-sm font-medium text-gray-700">{label}</span>
            )}
            {showValue && (
              <span className="text-sm text-gray-500">
                {Math.round(percentage)}%
              </span>
            )}
          </div>
        )}

        <div ref={ref} className={containerClasses} role="progressbar" aria-valuenow={value} aria-valuemax={max}>
          <ProgressBar
            className={barClasses}
            style={{ width: `${percentage}%` }}
            {...(animate && {
              initial: { width: 0 },
              animate: { width: `${percentage}%` },
              transition: {
                duration: 0.8,
                ease: [0.16, 1, 0.3, 1]
              }
            })}
          />
        </div>
      </div>
    );
  }
);

Progress.displayName = 'Progress';
