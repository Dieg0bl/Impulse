import React, { useState } from 'react';
import Button from '../components/Button';

interface Ticket {
  id: string;
  asunto: string;
  estado: 'ABIERTO' | 'EN_PROGRESO' | 'CERRADO';
  fecha: string;
  comentarios: { usuario: string; mensaje: string; fecha: string }[];
  adjuntos?: string[];
}

const mockTickets: Ticket[] = [
  {
    id: '1',
    asunto: 'No puedo subir evidencia',
    estado: 'ABIERTO',
    fecha: '2025-08-10',
    comentarios: [
      { usuario: 'soporte', mensaje: '¿Puedes adjuntar un pantallazo?', fecha: '2025-08-10' },
    ],
    adjuntos: [],
  },
];

const Soporte: React.FC = () => {
  const [tickets, setTickets] = useState<Ticket[]>(mockTickets);
  const [nuevo, setNuevo] = useState(false);
  const [asunto, setAsunto] = useState('');
  const [mensaje, setMensaje] = useState('');

  const crearTicket = () => {
    setTickets([
      ...tickets,
      {
        id: String(Date.now()),
        asunto,
        estado: 'ABIERTO',
        fecha: new Date().toISOString(),
        comentarios: [{ usuario: 'yo', mensaje, fecha: new Date().toISOString() }],
        adjuntos: [],
      },
    ]);
    setNuevo(false);
    setAsunto('');
    setMensaje('');
  };

  return (
    <div className="soporte-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Centro de ayuda y soporte</h1>
      <Button onClick={() => setNuevo(true)}>Nuevo ticket</Button>
      {nuevo && (
        <div className="my-4 p-4 border rounded">
          <input
            className="block mb-2 border px-2 py-1"
            placeholder="Asunto"
            value={asunto}
            onChange={e => setAsunto(e.target.value)}
          />
          <textarea
            className="block mb-2 border px-2 py-1"
            placeholder="Describe tu problema"
            value={mensaje}
            onChange={e => setMensaje(e.target.value)}
          />
          <Button onClick={crearTicket}>Enviar</Button>
        </div>
      )}
      <ul className="divide-y mt-6">
        {tickets.map(t => (
          <li key={t.id} className="py-4">
            <div className="font-medium">{t.asunto} <span className="text-xs">({t.estado})</span></div>
            <div className="text-xs text-gray-500">{new Date(t.fecha).toLocaleString()}</div>
            <div className="mt-2">
              {t.comentarios.map((c, i) => (
                <div key={i} className="text-sm mb-1"><b>{c.usuario}:</b> {c.mensaje} <span className="text-xs text-gray-400">{new Date(c.fecha).toLocaleString()}</span></div>
              ))}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Soporte;
