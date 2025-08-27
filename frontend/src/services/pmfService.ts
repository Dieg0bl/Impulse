// Servicio para encuestas y señales PMF
export const submitSurvey = async (type: string, answers: Record<string, any>): Promise<void> => {
  const res = await fetch(`/api/pmf/survey/${encodeURIComponent(type)}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(answers)
  });
  if (!res.ok) throw new Error(await res.text() || 'Error enviando encuesta');
};

export const getSignals = async (): Promise<any> => {
  const res = await fetch('/api/pmf/signals');
  if (!res.ok) throw new Error(await res.text() || 'Error obteniendo señales');
  return res.json();
};
