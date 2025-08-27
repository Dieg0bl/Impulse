// Servicio para feedback (NPS y CSAT)
export const submitNps = async (userId: number, score: number, reason?: string): Promise<any> => {
  const params = new URLSearchParams({ score: score.toString() });
  if (reason) params.append('reason', reason);
  const res = await fetch(`/api/feedback/nps/${userId}?${params.toString()}`, { method: 'POST' });
  if (!res.ok) throw new Error(await res.text() || 'Error enviando NPS');
  return res.json();
};

export const submitCsat = async (userId: number, score: number): Promise<any> => {
  const params = new URLSearchParams({ score: score.toString() });
  const res = await fetch(`/api/feedback/csat/${userId}?${params.toString()}`, { method: 'POST' });
  if (!res.ok) throw new Error(await res.text() || 'Error enviando CSAT');
  return res.json();
};

export const getNpsAggregate = async (): Promise<any> => {
  const res = await fetch('/api/feedback/nps/aggregate');
  if (!res.ok) throw new Error(await res.text() || 'Error obteniendo NPS agregado');
  return res.json();
};

export const getCsatAggregate = async (): Promise<any> => {
  const res = await fetch('/api/feedback/csat/aggregate');
  if (!res.ok) throw new Error(await res.text() || 'Error obteniendo CSAT agregado');
  return res.json();
};
