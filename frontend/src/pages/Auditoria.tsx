import React from 'react';

const mockAuditoria = [
  { id: 1, tipo: 'LOGIN', usuario: 'demo', fecha: '2025-08-10', detalle: 'Inicio de sesión exitoso' },
  { id: 2, tipo: 'RETO', usuario: 'demo', fecha: '2025-08-11', detalle: 'Creó un reto' },
  { id: 3, tipo: 'EVIDENCIA', usuario: 'demo', fecha: '2025-08-12', detalle: 'Subió evidencia' },
];

const Auditoria: React.FC = () => {
  return (
    <div className="auditoria-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Feed de Auditoría</h1>
      <ul className="divide-y">
        {mockAuditoria.map(a => (
          <li key={a.id} className="py-3">
            <div className="font-medium">{a.tipo} — {a.detalle}</div>
            <div className="text-xs text-gray-500">{a.usuario} — {new Date(a.fecha).toLocaleString()}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Auditoria;
