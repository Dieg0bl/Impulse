// Servicio para consents y privacidad
export interface ConsentDTO {
  id: string;
  value: boolean;
}

export const getConsents = async (): Promise<ConsentDTO[]> => {
  const res = await fetch('/api/privacy/consents');
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error obteniendo consents');
  }
  return res.json();
};

export const updateConsent = async (consentId: string, value: boolean): Promise<ConsentDTO> => {
  const res = await fetch(`/api/privacy/consents/${consentId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ value })
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error actualizando consent');
  }
  return res.json();
};

export const getPrivacyRequests = async (): Promise<any[]> => {
  const res = await fetch('/api/privacy/requests');
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error obteniendo solicitudes');
  }
  return res.json();
};

export const createPrivacyRequest = async (type: string): Promise<any> => {
  const res = await fetch('/api/privacy/requests', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ type })
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error creando solicitud');
  }
  return res.json();
};
