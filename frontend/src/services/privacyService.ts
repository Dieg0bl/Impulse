// Servicio para consents y privacidad
export const getConsents = async () => {
  const res = await fetch('/api/privacy/consents');
  if (!res.ok) throw new Error('Error obteniendo consents');
  return res.json();
};

export const updateConsent = async (consentId: string, value: boolean) => {
  const res = await fetch(`/api/privacy/consents/${consentId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ value })
  });
  if (!res.ok) throw new Error('Error actualizando consent');
  return res.json();
};

export const getPrivacyRequests = async () => {
  const res = await fetch('/api/privacy/requests');
  if (!res.ok) throw new Error('Error obteniendo solicitudes');
  return res.json();
};

export const createPrivacyRequest = async (type: string) => {
  const res = await fetch('/api/privacy/requests', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ type })
  });
  if (!res.ok) throw new Error('Error creando solicitud');
  return res.json();
};
