/**
 * Sistema de Logging Profesional para IMPULSE
 * Reemplaza todos los console.log con logging estructurado
 */

export enum LogLevel {
  ERROR = 0,
  WARN = 1,
  INFO = 2,
  DEBUG = 3,
  TRACE = 4
}

interface LogEntry {
  timestamp: string;
  level: LogLevel;
  message: string;
  context?: string;
  data?: any;
  userId?: string;
}

class Logger {
  private static instance: Logger;
  private logLevel: LogLevel = LogLevel.INFO;
  private isDevelopment: boolean = (typeof window !== 'undefined') && !!(window.location && window.location.hostname === 'localhost');

  private constructor() {}

  public static getInstance(): Logger {
    if (!Logger.instance) {
      Logger.instance = new Logger();
    }
    return Logger.instance;
  }

  public setLogLevel(level: LogLevel): void {
    this.logLevel = level;
  }

  private shouldLog(level: LogLevel): boolean {
    return level <= this.logLevel;
  }

  private formatLog(entry: LogEntry): string {
    const { timestamp, level, message, context, data, userId } = entry;
    const levelName = LogLevel[level];
    let logMessage = `[${timestamp}] ${levelName}`;
    
    if (context) logMessage += ` [${context}]`;
    if (userId) logMessage += ` [User: ${userId}]`;
    
    logMessage += `: ${message}`;
    
    if (data) {
      logMessage += ` | Data: ${JSON.stringify(data)}`;
    }
    
    return logMessage;
  }

  private createLogEntry(level: LogLevel, message: string, context?: string, data?: any): LogEntry {
    return {
      timestamp: new Date().toISOString(),
      level,
      message,
      context,
      data,
      userId: this.getCurrentUserId()
    };
  }

  private getCurrentUserId(): string | undefined {
    try {
      if (typeof localStorage === 'undefined') return undefined;
      const raw = localStorage.getItem('user');
      if (!raw) return undefined;
      const user = JSON.parse(raw);
      return user?.id;
    } catch {
      return undefined;
    }
  }

  private log(level: LogLevel, message: string, context?: string, data?: any): void {
    if (!this.shouldLog(level)) return;

    const entry = this.createLogEntry(level, message, context, data);
    const formattedMessage = this.formatLog(entry);

    // Solo mostrar en consola en desarrollo
    if (this.isDevelopment) {
      switch (level) {
        case LogLevel.ERROR:
          console.error(formattedMessage, data);
          break;
        case LogLevel.WARN:
          console.warn(formattedMessage, data);
          break;
        case LogLevel.INFO:
          console.info(formattedMessage, data);
          break;
        case LogLevel.DEBUG:
        case LogLevel.TRACE:
          console.log(formattedMessage, data);
          break;
      }
    }

    // En producción, enviar a servicio de logging
    if (!this.isDevelopment && level <= LogLevel.WARN) {
      this.sendToLoggingService(entry);
    }
  }

  private async sendToLoggingService(entry: LogEntry): Promise<void> {
    try {
      // Implementar envío a servicio de logging en producción
      await fetch('/api/logs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(entry)
      });
    } catch (error) {
      // Fallback silencioso en producción
    }
  }

  // Métodos públicos
  public error(message: string, context?: string, data?: any): void {
    this.log(LogLevel.ERROR, message, context, data);
  }

  public warn(message: string, context?: string, data?: any): void {
    this.log(LogLevel.WARN, message, context, data);
  }

  public info(message: string, context?: string, data?: any): void {
    this.log(LogLevel.INFO, message, context, data);
  }

  public debug(message: string, context?: string, data?: any): void {
    this.log(LogLevel.DEBUG, message, context, data);
  }

  public trace(message: string, context?: string, data?: any): void {
    this.log(LogLevel.TRACE, message, context, data);
  }

  // Métodos de conveniencia para migración
  public auth(message: string, data?: any): void {
    this.info(message, 'AUTH', data);
  }

  public navigation(message: string, data?: any): void {
    this.debug(message, 'NAVIGATION', data);
  }

  public api(message: string, data?: any): void {
    this.debug(message, 'API', data);
  }

  public security(message: string, data?: any): void {
    this.warn(message, 'SECURITY', data);
  }

  public performance(message: string, data?: any): void {
    this.debug(message, 'PERFORMANCE', data);
  }
}

// Instancia singleton
export const logger = Logger.getInstance();

// Configurar nivel según entorno (protegido para entornos sin window)
if (typeof window !== 'undefined' && window.location && window.location.hostname === 'localhost') {
  logger.setLogLevel(LogLevel.DEBUG);
} else {
  logger.setLogLevel(LogLevel.WARN);
}
