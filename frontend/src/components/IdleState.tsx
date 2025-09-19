import React from 'react';
import { PauseCircle } from 'lucide-react';

interface IdleStateProps {
  title?: string;
  description?: string;
  children?: React.ReactNode;
  // legacy: some pages pass an onResume handler
  onResume?: () => void;
}

const IdleState: React.FC<IdleStateProps> = ({ title = 'En espera', description = '', children, onResume }) => (
  <div className="surface p-8 rounded-xl text-center" aria-live="polite">
    <PauseCircle className="w-10 h-10 text-gray-400 mx-auto mb-4" />
    <h3 className="heading-3 mb-2">{title}</h3>
    {description && <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">{description}</p>}
    {children && <div className="mt-4">{children}</div>}
    {onResume && (
      <div className="mt-4">
        <button type="button" onClick={onResume} className="btn btn-primary">Reanudar</button>
      </div>
    )}
  </div>
);

export { IdleState };
export default IdleState;
