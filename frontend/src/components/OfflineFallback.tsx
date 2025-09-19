import React from 'react';

const OfflineFallback: React.FC = () => {
  return (
    <div className="offline-fallback container-app p-8 text-center">
      <h2 className="text-lg font-semibold">Sin conexión</h2>
      <p className="mt-2 text-sm text-muted">Parece que estás desconectado. Revisa tu conexión o vuelve a intentarlo más tarde.</p>
    </div>
  );
};

export default OfflineFallback;
