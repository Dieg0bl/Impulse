import React, { Component, ReactNode } from 'react';

interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
  errorInfo?: any;
}

interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
}

class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: any) {
    console.error('Error capturado por ErrorBoundary:', error, errorInfo);
    this.setState({ errorInfo });
  }

  handleReload = () => {
    window.location.reload();
  };

  handleGoHome = () => {
    window.location.href = '/';
  };

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="error-boundary">
          <div className="error-container">
            <div className="error-icon">⚠️</div>
            <h1 className="error-title">¡Oops! Algo salió mal</h1>
            <p className="error-message">
              Ha ocurrido un error inesperado. Por favor, intenta recargar la página 
              o contacta con soporte si el problema persiste.
            </p>
            
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <details className="error-details">
                <summary>Detalles del error (solo en desarrollo)</summary>
                <pre className="error-stack">
                  {this.state.error.toString()}
                  {this.state.errorInfo?.componentStack}
                </pre>
              </details>
            )}
            
            <div className="error-actions">
              <button 
                className="btn btn-primary"
                onClick={this.handleReload}
                aria-label="Recargar página"
              >
                🔄 Recargar Página
              </button>
              <button 
                className="btn btn-secondary"
                onClick={this.handleGoHome}
                aria-label="Ir al inicio"
              >
                🏠 Ir al Inicio
              </button>
            </div>
            
            <div className="error-support">
              <p className="support-text">
                Si necesitas ayuda, contacta con soporte:
              </p>
              <a 
                href="mailto:soporte@impulse.dev" 
                className="support-link"
                aria-label="Contactar soporte por email"
              >
                📧 soporte@impulse.dev
              </a>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
