/**
 * Sistema de manejo de errores unificado para IMPULSE
 * Reemplaza manejo inconsistente de errores
 */

import React, { Component, ErrorInfo, ReactNode } from 'react';
import { logger } from '../utils/logger.ts';

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: any;
  errorId: string;
}

interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
  onError?: (error: Error, errorInfo: any) => void;
  level?: 'page' | 'component' | 'critical';
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
      errorId: ''
    };
  }

  static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    const errorId = `ERR_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    return {
      hasError: true,
      error,
      errorId
    };
  }

  componentDidCatch(error: Error, errorInfo: any): void {
    const { onError, level = 'component' } = this.props;
    
    // Log del error usando sistema profesional
    logger.error(
      `ErrorBoundary caught error at ${level} level`,
      'ERROR_BOUNDARY',
      {
        error: error.message,
        stack: error.stack,
        errorInfo,
        errorId: this.state.errorId,
        level
      }
    );

    // Callback personalizado
    if (onError) {
      onError(error, errorInfo);
    }

    // Actualizar estado
    this.setState({
      errorInfo
    });

    // Para errores críticos, reportar al sistema de monitoreo
    if (level === 'critical') {
      this.reportCriticalError(error, errorInfo);
    }
  }

  private reportCriticalError(error: Error, errorInfo: any): void {
    // Implementar reporte a sistema de monitoreo externo
    try {
      fetch('/api/errors/critical', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          error: error.message,
          stack: error.stack,
          errorInfo,
          timestamp: new Date().toISOString(),
          userAgent: navigator.userAgent,
          url: window.location.href,
          userId: this.getCurrentUserId()
        })
      }).catch(() => {
        // Silencioso en caso de fallo
      });
    } catch {
      // Silencioso en caso de fallo
    }
  }

  private getCurrentUserId(): string | null {
    try {
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      return user.id || null;
    } catch {
      return null;
    }
  }

  private handleRetry = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
      errorId: ''
    });
  };

  handleReload = () => {
    window.location.reload();
  };

  handleGoHome = () => {
    window.location.href = '/';
  };

  render() {
    const { hasError, error, errorId } = this.state;
    const { children, fallback, level = 'component' } = this.props;

    if (hasError) {
      // Fallback personalizado
      if (fallback) {
        return fallback;
      }

      // UI por defecto según nivel
      return this.renderErrorUI(level, error, errorId);
    }

    return children;
  }

  private renderErrorUI(level: string, error: Error | null, errorId: string): ReactNode {
    const isCritical = level === 'critical';
    const isPage = level === 'page';

    return (
      <div className={`error-boundary ${level}`}>
        <div className="error-container">
          <div className="error-icon">
            {isCritical ? '💥' : isPage ? '📄' : '⚠️'}
          </div>
          
          <h2 className="error-title">
            {isCritical 
              ? 'Error Crítico' 
              : isPage 
                ? 'Error en la Página' 
                : 'Algo salió mal'
            }
          </h2>
          
          <p className="error-message">
            {isCritical 
              ? 'Se ha producido un error crítico en la aplicación.' 
              : isPage
                ? 'No se pudo cargar esta página correctamente.'
                : 'Este componente no está funcionando correctamente.'
            }
          </p>

          {error && window.location.hostname === 'localhost' && (
            <details className="error-details">
              <summary>Detalles técnicos (solo en desarrollo)</summary>
              <pre className="error-stack">
                {error.message}
                {error.stack && `\n\n${error.stack}`}
              </pre>
              <p className="error-id">ID del Error: {errorId}</p>
            </details>
          )}

          <div className="error-actions">
            {!isCritical && (
              <button 
                onClick={this.handleRetry}
                className="btn btn-primary"
              >
                🔄 Reintentar
              </button>
            )}
            
            <button 
              onClick={this.handleReload}
              className={`btn ${isCritical ? 'btn-primary' : 'btn-secondary'}`}
            >
              🔄 Recargar página
            </button>
            
            {isCritical && (
              <button 
                onClick={() => window.location.href = '/'}
                className="btn btn-outline"
              >
                🏠 Ir al inicio
              </button>
            )}
          </div>
        </div>
      </div>
    );
  }
}

export default ErrorBoundary;
