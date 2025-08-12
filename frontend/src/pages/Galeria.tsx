import React, { useState } from 'react';

const mockMedia = [
  { id: '1', tipo: 'IMAGEN', url: '/media/ejemplo1.jpg', descripcion: 'Evidencia 1' },
  { id: '2', tipo: 'VIDEO', url: '/media/ejemplo2.mp4', descripcion: 'Evidencia 2' },
];

const Galeria: React.FC = () => {
  const [media] = useState(mockMedia);
  const [selected, setSelected] = useState<string | null>(null);

  return (
    <div className="galeria-page container mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold mb-4">Galería de Evidencias</h1>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {media.map(m => (
          <div key={m.id} className="cursor-pointer" onClick={() => setSelected(m.id)}>
            {m.tipo === 'IMAGEN' ? (
              <img src={m.url} alt={m.descripcion} className="rounded shadow h-32 object-cover w-full" />
            ) : (
              <video src={m.url} className="rounded shadow h-32 w-full object-cover" />
            )}
            <div className="text-xs mt-1 text-center">{m.descripcion}</div>
          </div>
        ))}
      </div>
      {selected && (
        <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50" onClick={() => setSelected(null)}>
          <div className="bg-white p-4 rounded shadow-lg max-w-lg w-full relative">
            <button className="absolute top-2 right-2 text-xl" onClick={() => setSelected(null)}>&times;</button>
            {media.find(m => m.id === selected)?.tipo === 'IMAGEN' ? (
              <img src={media.find(m => m.id === selected)?.url} alt="" className="w-full" />
            ) : (
              <video src={media.find(m => m.id === selected)?.url} controls className="w-full" />
            )}
            <div className="mt-2 text-center">{media.find(m => m.id === selected)?.descripcion}</div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Galeria;
