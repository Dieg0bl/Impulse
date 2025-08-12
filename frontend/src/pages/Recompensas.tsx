import React, { useState } from 'react';

const mockRecompensas = [
  { id: '1', nombre: 'Badge Salud', tipo: 'badge', descripcion: 'Por completar 5 retos de salud', puntos: 50 },
  { id: '2', nombre: 'Medalla Oro', tipo: 'medalla', descripcion: 'Por 100 puntos totales', puntos: 100 },
];

const Recompensas: React.FC = () => {
  const [recompensas] = useState(mockRecompensas);

  return (
    <div className="recompensas-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Recompensas y Logros</h1>
      <ul className="divide-y">
        {recompensas.map(r => (
          <li key={r.id} className="py-4 flex items-center gap-4">
            <div className="font-medium">{r.nombre}</div>
            <div className="text-xs text-gray-500">{r.descripcion}</div>
            <span className="px-2 py-1 rounded text-xs font-medium bg-yellow-100 text-yellow-800">{r.tipo}</span>
            <span className="font-bold">+{r.puntos} pts</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Recompensas;
