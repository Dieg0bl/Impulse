// Sentry y telemetría básica para Fase 0
import * as Sentry from '@sentry/browser';

// Use environment variable for DSN to avoid leaking secrets in source.
const SENTRY_DSN = process.env.REACT_APP_SENTRY_DSN || process.env.VITE_SENTRY_DSN || '';

if (SENTRY_DSN) {
  Sentry.init({
    dsn: SENTRY_DSN,
    tracesSampleRate: 1.0,
    environment: process.env.NODE_ENV,
  });
} else {
  // noop: keep API but avoid initializing Sentry when DSN not provided (useful in local/dev)
  // Calls to captureException/captureMessage will be no-ops if Sentry not initialized.
  // We intentionally do not throw to avoid breaking dev workflows.
}

export function captureException(error: unknown, context?: Record<string, unknown>) {
  // safe-call: Sentry.captureException will be a noop if not initialized
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (Sentry as any).captureException(error, { extra: context });
}

export function captureMessage(message: string, context?: Record<string, unknown>) {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (Sentry as any).captureMessage(message, { extra: context });
}
