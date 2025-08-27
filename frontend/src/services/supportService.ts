// Servicio para soporte (tickets)
export interface SupportTicket {
  id: number;
  userId: number;
  subject: string;
  body: string;
  status: string;
}

export const createSupportTicket = async (userId: number, subject: string, body: string): Promise<SupportTicket> => {
  const res = await fetch(`/api/support/ticket/${userId}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`, {
    method: 'POST',
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error creando ticket');
  }
  return res.json();
};

export const listSupportTickets = async (): Promise<SupportTicket[]> => {
  const res = await fetch('/api/support/tickets');
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error obteniendo tickets');
  }
  return res.json();
};

export const closeSupportTicket = async (id: number): Promise<SupportTicket> => {
  const res = await fetch(`/api/support/ticket/${id}/close`, {
    method: 'POST',
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || 'Error cerrando ticket');
  }
  return res.json();
};
