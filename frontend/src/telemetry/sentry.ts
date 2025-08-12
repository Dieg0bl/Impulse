// Sentry y telemetría básica para Fase 0
import * as Sentry from '@sentry/browser';

Sentry.init({
  dsn: 'https://examplePublicKey@o0.ingest.sentry.io/0', // Reemplaza con tu DSN real
  tracesSampleRate: 1.0,
  environment: process.env.NODE_ENV,
});

export function captureException(error: any, context?: any) {
  Sentry.captureException(error, { extra: context });
}

export function captureMessage(message: string, context?: any) {
  Sentry.captureMessage(message, { extra: context });
}
