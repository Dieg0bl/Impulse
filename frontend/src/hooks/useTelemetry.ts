import { useCallback } from 'react'
import { v4 as uuidv4 } from 'uuid'

// Lightweight telemetry hook. In prod replace with actual telemetry SDK wiring.
export const useTelemetry = (options?: { proxyToConsole?: boolean }) => {
  // Generate a correlation id per hook instance if none provided upstream
  const correlationId = uuidv4()

  const trackEvent = useCallback((name: string, props?: Record<string, any>) => {
    const payload = { name, props: props || {}, correlationId, ts: new Date().toISOString() }
    if (options?.proxyToConsole || process.env.NODE_ENV !== 'production') {
      // lightweight dev proxy
      // eslint-disable-next-line no-console
      console.debug('[telemetry]', payload)
    }
  // Intentionally noop for now; replace with real telemetry sink in production wiring.
  }, [correlationId, options])

  return { trackEvent, correlationId }
}

export default useTelemetry
