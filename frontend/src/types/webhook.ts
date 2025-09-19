export type WebhookProvider = 'stripe' | 'unknown';

export interface WebhookEventSummary {
  id: string;
  eventId: string;
  provider: WebhookProvider;
  eventType: string;
  receivedAt: string; // ISO timestamp
  signatureVerified?: boolean;
  processedAt?: string | null;
  result?: 'ok' | 'error' | 'pending';
}

export interface WebhookEventDetail extends WebhookEventSummary {
  payload: Record<string, any> | string;
  payloadHash?: string;
  attempts?: number;
  idempotencyKeys?: string[];
  traces?: Array<{ ts: string; message: string }>;
}

export interface WebhookListRequest {
  page?: number;
  size?: number;
  status?: string;
  q?: string;
  from?: string; // ISO date
  to?: string; // ISO date
}

export default {};
