export function log(level: 'info'|'warn'|'error', msg: string, meta?: Record<string, any>) {
  const entry = { level, msg, ...meta, timestamp: new Date().toISOString() };
  console.log(JSON.stringify(entry));
}
