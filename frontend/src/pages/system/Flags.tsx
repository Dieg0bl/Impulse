import React, { useState } from 'react';
import AppButton from '../../ui/AppButton';
import { Badge } from '../../components/ui';

interface Flag {
  name: string;
  description: string;
  owner: string;
  retireBy: string;
  status: 'ON' | 'OFF';
  impact: string;
}

const Flags: React.FC = () => {
  const [filterStatus, setFilterStatus] = useState<'ALL' | 'ON' | 'OFF'>('ALL');
  const [filterOwner, setFilterOwner] = useState('');
  const [filterExpired, setFilterExpired] = useState(false);

  // Mock data
  const mockFlags: Flag[] = [
    { name: 'BILLING_ON', description: 'Enable billing features', owner: 'billing-team', retireBy: '2025-12-01', status: 'ON', impact: 'Pricing, payments' },
    { name: 'CMP_ON', description: 'Enable cookie management', owner: 'privacy-team', retireBy: '2025-10-01', status: 'OFF', impact: 'Cookie banner' },
    { name: 'EXPIRED_FLAG', description: 'Old flag', owner: 'dev-team', retireBy: '2025-01-01', status: 'OFF', impact: 'Legacy' },
  ];

  const filteredFlags = mockFlags.filter(flag => {
    if (filterStatus !== 'ALL' && flag.status !== filterStatus) return false;
    if (filterOwner && !flag.owner.includes(filterOwner)) return false;
    if (filterExpired && new Date(flag.retireBy) > new Date()) return false;
    return true;
  });

  const groupedFlags = filteredFlags.reduce((acc, flag) => {
    if (!acc[flag.owner]) acc[flag.owner] = [];
    acc[flag.owner].push(flag);
    return acc;
  }, {} as Record<string, Flag[]>);

  const isExpired = (retireBy: string) => new Date(retireBy) < new Date();

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
  };

  return (
    <div className="container-app">
      <header className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">Feature Flags</h1>
      </header>

      {/* Filters */}
      <div className="filter-bar mb-4 flex gap-4">
        <select value={filterStatus} onChange={e => setFilterStatus(e.target.value as any)}>
          <option value="ALL">Todos</option>
          <option value="ON">ON</option>
          <option value="OFF">OFF</option>
        </select>
        <input
          type="text"
          placeholder="Filtrar por owner"
          value={filterOwner}
          onChange={e => setFilterOwner(e.target.value)}
          className="border rounded px-2 py-1"
        />
        <label>
          <input
            type="checkbox"
            checked={filterExpired}
            onChange={e => setFilterExpired(e.target.checked)}
          />
          Solo vencidos
        </label>
      </div>

      {/* Alert for expired */}
      {mockFlags.some(f => isExpired(f.retireBy)) && (
        <div className="bg-yellow-50 border border-yellow-200 rounded p-4 mb-4" role="alert">
          <p className="text-yellow-800">Hay flags vencidos. Revisa el CI para más detalles.</p>
        </div>
      )}

      {/* Grouped table */}
      {Object.entries(groupedFlags).map(([owner, flags]) => (
        <section key={owner} className="mb-6">
          <h2 className="text-lg font-semibold mb-2">{owner}</h2>
          <table className="w-full table-auto border-collapse border">
            <thead>
              <tr className="bg-gray-100">
                <th className="border p-2 text-left">Flag</th>
                <th className="border p-2 text-left">Descripción</th>
                <th className="border p-2 text-left">Retire By</th>
                <th className="border p-2 text-left">Estado</th>
                <th className="border p-2 text-left">Impacto</th>
                <th className="border p-2 text-left">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {flags.map(flag => (
                <tr key={flag.name} className={isExpired(flag.retireBy) ? 'bg-red-50' : ''}>
                  <td className="border p-2 font-mono text-sm">{flag.name}</td>
                  <td className="border p-2">{flag.description}</td>
                  <td className="border p-2">{flag.retireBy}</td>
                  <td className="border p-2">
                    <Badge variant={flag.status === 'ON' ? 'success' : 'error'}>{flag.status}</Badge>
                  </td>
                  <td className="border p-2">{flag.impact}</td>
                  <td className="border p-2">
                    <AppButton size="compact" onClick={() => copyToClipboard(flag.name)}>
                      Copiar
                    </AppButton>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      ))}
    </div>
  );
};

export default Flags;
