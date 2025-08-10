import React, { createContext, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { FLAGS as STATIC_FLAGS } from '../config/flags';

/**
 * Dynamic feature flags provider.
 * - Starts with static build-time flags (backwards compatibility)
 * - Hydrates from /api/flags (flattening nested YAML structure)
 * - Safe fallback on network / parse errors
 * - Simple in-memory + sessionStorage cache to avoid refetch storm
 */

export type FlagMap = Record<string, boolean>;

interface FlagsContextValue {
  flags: FlagMap;
  isLoaded: boolean; // true when remote attempt finished (success OR fail)
  get: (key: string) => boolean;
  refresh: () => Promise<void>;
  raw: any; // original nested object (for debugging / future targeting UI)
}

const FlagsContext = createContext<FlagsContextValue | undefined>(undefined);

// Session storage key (scoped to app)
const STORAGE_KEY = 'impulse.flags.cache.v1';
// Avoid overlapping concurrent fetches
let inFlight: Promise<FlagMap> | null = null;

function flatten(obj: any, prefix = '', out: Record<string, boolean> = {}): Record<string, boolean> {
  if (obj && typeof obj === 'object') {
    for (const [k, v] of Object.entries(obj)) {
      const nextKey = prefix ? `${prefix}.${k}` : k;
      if (v !== null && typeof v === 'object' && !Array.isArray(v)) {
        flatten(v, nextKey, out);
      } else {
        // Coerce to boolean (only true truthy => true, everything else false)
        out[nextKey] = !!v;
      }
    }
  }
  return out;
}

async function fetchFlags(): Promise<{ flat: FlagMap; raw: any }> {
  const res = await fetch('/api/flags', { credentials: 'include' }) // relative; adjust if api gateway prefix later
    .catch((e) => { throw new Error('network:' + e.message); });
  if (!res.ok) throw new Error('http:' + res.status);
  const data = await res.json();
  return { flat: flatten(data), raw: data };
}

export const FlagsProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [flags, setFlags] = useState<FlagMap>(() => ({ ...STATIC_FLAGS }));
  const [raw, setRaw] = useState<any>(null);
  const [loaded, setLoaded] = useState(false);
  const mounted = useRef(true);

  // Load from sessionStorage if present (warm start before network)
  useEffect(() => {
    try {
      const cached = sessionStorage.getItem(STORAGE_KEY);
      if (cached) {
        const parsed = JSON.parse(cached);
        if (parsed && parsed.flat) {
          setFlags((prev) => ({ ...prev, ...parsed.flat }));
          setRaw(parsed.raw ?? null);
        }
      }
    } catch { /* ignore */ }
  }, []);

  const doFetch = async () => {
    if (!inFlight) {
      inFlight = fetchFlags().then(r => r.flat).catch(() => ({} as FlagMap)).finally(() => { inFlight = null; });
    }
    const { flat, raw } = await fetchFlags().catch(() => ({ flat: {} as FlagMap, raw: null }));
    if (!mounted.current) return;
    if (Object.keys(flat).length) {
      setFlags(prev => ({ ...prev, ...flat }));
      setRaw(raw);
      try { sessionStorage.setItem(STORAGE_KEY, JSON.stringify({ flat, raw, ts: Date.now() })); } catch { /* ignore */ }
    }
    setLoaded(true);
  };

  // First fetch after mount (microtask to allow initial paint)
  useEffect(() => {
    Promise.resolve().then(doFetch);
    return () => { mounted.current = false; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const get = (key: string): boolean => {
    return !!flags[key];
  };

  const refresh = async () => {
    setLoaded(false);
    await doFetch();
  };

  const value: FlagsContextValue = useMemo(() => ({ flags, isLoaded: loaded, get, refresh, raw }), [flags, loaded, raw]);

  return <FlagsContext.Provider value={value}>{children}</FlagsContext.Provider>;
};

export function useFlags() {
  const ctx = useContext(FlagsContext);
  if (!ctx) throw new Error('useFlags must be used within <FlagsProvider>');
  return ctx;
}

export function useFlag(key: string): boolean {
  return useFlags().get(key);
}

// Helper for conditional rendering inline
export const Flag: React.FC<{ name: string; children: React.ReactNode; fallback?: React.ReactNode }> = ({ name, children, fallback = null }) => {
  const enabled = useFlag(name);
  return enabled ? <>{children}</> : <>{fallback}</>;
};
