import React, { useState } from 'react';
import AppButton from '../../ui/AppButton';
import { Badge } from '../../components/ui';

interface AuditEntry {
  id: string;
  timestamp: string;
  action: string;
  resource: string;
  user: string;
  reason: string;
  correlationId: string;
  details: any;
}

const AuditLog: React.FC = () => {
  const [filterAction, setFilterAction] = useState('');
  const [filterResource, setFilterResource] = useState('');
  const [filterUser, setFilterUser] = useState('');
  const [filterCorrelationId, setFilterCorrelationId] = useState('');
  const [filterDateFrom, setFilterDateFrom] = useState('');
  const [filterDateTo, setFilterDateTo] = useState('');
  const [selectedEntry, setSelectedEntry] = useState<AuditEntry | null>(null);

  // Mock data
  const mockEntries: AuditEntry[] = [
    {
      id: '1',
      timestamp: '2025-09-19T10:00:00Z',
      action: 'CREATE',
      resource: 'Challenge:123',
      user: 'admin',
      reason: 'New challenge created',
      correlationId: 'corr-123',
      details: { title: 'Test Challenge' }
    },
    {
      id: '2',
      timestamp: '2025-09-19T09:30:00Z',
      action: 'DELETE',
      resource: 'Evidence:456',
      user: 'moderator',
      reason: 'Evidence removed for policy violation',
      correlationId: 'corr-456',
      details: { reason: 'Spam' }
    },
  ];

  const filteredEntries = mockEntries.filter(entry => {
    if (filterAction && !entry.action.includes(filterAction)) return false;
    if (filterResource && !entry.resource.includes(filterResource)) return false;
    if (filterUser && !entry.user.includes(filterUser)) return false;
    if (filterCorrelationId && entry.correlationId !== filterCorrelationId) return false;
    if (filterDateFrom && new Date(entry.timestamp) < new Date(filterDateFrom)) return false;
    if (filterDateTo && new Date(entry.timestamp) > new Date(filterDateTo)) return false;
    return true;
  });

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
  };

  const openResource = (resource: string) => {
    // Navigate to resource
    console.log('Open', resource);
  };

  return (
    <div className="container-app">
      <header className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">Audit Log</h1>
      </header>

      {/* Filters */}
      <div className="filter-bar mb-4 grid grid-cols-1 md:grid-cols-3 gap-4">
        <input
          type="text"
          placeholder="Acción"
          value={filterAction}
          onChange={e => setFilterAction(e.target.value)}
          className="border rounded px-2 py-1"
        />
        <input
          type="text"
          placeholder="Recurso"
          value={filterResource}
          onChange={e => setFilterResource(e.target.value)}
          className="border rounded px-2 py-1"
        />
        <input
          type="text"
          placeholder="Usuario"
          value={filterUser}
          onChange={e => setFilterUser(e.target.value)}
          className="border rounded px-2 py-1"
        />
        <input
          type="text"
          placeholder="Correlation ID"
          value={filterCorrelationId}
          onChange={e => setFilterCorrelationId(e.target.value)}
          className="border rounded px-2 py-1"
        />
        <input
          type="date"
          placeholder="Desde"
          value={filterDateFrom}
          onChange={e => setFilterDateFrom(e.target.value)}
          className="border rounded px-2 py-1"
        />
        <input
          type="date"
          placeholder="Hasta"
          value={filterDateTo}
          onChange={e => setFilterDateTo(e.target.value)}
          className="border rounded px-2 py-1"
        />
      </div>

      {/* Table */}
      <table className="w-full table-auto border-collapse border">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2 text-left">Fecha</th>
            <th className="border p-2 text-left">Acción</th>
            <th className="border p-2 text-left">Recurso</th>
            <th className="border p-2 text-left">Usuario</th>
            <th className="border p-2 text-left">Motivo</th>
            <th className="border p-2 text-left">Correlation ID</th>
            <th className="border p-2 text-left">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {filteredEntries.map(entry => (
            <tr key={entry.id} className="border-t">
              <td className="border p-2">{new Date(entry.timestamp).toLocaleString()}</td>
              <td className="border p-2">
                <Badge variant="info">{entry.action}</Badge>
              </td>
              <td className="border p-2">{entry.resource}</td>
              <td className="border p-2">{entry.user}</td>
              <td className="border p-2 truncate max-w-xs" title={entry.reason}>{entry.reason}</td>
              <td className="border p-2 font-mono text-sm">{entry.correlationId}</td>
              <td className="border p-2 flex gap-2">
                <AppButton size="compact" onClick={() => copyToClipboard(entry.correlationId)}>
                  Copiar ID
                </AppButton>
                <AppButton size="compact" onClick={() => openResource(entry.resource)}>
                  Abrir
                </AppButton>
                <AppButton size="compact" onClick={() => setSelectedEntry(entry)}>
                  Detalle
                </AppButton>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Drawer */}
      {selectedEntry && (
        <div className="fixed inset-y-0 right-0 w-96 bg-white shadow-lg border-l z-50">
          <div className="p-4">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold">Detalle de Auditoría</h2>
              <AppButton onClick={() => setSelectedEntry(null)}>Cerrar</AppButton>
            </div>
            <div className="space-y-4">
              <div>
                <strong>ID:</strong> {selectedEntry.id}
              </div>
              <div>
                <strong>Timestamp:</strong> {selectedEntry.timestamp}
              </div>
              <div>
                <strong>Acción:</strong> {selectedEntry.action}
              </div>
              <div>
                <strong>Recurso:</strong> {selectedEntry.resource}
              </div>
              <div>
                <strong>Usuario:</strong> {selectedEntry.user}
              </div>
              <div>
                <strong>Motivo:</strong> {selectedEntry.reason}
              </div>
              <div>
                <strong>Correlation ID:</strong> {selectedEntry.correlationId}
              </div>
              <div>
                <strong>Detalles:</strong>
                <pre className="bg-gray-100 p-2 rounded text-sm mt-2 overflow-auto">
                  {JSON.stringify(selectedEntry.details, null, 2)}
                </pre>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuditLog;
