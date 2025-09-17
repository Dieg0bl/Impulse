import React from 'react';
import { motion } from 'framer-motion';

interface PageHeaderProps {
  title: React.ReactNode;
  subtitle?: React.ReactNode;
  actions?: React.ReactNode;
  className?: string;
  children?: React.ReactNode; // for extra content below (tabs, filters, etc.)
}

const PageHeader: React.FC<PageHeaderProps> = ({ title, subtitle, actions, className = '', children }) => {
  return (
    <header className={`mb-6 md:mb-8 ${className}`}>
      <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4 md:gap-6">
        <div className="space-y-2 md:space-y-3 max-w-3xl">
          <motion.h1
            layout
            className="heading-1 text-gray-900 dark:text-white"
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.25 }}
          >
            {title}
          </motion.h1>
          {subtitle && (
            <p className="text-gray-600 dark:text-gray-300 text-fluid-lg leading-relaxed measure">
              {subtitle}
            </p>
          )}
        </div>
        {actions && (
          <div className="flex items-center gap-3 md:justify-end" aria-label="Acciones de página">
            {actions}
          </div>
        )}
      </div>
      {children && <div className="mt-4">{children}</div>}
    </header>
  );
};

export default PageHeader;
