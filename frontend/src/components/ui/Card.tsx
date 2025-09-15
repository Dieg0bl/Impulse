import React from 'react';
import { motion } from 'framer-motion';
import { cn } from '../../utils/cn';

export interface CardProps {
  className?: string;
  children: React.ReactNode;
  hover?: boolean;
  animate?: boolean;
  padding?: 'none' | 'sm' | 'md' | 'lg' | 'xl';
  shadow?: 'none' | 'soft' | 'medium' | 'large';
  rounded?: 'none' | 'sm' | 'md' | 'lg' | 'xl' | '2xl';
  onClick?: () => void;
}

const paddingVariants = {
  none: '',
  sm: 'p-4',
  md: 'p-6',
  lg: 'p-8',
  xl: 'p-10'
};

const shadowVariants = {
  none: '',
  soft: 'shadow-soft',
  medium: 'shadow-medium',
  large: 'shadow-large'
};

const roundedVariants = {
  none: 'rounded-none',
  sm: 'rounded-sm',
  md: 'rounded-md',
  lg: 'rounded-lg',
  xl: 'rounded-xl',
  '2xl': 'rounded-2xl'
};

export const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({
    className,
    children,
    hover = false,
    animate = true,
    padding = 'md',
    shadow = 'soft',
    rounded = 'xl',
    onClick
  }, ref) => {
    const cardClasses = cn(
      'bg-white border border-gray-200',
      paddingVariants[padding],
      shadowVariants[shadow],
      roundedVariants[rounded],
      hover && 'card-hover cursor-pointer',
      onClick && 'cursor-pointer',
      className
    );

    if (animate) {
      return (
        <motion.div
          ref={ref}
          className={cardClasses}
          onClick={onClick}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
          whileHover={hover ? {
            scale: 1.02,
            transition: { duration: 0.2 }
          } : undefined}
        >
          {children}
        </motion.div>
      );
    }

    return (
      <div
        ref={ref}
        className={cardClasses}
        onClick={onClick}
      >
        {children}
      </div>
    );
  }
);

Card.displayName = 'Card';

// Card sub-components
export const CardHeader = React.forwardRef<HTMLDivElement, { className?: string; children: React.ReactNode }>(
  ({ className, children }, ref) => (
    <div ref={ref} className={cn('flex flex-col space-y-1.5 pb-6', className)}>
      {children}
    </div>
  )
);

CardHeader.displayName = 'CardHeader';

export const CardTitle = React.forwardRef<HTMLHeadingElement, { className?: string; children: React.ReactNode }>(
  ({ className, children }, ref) => (
    <h3 ref={ref} className={cn('text-xl font-semibold text-gray-900', className)}>
      {children}
    </h3>
  )
);

CardTitle.displayName = 'CardTitle';

export const CardDescription = React.forwardRef<HTMLParagraphElement, { className?: string; children: React.ReactNode }>(
  ({ className, children }, ref) => (
    <p ref={ref} className={cn('text-sm text-gray-600', className)}>
      {children}
    </p>
  )
);

CardDescription.displayName = 'CardDescription';

export const CardContent = React.forwardRef<HTMLDivElement, { className?: string; children: React.ReactNode }>(
  ({ className, children }, ref) => (
    <div ref={ref} className={cn('pt-0', className)}>
      {children}
    </div>
  )
);

CardContent.displayName = 'CardContent';

export const CardFooter = React.forwardRef<HTMLDivElement, { className?: string; children: React.ReactNode }>(
  ({ className, children }, ref) => (
    <div ref={ref} className={cn('flex items-center pt-6', className)}>
      {children}
    </div>
  )
);

CardFooter.displayName = 'CardFooter';
