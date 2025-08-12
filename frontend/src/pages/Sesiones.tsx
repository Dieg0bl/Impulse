import React, { useState } from 'react';
import { useSessions } from '../hooks/useSessions';
import Button from '../components/Button';
import Skeleton from '../components/Skeleton';

const Sesiones: React.FC = () => {
  const { sessions, loading, error, revoke } = useSessions();
  const [revocandoId, setRevocandoId] = useState<string | null>(null);

  if (loading) {
    return <div style={{ maxWidth: 600, margin: '2rem auto' }}><Skeleton height={40} /><Skeleton height={40} /></div>;
  }
  if (error) {
    return <div className="error-message" role="alert">{error}</div>;
  }
  if (!sessions.length) {
    return <div className="empty-state" role="status">No hay sesiones activas.</div>;
  }

  return (
    <div className="sesiones-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Sesiones activas</h1>
      <ul className="divide-y">
  {sessions.map(s => (
          <li key={s.id} className="flex items-center justify-between py-3">
            <div>
              <div className="font-medium">{s.device} ({s.ip})</div>
              <div className="text-xs text-gray-500">{s.ubicacion} - {new Date(s.ultimoAcceso).toLocaleString()}</div>
            </div>
            <Button
              variant="danger"
              size="small"
              disabled={revocandoId === s.id}
              onClick={async () => {
                setRevocandoId(s.id);
                await revoke(s.id);
                setRevocandoId(null);
              }}
            >
              {revocandoId === s.id ? 'Revocando...' : 'Revocar'}
            </Button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Sesiones;
