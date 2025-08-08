/**
 * Hook para manejo de errores asíncronos
 */

import React from 'react';
import { logger } from '../utils/logger';

export const useErrorHandler = () => {
  const handleError = React.useCallback((error: Error, context?: string) => {
    logger.error(error.message, context || 'ASYNC_ERROR', {
      stack: error.stack,
      timestamp: new Date().toISOString()
    });
  }, []);

  const handleAsyncError = React.useCallback(async <T>(
    asyncFn: () => Promise<T>,
    context?: string
  ): Promise<T | null> => {
    try {
      return await asyncFn();
    } catch (error) {
      handleError(error as Error, context);
      return null;
    }
  }, [handleError]);

  return { handleError, handleAsyncError };
};
