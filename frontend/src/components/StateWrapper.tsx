import React from 'react';
import DataState from './DataState';
import SuccessState from './SuccessState';
import IdleState from './IdleState';
import UpdatingState from './UpdatingState';

interface StateWrapperProps {
  loading?: boolean;
  error?: string | null;
  empty?: boolean;
  success?: boolean;
  idle?: boolean;
  updating?: boolean;
  children?: React.ReactNode;
  emptyTitle?: string;
  emptyDescription?: string;
  successTitle?: string;
  successDescription?: string;
}

/**
 * Wrapper universal para manejar todos los estados de una vista o sección.
 * Prioridad: loading > error > updating > success > empty > idle > content
 */
const StateWrapper: React.FC<StateWrapperProps> = ({
  loading,
  error,
  updating,
  success,
  idle,
  empty,
  children,
  emptyTitle,
  emptyDescription,
  successTitle,
  successDescription,
}) => {
  if (loading) return <DataState loading />;
  if (error) return <DataState error={error} />;
  if (updating) return <UpdatingState title="Actualizando…" />;
  if (success) return <SuccessState title={successTitle} description={successDescription} />;
  if (empty) return <DataState empty emptyTitle={emptyTitle} emptyDescription={emptyDescription} />;
  if (idle) return <IdleState />;
  return <>{children}</>;
};

export default StateWrapper;
