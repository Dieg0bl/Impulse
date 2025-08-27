
import React, { useState, useEffect } from 'react';
import Button from '../components/Button';
import { useAppContext } from '../contexts/AppContext.tsx';

const mockReportes = [
  { id: '1', tipo: 'EVIDENCIA', estado: 'PENDIENTE', fecha: '2025-08-10', detalle: 'Evidencia sospechosa', usuario: 'demo' },
  { id: '2', tipo: 'USUARIO', estado: 'REVISADO', fecha: '2025-08-11', detalle: 'Comportamiento inapropiado', usuario: 'user2' },
];

const Moderacion: React.FC = () => {
  const { state, navigate } = useAppContext();
  const { currentUser: user } = state;
  const [reportes, setReportes] = useState(mockReportes);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    if (user.rol !== 'ADMIN') {
      navigate('/dashboard');
    }
  }, [user, navigate]);

  const marcarRevisado = (id: string) => {
    setReportes(reportes.map(r => r.id === id ? { ...r, estado: 'REVISADO' } : r));
  };

  return (
    <div className="moderacion-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Panel de Moderación</h1>
      <ul className="divide-y">
        {reportes.map(r => (
          <li key={r.id} className="py-3 flex justify-between items-center">
            <div>
              <div className="font-medium">{r.tipo} — {r.detalle}</div>
              <div className="text-xs text-gray-500">{r.usuario} — {new Date(r.fecha).toLocaleString()}</div>
            </div>
            <span className={`px-2 py-1 rounded text-xs font-medium ${r.estado === 'REVISADO' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>{r.estado}</span>
            {r.estado !== 'REVISADO' && (
              <Button size="small" onClick={() => marcarRevisado(r.id)}>Marcar revisado</Button>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Moderacion;
