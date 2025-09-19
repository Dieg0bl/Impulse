import React, { useState, useEffect, useCallback } from 'react';
import AppButton from '../ui/AppButton';

interface HealthStatus {
  status: 'OK' | 'WARN' | 'ERROR';
  p95Latency: number;
  error5xx: number;
  versions: { fe: string; be: string };
  dependencies: { name: string; status: 'OK' | 'WARN' | 'ERROR' }[];
  newVersionAvailable: boolean;
}

const HealthBanner: React.FC = () => {
  const [health, setHealth] = useState<HealthStatus | null>(null);
  const [showDialog, setShowDialog] = useState(false);
  const [loading, setLoading] = useState(true);

  const getColorClass = (status: string) => {
    if (status === 'OK') return 'text-green-600';
    if (status === 'WARN') return 'text-yellow-600';
    return 'text-red-600';
  };

  // Mock health data
  const mockHealth: HealthStatus = {
    status: 'WARN',
    p95Latency: 250,
    error5xx: 5,
    versions: { fe: '1.0.0', be: '1.0.0' },
    dependencies: [
      { name: 'Database', status: 'OK' },
      { name: 'Cache', status: 'WARN' },
      { name: 'External API', status: 'OK' },
    ],
    newVersionAvailable: true,
  };

  const fetchHealth = useCallback(async () => {
    setLoading(true);
    try {
      // Mock fetch
      setTimeout(() => {
        setHealth(mockHealth);
        setLoading(false);
      }, 1000);
    } catch {
      setHealth({ ...mockHealth, status: 'ERROR' });
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchHealth();
  }, [fetchHealth]);

  const postEvent = useCallback(async (type: string) => {
    try {
      await fetch('/api/v1/events', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ type, details: {}, timestamp: new Date().toISOString() }),
      });
    } catch {}
  }, []);

  const handleReload = () => {
    postEvent('app_updated_confirmed');
    window.location.reload();
  };

  if (loading || !health || health.status === 'OK') {
    return null;
  }

  const statusColor = {
    WARN: 'yellow',
    ERROR: 'red',
  }[health.status];

  return (
    <>
      <div
        className={`bg-${statusColor}-50 border border-${statusColor}-200 text-${statusColor}-800 px-4 py-3 flex items-center gap-4`}
        role="alert"
      >
        <div className={`w-4 h-4 rounded-full bg-${statusColor}-500`}></div>
        <div className="flex-1">
          <strong>Estado del sistema:</strong> {health.status === 'WARN' ? 'Advertencia' : 'Error'}
          {health.newVersionAvailable && ' — Nueva versión disponible'}
        </div>
        <div className="flex gap-2">
          <AppButton onClick={() => setShowDialog(true)} variant="outlined" size="compact">
            Ver detalles
          </AppButton>
          {health.newVersionAvailable && (
            <AppButton onClick={handleReload} variant="contained" size="compact">
              Recargar
            </AppButton>
          )}
        </div>
      </div>

      {showDialog && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" role="dialog" aria-labelledby="health-dialog-title">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h2 id="health-dialog-title" className="text-lg font-semibold mb-4">Estado del sistema</h2>
            <div className="space-y-3 mb-6">
              <div>
                <strong>Latencia p95:</strong> {health.p95Latency} ms
              </div>
              <div>
                <strong>Errores 5xx recientes:</strong> {health.error5xx}
              </div>
              <div>
                <strong>Versiones:</strong> FE {health.versions.fe} | BE {health.versions.be}
              </div>
              <div>
                <strong>Dependencias:</strong>
                <ul className="ml-4 mt-1 space-y-1">
                  {health.dependencies.map(dep => (
                    <li key={dep.name} className={`text-sm ${getColorClass(dep.status)}`}>
                      {dep.name}: {dep.status}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
            <div className="flex gap-2 justify-end">
              <AppButton onClick={() => setShowDialog(false)} variant="outlined" size="compact">
                Cerrar
              </AppButton>
              {health.newVersionAvailable && (
                <AppButton onClick={handleReload} variant="contained" size="compact">
                  Recargar
                </AppButton>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default HealthBanner;

