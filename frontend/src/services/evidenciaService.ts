// Servicio para evidencias: obtener, subir y reportar
export const getEvidencias = async (retoId: string) => {
  const res = await fetch(`/api/retos/${retoId}/evidencias`);
  if (!res.ok) throw new Error('Error obteniendo evidencias');
  return res.json();
};

export const uploadEvidencia = async (retoId: string, file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  const res = await fetch(`/api/retos/${retoId}/evidencias`, {
    method: 'POST',
    body: formData
  });
  if (!res.ok) throw new Error('Error subiendo evidencia');
  return res.json();
};

export const reportEvidencia = async (evidenciaId: string, motivo: string) => {
  const res = await fetch(`/api/evidencias/${evidenciaId}/report`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ motivo })
  });
  if (!res.ok) throw new Error('Error reportando evidencia');
  return res.json();
};
