// Servicio para referidos
export const generateReferral = async (userId: number): Promise<any> => {
  const res = await fetch(`/api/referrals/generate/${userId}`, { method: 'POST' });
  if (!res.ok) throw new Error(await res.text() || 'Error generando referido');
  return res.json();
};

export const applyReferral = async (code: string, userId: number): Promise<any> => {
  const res = await fetch(`/api/referrals/apply/${encodeURIComponent(code)}/${userId}`, { method: 'POST' });
  if (!res.ok) throw new Error(await res.text() || 'Error aplicando referido');
  return res.json();
};

export const getReferralStats = async (userId: number): Promise<any> => {
  const res = await fetch(`/api/referrals/stats/${userId}`);
  if (!res.ok) throw new Error(await res.text() || 'Error obteniendo stats de referidos');
  return res.json();
};
