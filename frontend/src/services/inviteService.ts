export interface InviteResponse { code: string; url: string; }

export async function createInvite(referrerId: number, channel: string): Promise<InviteResponse> {
  const resp = await fetch('/api/invites', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ referrer_id: referrerId, channel })
  });
  if (!resp.ok) throw new Error('Error creando invitación');
  return resp.json();
}

export async function checkInvite(code: string): Promise<boolean> {
  const resp = await fetch(`/api/invites/join/${code}`);
  if (!resp.ok) return false;
  const data = await resp.json();
  return !!data.ok;
}

export async function convertInvite(code: string): Promise<any> {
  const resp = await fetch(`/api/invites/convert/${code}` , { method: 'POST' });
  if (!resp.ok) throw new Error('Error convirtiendo invitación');
  return resp.json();
}

export interface InviteStats { referrer_id: number; total: number; accepted: number; conversion_rate: number; }
export async function getInviteStats(referrerId: number): Promise<InviteStats> {
  const resp = await fetch(`/api/invites/stats/${referrerId}`);
  if (!resp.ok) throw new Error('Error obteniendo estadísticas');
  return resp.json();
}
