import React, { useState } from 'react';
import Button from '../components/Button';

const mockPagos = [
  { id: '1', metodo: 'Stripe', estado: 'COMPLETADO', fecha: '2025-08-10', monto: 9.99 },
  { id: '2', metodo: 'PayPal', estado: 'PENDIENTE', fecha: '2025-08-12', monto: 19.99 },
];

const Pagos: React.FC = () => {
  const [pagos, setPagos] = useState(mockPagos);
  const [procesando, setProcesando] = useState(false);

  const iniciarPago = async (metodo: string) => {
    setProcesando(true);
    setTimeout(() => {
      setPagos([...pagos, { id: String(Date.now()), metodo, estado: 'COMPLETADO', fecha: new Date().toISOString(), monto: 14.99 }]);
      setProcesando(false);
    }, 1500);
  };

  return (
    <div className="pagos-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Pagos y Suscripciones</h1>
      <div className="flex gap-4 mb-6">
        <Button onClick={() => iniciarPago('Stripe')} disabled={procesando}>Stripe</Button>
        <Button onClick={() => iniciarPago('PayPal')} disabled={procesando}>PayPal</Button>
        <Button onClick={() => iniciarPago('ApplePay')} disabled={procesando}>Apple Pay</Button>
      </div>
      <ul className="divide-y">
        {pagos.map(p => (
          <li key={p.id} className="py-3 flex justify-between items-center">
            <div>
              <div className="font-medium">{p.metodo}</div>
              <div className="text-xs text-gray-500">{new Date(p.fecha).toLocaleString()}</div>
            </div>
            <div className="font-bold">{p.monto.toLocaleString('es-ES', { style: 'currency', currency: 'EUR' })}</div>
            <span className={`px-2 py-1 rounded text-xs font-medium ${p.estado === 'COMPLETADO' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>{p.estado}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Pagos;
