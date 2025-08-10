import React, { useState } from 'react';
import { createInvite } from '../services/inviteService';

interface ViralShareProps {
  referrerId: number;
  channel?: string;
}

// Lightweight share component leveraging Web Share API when available
export const ViralShare: React.FC<ViralShareProps> = ({ referrerId, channel = 'link' }) => {
  const [inviteUrl, setInviteUrl] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  async function generate() {
    setLoading(true); setError(null); setCopied(false);
    try {
      const res = await createInvite(referrerId, channel);
      setInviteUrl(res.url);
      if (navigator.share) {
        try { await navigator.share({ title: 'Únete a Impulse', text: 'Súmate a mis retos en Impulse!', url: res.url }); } catch {}
      }
    } catch (e:any) {
      setError(e.message || 'Error al crear invitación');
    } finally { setLoading(false); }
  }

  async function copy() {
    if (!inviteUrl) return;
    try { await navigator.clipboard.writeText(inviteUrl); setCopied(true); setTimeout(()=>setCopied(false),1500); } catch {}
  }

  return (
    <div style={{border:'1px solid #1e293b', padding:'1rem', borderRadius:8}}>
      <h4>Invita a amigos</h4>
      {!inviteUrl && <button onClick={generate} disabled={loading}>{loading ? 'Generando...' : 'Crear enlace'}</button>}
      {error && <div style={{color:'tomato', marginTop:8}}>{error}</div>}
      {inviteUrl && (
        <div style={{marginTop:8}}>
          <input style={{width:'100%'}} value={inviteUrl} readOnly />
          <div style={{display:'flex', gap:8, marginTop:8}}>
            <button onClick={copy}>{copied ? 'Copiado!' : 'Copiar'}</button>
            <button onClick={generate} disabled={loading}>Nuevo</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ViralShare;
