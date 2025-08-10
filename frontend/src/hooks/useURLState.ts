import { useEffect, useCallback, useState } from 'react';
import urlStateService, { PageState, URLStateOptions } from '../services/urlStateService';

/**
 * Hook para gestión avanzada de estado en URLs
 */
export function useURLState(pageId: string, options: URLStateOptions = {}) {
  const [pageState, setPageState] = useState<PageState | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Inicializar estado de página
  useEffect(() => {
    const initializePageState = async () => {
      try {
        setIsLoading(true);
        setError(null);

        // Intentar restaurar desde URL primero
        const restoredState = await urlStateService.restoreFromURL();
        
        if (restoredState && restoredState.pageId === pageId) {
          setPageState(restoredState);
        } else {
          // Crear nuevo estado si no hay restauración
          const { pageState: newState } = await urlStateService.createPageState(pageId, options);
          setPageState(newState);
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Error inicializando estado');
      } finally {
        setIsLoading(false);
      }
    };

    initializePageState();
  }, [pageId]);

  // Actualizar estado de vista
  const updateViewState = useCallback(async (key: string, value: any) => {
    try {
      setError(null);
      const { pageState: updatedState } = await urlStateService.updateViewState(key, value, options);
      setPageState(updatedState);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error actualizando vista');
    }
  }, [options]);

  // Actualizar filtros
  const updateFilters = useCallback(async (filters: Record<string, any>) => {
    try {
      setError(null);
      const { pageState: updatedState } = await urlStateService.updateFilters(filters, options);
      setPageState(updatedState);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error actualizando filtros');
    }
  }, [options]);

  // Guardar formulario
  const saveFormState = useCallback(async (formData: Record<string, any>) => {
    try {
      setError(null);
      const { pageState: updatedState } = await urlStateService.saveFormState(formData, options);
      setPageState(updatedState);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error guardando formulario');
    }
  }, [options]);

  // Obtener valor específico de vista
  const getViewState = useCallback((key: string) => {
    return urlStateService.getViewState(key);
  }, []);

  // Obtener filtros actuales
  const getFilters = useCallback(() => {
    return urlStateService.getFilters();
  }, []);

  // Obtener estado de formulario
  const getFormState = useCallback(() => {
    return urlStateService.getFormState();
  }, []);

  // Crear bookmark
  const createBookmark = useCallback((title?: string) => {
    return urlStateService.createBookmark(title);
  }, []);

  // Verificar si hay datos de formulario
  const hasFormState = useCallback(() => {
    return urlStateService.hasFormState();
  }, []);

  return {
    pageState,
    isLoading,
    error,
    updateViewState,
    updateFilters,
    saveFormState,
    getViewState,
    getFilters,
    getFormState,
    createBookmark,
    hasFormState,
  };
}

/**
 * Hook para gestión de filtros con sincronización automática
 */
export function useURLFilters(pageId: string, initialFilters: Record<string, any> = {}) {
  const { updateFilters, getFilters, isLoading } = useURLState(pageId, { autoSync: true });
  const [filters, setFilters] = useState<Record<string, any>>(initialFilters);

  // Sincronizar filtros del estado restaurado
  useEffect(() => {
    if (!isLoading) {
      const urlFilters = getFilters();
      if (Object.keys(urlFilters).length > 0) {
        setFilters(urlFilters);
      }
    }
  }, [isLoading, getFilters]);

  // Actualizar filtro individual
  const updateFilter = useCallback(async (key: string, value: any) => {
    const newFilters = { ...filters, [key]: value };
    setFilters(newFilters);
    await updateFilters(newFilters);
  }, [filters, updateFilters]);

  // Limpiar filtro específico
  const clearFilter = useCallback(async (key: string) => {
    const newFilters = { ...filters };
    delete newFilters[key];
    setFilters(newFilters);
    await updateFilters(newFilters);
  }, [filters, updateFilters]);

  // Limpiar todos los filtros
  const clearAllFilters = useCallback(async () => {
    setFilters({});
    await updateFilters({});
  }, [updateFilters]);

  // Aplicar múltiples filtros
  const applyFilters = useCallback(async (newFilters: Record<string, any>) => {
    setFilters(newFilters);
    await updateFilters(newFilters);
  }, [updateFilters]);

  return {
    filters,
    updateFilter,
    clearFilter,
    clearAllFilters,
    applyFilters,
    isLoading,
  };
}

/**
 * Hook para recuperación automática de formularios
 */
export function useFormRecovery(pageId: string, formId: string) {
  const { saveFormState, getFormState, hasFormState } = useURLState(pageId, { 
    persistFormData: true,
    debounceMs: 1000 
  });

  // Guardar datos de formulario
  const saveForm = useCallback(async (formData: Record<string, any>) => {
    const formKey = `form_${formId}`;
    await saveFormState({ [formKey]: formData });
  }, [formId, saveFormState]);

  // Recuperar datos de formulario
  const recoverForm = useCallback(() => {
    const formKey = `form_${formId}`;
    const formState = getFormState();
    return formState[formKey] || {};
  }, [formId, getFormState]);

  // Verificar si hay datos guardados
  const hasRecoveryData = useCallback(() => {
    const formKey = `form_${formId}`;
    const formState = getFormState();
    return !!formState[formKey] && Object.keys(formState[formKey]).length > 0;
  }, [formId, getFormState]);

  return {
    saveForm,
    recoverForm,
    hasRecoveryData,
    hasAnyFormState: hasFormState,
  };
}

export default useURLState;
