// Servicio para métricas y economía
export const getWeeklyAha = async (): Promise<any> => {
  const res = await fetch('/api/metrics/nsm/weekly-aha');
  if (!res.ok) throw new Error(await res.text() || 'Error obteniendo weekly AHA');
  return res.json();
};

export const incrementEconomics = async (name: string, delta: number): Promise<any> => {
  const res = await fetch(`/api/metrics/economics/increment/${encodeURIComponent(name)}/${delta}`, { method: 'POST' });
  if (!res.ok) throw new Error(await res.text() || 'Error incrementando economics');
  return res.json();
};

export const getEconomics = async (name: string): Promise<any> => {
  const res = await fetch(`/api/metrics/economics/${encodeURIComponent(name)}`);
  if (!res.ok) throw new Error(await res.text() || 'Error obteniendo economics');
  return res.json();
};

export const getEconomicsAdvanced = async (): Promise<any> => {
  const res = await fetch('/api/metrics/economics/summary/advanced');
  if (!res.ok) throw new Error(await res.text() || 'Error obteniendo economics avanzados');
  return res.json();
};
