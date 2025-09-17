import React from 'react';
import { Button } from './ui/Button';
import { AlertTriangle, RefreshCw } from 'lucide-react';

// Pre-generated ids for skeleton items to avoid using index as key
const SKELETON_IDS = ['a','b','c','d','e','f'];

interface DataStateProps {
  loading?: boolean;
  error?: string | null;
  empty?: boolean;
  skeleton?: React.ReactNode;
  onRetry?: () => void;
  emptyTitle?: React.ReactNode;
  emptyDescription?: React.ReactNode;
  children?: React.ReactNode;
}

/**
 * DataState centraliza los estados: loading -> error -> empty -> content.
 * Permite pasar un skeleton custom o usa uno básico.
 */
const DataState: React.FC<DataStateProps> = ({
  loading,
  error,
  empty,
  skeleton,
  onRetry,
  emptyTitle = 'Sin resultados',
  emptyDescription = 'No hay elementos para mostrar.',
  children
}) => {
  if (loading) {
    return (
      <div className="space-y-4" aria-busy="true" aria-live="polite">
        {skeleton || (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {SKELETON_IDS.map(id => (
              <div key={id} className="surface p-4 rounded-xl animate-pulse space-y-3">
                <div className="h-4 w-1/3 bg-gray-200 dark:bg-gray-700 rounded" />
                <div className="h-3 w-2/3 bg-gray-200 dark:bg-gray-700 rounded" />
                <div className="h-24 w-full bg-gray-200 dark:bg-gray-700 rounded" />
              </div>
            ))}
          </div>
        )}
      </div>
    );
  }

  if (error) {
    return (
      <div className="surface p-8 rounded-xl text-center" role="alert">
        <AlertTriangle className="w-10 h-10 text-error-600 mx-auto mb-4" />
        <h3 className="heading-3 mb-2">Ocurrió un error</h3>
        <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">{error}</p>
        {onRetry && (
          <Button onClick={onRetry} variant="primary" size="sm" icon={<RefreshCw className="w-4 h-4" />}>Reintentar</Button>
        )}
      </div>
    );
  }

  if (empty) {
    return (
      <div className="surface p-10 rounded-xl text-center">
        <h3 className="heading-3 mb-2">{emptyTitle}</h3>
        <p className="text-sm text-gray-600 dark:text-gray-400 mb-4">{emptyDescription}</p>
        {children && <div className="mt-4">{children}</div>}
      </div>
    );
  }

  return <>{children}</>;
};

export default DataState;
