import { logger } from '../utils/logger.ts';

export interface PageState {
  pageId: string;
  viewState: Record<string, any>;
  filterState: Record<string, any>;
  formState: Record<string, any>;
  timestamp: string;
  userId: string;
  metadata: Record<string, any>;
}

export interface PageStateResponse {
  pageState: PageState;
  queryString: string;
}

export interface URLStateOptions {
  autoSync?: boolean;
  enableBookmarking?: boolean;
  persistFormData?: boolean;
  debounceMs?: number;
}

/**
 * Servicio para gestión avanzada de estado en URLs.
 * Proporciona bookmarking inteligente y sincronización automática.
 */
class URLStateService {
  private currentState: PageState | null = null;
  private syncTimer: number | null = null;
  private navigationContext: any = null;
  private isInitialized = false;

  /**
   * Inicializar el servicio con contexto de navegación
   */
  initialize(navigationContext: any): void {
    this.navigationContext = navigationContext;
    this.isInitialized = true;
    
    // Restaurar estado al cargar la página
    this.restoreFromURL();
    
    // Escuchar cambios de URL del navegador
    window.addEventListener('popstate', this.handlePopState.bind(this));
  }

  /**
   * Crear estado inicial para una página
   */
  async createPageState(pageId: string, options: URLStateOptions = {}): Promise<PageStateResponse> {
    try {
      const response = await fetch('/api/navigation/state/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({ pageId }),
      });

      if (!response.ok) {
        throw new Error('Error creando estado de página');
      }

      const result: PageStateResponse = await response.json();
      this.currentState = result.pageState;

      // Sincronizar con URL si está habilitado
      if (options.autoSync !== false) {
        this.syncToURL(options);
      }

      return result;
    } catch (error) {
      logger.error('Error creando estado de página', 'URL_STATE', error);
      throw error;
    }
  }

  /**
   * Actualizar estado de vista (filtros, paginación, ordenamiento)
   */
  async updateViewState(
    key: string, 
    value: any, 
    options: URLStateOptions = {}
  ): Promise<PageStateResponse> {
    if (!this.currentState) {
      throw new Error('No hay estado de página activo');
    }

    try {
      const response = await fetch('/api/navigation/state/view', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
          currentState: this.buildCurrentQueryString(),
          key,
          value,
        }),
      });

      if (!response.ok) {
        throw new Error('Error actualizando estado de vista');
      }

      const result: PageStateResponse = await response.json();
      this.currentState = result.pageState;

      // Sincronizar con URL
      this.debouncedSync(options);

      return result;
    } catch (error) {
      logger.error('Error actualizando estado de vista', 'URL_STATE', error);
      throw error;
    }
  }

  /**
   * Actualizar múltiples filtros
   */
  async updateFilters(
    filters: Record<string, any>, 
    options: URLStateOptions = {}
  ): Promise<PageStateResponse> {
    if (!this.currentState) {
      throw new Error('No hay estado de página activo');
    }

    try {
      const response = await fetch('/api/navigation/state/filters', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
          currentState: this.buildCurrentQueryString(),
          filters,
        }),
      });

      if (!response.ok) {
        throw new Error('Error actualizando filtros');
      }

      const result: PageStateResponse = await response.json();
      this.currentState = result.pageState;

      // Sincronizar con URL
      this.debouncedSync(options);

      return result;
    } catch (error) {
      logger.error('Error actualizando filtros', 'URL_STATE', error);
      throw error;
    }
  }

  /**
   * Guardar estado de formulario para recuperación
   */
  async saveFormState(
    formData: Record<string, any>, 
    options: URLStateOptions = {}
  ): Promise<PageStateResponse> {
    if (!this.currentState) {
      throw new Error('No hay estado de página activo');
    }

    try {
      const response = await fetch('/api/navigation/state/form', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
          currentState: this.buildCurrentQueryString(),
          formData,
        }),
      });

      if (!response.ok) {
        throw new Error('Error guardando formulario');
      }

      const result: PageStateResponse = await response.json();
      this.currentState = result.pageState;

      // Solo sincronizar si está explícitamente habilitado para formularios
      if (options.persistFormData) {
        this.debouncedSync(options);
      }

      return result;
    } catch (error) {
      logger.error('Error guardando formulario', 'URL_STATE', error);
      throw error;
    }
  }

  /**
   * Restaurar estado desde URL/bookmark
   */
  async restoreFromURL(): Promise<PageState | null> {
    const queryString = window.location.search.substring(1);
    
    if (!queryString) {
      return null;
    }

    try {
      const response = await fetch('/api/navigation/state/restore', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({ queryString }),
      });

      if (!response.ok) {
        return null;
      }

      const pageState: PageState = await response.json();
      this.currentState = pageState;

      // Aplicar estado a la interfaz
      this.applyStateToInterface(pageState);

      return pageState;
    } catch (error) {
      logger.error('Error restaurando estado', 'URL_STATE', error);
      return null;
    }
  }

  /**
   * Crear bookmarks dinámicos con estado completo
   */
  createBookmark(title?: string): string {
    if (!this.currentState) {
      return window.location.href;
    }

    const baseUrl = `${window.location.protocol}//${window.location.host}${window.location.pathname}`;
    const queryString = this.buildCurrentQueryString();
    
    return `${baseUrl}?${queryString}`;
  }

  /**
   * Obtener estado actual
   */
  getCurrentState(): PageState | null {
    return this.currentState;
  }

  /**
   * Obtener valor específico del estado de vista
   */
  getViewState(key: string): any {
    return this.currentState?.viewState[key];
  }

  /**
   * Obtener filtros actuales
   */
  getFilters(): Record<string, any> {
    return this.currentState?.filterState || {};
  }

  /**
   * Obtener datos de formulario guardados
   */
  getFormState(): Record<string, any> {
    return this.currentState?.formState || {};
  }

  /**
   * Verificar si hay estado de formulario guardado
   */
  hasFormState(): boolean {
    return Object.keys(this.getFormState()).length > 0;
  }

  // Métodos privados

  private buildCurrentQueryString(): string {
    if (!this.currentState) return '';
    
    // Aquí se construiría el query string desde el estado actual
    // Por simplicidad, retornamos los parámetros actuales
    return window.location.search.substring(1);
  }

  private syncToURL(options: URLStateOptions = {}): void {
    if (!this.currentState || !this.isInitialized) return;

    const queryString = this.buildCurrentQueryString();
    const newUrl = `${window.location.pathname}?${queryString}`;
    
    // Usar History API para evitar recargar la página
    if (options.enableBookmarking !== false) {
      window.history.pushState(this.currentState, '', newUrl);
    } else {
      window.history.replaceState(this.currentState, '', newUrl);
    }
  }

  private debouncedSync(options: URLStateOptions = {}): void {
    if (this.syncTimer) {
      clearTimeout(this.syncTimer);
    }

    const debounceMs = options.debounceMs || 300;
    
    this.syncTimer = setTimeout(() => {
      this.syncToURL(options);
    }, debounceMs);
  }

  private handlePopState(event: PopStateEvent): void {
    if (event.state) {
      this.currentState = event.state as PageState;
      this.applyStateToInterface(this.currentState);
    } else {
      // Manejar navegación hacia atrás sin estado
      this.restoreFromURL();
    }
  }

  private applyStateToInterface(pageState: PageState): void {
    // Notificar al contexto de navegación sobre el cambio de estado
    if (this.navigationContext) {
      // Aplicar filtros
      Object.entries(pageState.filterState).forEach(([key, value]) => {
        this.navigationContext?.setFilter?.(key, value);
      });

      // Aplicar estado de vista
      Object.entries(pageState.viewState).forEach(([key, value]) => {
        this.navigationContext?.setViewState?.(key, value);
      });
    }

    // Disparar evento personalizado para que los componentes reaccionen
    window.dispatchEvent(new CustomEvent('urlstate:restored', {
      detail: { pageState }
    }));
  }

  /**
   * Limpiar recursos
   */
  cleanup(): void {
    if (this.syncTimer) {
      clearTimeout(this.syncTimer);
    }
    window.removeEventListener('popstate', this.handlePopState.bind(this));
    this.isInitialized = false;
  }
}

export const urlStateService = new URLStateService();
export default urlStateService;
