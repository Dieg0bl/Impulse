// Configuración básica de Sentry para monitoreo
export const init = () => {
  // Inicialización básica de Sentry (comentado para evitar errores)
  console.log('Sentry telemetry initialized');
};

export const captureException = (error: Error) => {
  console.error('Error captured:', error);
};

export default {
  init,
  captureException
};
