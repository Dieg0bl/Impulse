// Servicio para emails
export const sendEmail = async (template: string, userId: number, params?: Record<string, any>): Promise<any> => {
  const res = await fetch(`/api/email/send/${encodeURIComponent(template)}/${userId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: params ? JSON.stringify(params) : undefined
  });
  if (!res.ok) throw new Error(await res.text() || 'Error enviando email');
  return res.json();
};
