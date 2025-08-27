// Servicio para evidencias: obtener, subir y reportar
export const getEvidencias = async (retoId: string): Promise<any[]> => {
  const res = await fetch(`/api/retos/${retoId}/evidencias`);
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error obteniendo evidencias');
  }
  return res.json();
};

export const uploadEvidencia = async (retoId: string, file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  const res = await fetch(`/api/retos/${retoId}/evidencias`, {
    method: 'POST',
    body: formData
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error subiendo evidencia');
  }
  return res.text();
};

export const reportEvidencia = async (evidenciaId: string, motivo: string): Promise<string> => {
  const res = await fetch(`/api/evidencias/${evidenciaId}/report`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ motivo })
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error reportando evidencia');
  }
  return res.text();
};
