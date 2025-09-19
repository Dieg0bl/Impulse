import React from 'react';
import { Loader2 } from 'lucide-react';

interface UpdatingStateProps {
  title?: string;
  description?: string;
  children?: React.ReactNode;
}

const UpdatingState: React.FC<UpdatingStateProps> = ({ title = 'Actualizando…', description = '', children }) => (
  <div className="surface p-8 rounded-xl text-center" aria-live="polite">
    <Loader2 className="w-10 h-10 text-primary-500 mx-auto mb-4 animate-spin" />
    <h3 className="heading-3 mb-2">{title}</h3>
    {description && <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">{description}</p>}
    {children && <div className="mt-4">{children}</div>}
  </div>
);

export default UpdatingState;
