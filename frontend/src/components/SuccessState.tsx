import React from 'react';
import { CheckCircle } from 'lucide-react';

interface SuccessStateProps {
  title?: string;
  description?: string;
  children?: React.ReactNode;
}

const SuccessState: React.FC<SuccessStateProps> = ({ title = '¡Éxito!', description = '', children }) => (
  <div className="surface p-8 rounded-xl text-center" role="status" aria-live="polite">
    <CheckCircle className="w-10 h-10 text-success-600 mx-auto mb-4" />
    <h3 className="heading-3 mb-2">{title}</h3>
    {description && <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">{description}</p>}
    {children && <div className="mt-4">{children}</div>}
  </div>
);

export default SuccessState;
