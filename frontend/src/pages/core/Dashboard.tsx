
import React, { useEffect, useState, useCallback } from 'react';
import { challengeApi, evidenceApi, validationApi, userApi } from '../../services/api';
import EvidenceCard from '../../components/EvidenceCard';
import ValidationCard from '../../components/ValidationCard';
import PricingPlanCard from '../../components/PricingPlanCard';
import { Badge } from '../../components/ui';
import AppButton from '../../ui/AppButton';
import { useConfig } from '../../services/configService';
import type { UserResponseDto, ChallengeResponseDto, ValidationResponseDto, EvidenceResponseDto } from '../../types/dtos';

const Dashboard: React.FC = () => {
  const [user, setUser] = useState<UserResponseDto | null>(null);
  const [challenges, setChallenges] = useState<ChallengeResponseDto[]>([]);
  const [evidences, setEvidences] = useState<EvidenceResponseDto[]>([]);
  const [validations, setValidations] = useState<ValidationResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState<{ code: string; message: string; correlationId: string } | null>(null);
  const [offline, setOffline] = useState(false);
  const [stale, setStale] = useState(false);
  const [toast, setToast] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const config = useConfig();

  // Mock data for retos activos
  const mockChallenges: any[] = [
    { id: '1', title: 'Reto 1', status: 'ACTIVE', pending: 2, lastActivity: '2025-09-18T10:00:00Z', urgent: true },
    { id: '2', title: 'Reto 2', status: 'ACTIVE', pending: 0, lastActivity: '2025-09-17T15:30:00Z', urgent: false },
  ];

  // Mock data for evidencias recientes
  const mockEvidences: any[] = [
    { id: '1', title: 'Evidencia 1', status: 'PENDING', createdAt: '2025-09-18T08:00:00Z' },
    { id: '2', title: 'Evidencia 2', status: 'APPROVED', createdAt: '2025-09-17T14:00:00Z' },
    { id: '3', title: 'Evidencia 3', status: 'REJECTED', createdAt: '2025-09-16T12:00:00Z' },
  ];

  // Mock data for validations
  const mockValidations = [
    { id: '1', evidenceId: '1', status: 'PENDING' },
    { id: '2', evidenceId: '2', status: 'PENDING' },
  ];

  // Metrics: POST /api/v1/events
  const postEvent = useCallback(async (eventType: string, details: Record<string, any> = {}) => {
    try {
      await fetch('/api/v1/events', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(localStorage.getItem('token') ? { 'Authorization': `Bearer ${localStorage.getItem('token')}` } : {}),
          'X-Correlation-Id': (window as any)['X-Correlation-Id'] || ''
        },
        body: JSON.stringify({ type: eventType, details, timestamp: new Date().toISOString() })
      });
    } catch {
      // Silent fail for metrics
    }
  }, []);

  // Data fetch
  const fetchDashboard = useCallback(async () => {
    setLoading(true);
    setError(null);
    setOffline(false);
    setStale(false);
    try {
      const [userData, challengesData, validationsData] = await Promise.all([
        userApi.getCurrentUser(),
        challengeApi.getChallenges({ page: 0, size: 3 }),
        validationApi.getValidations({ page: 0, size: 3 }),
      ]);
      setUser(userData);
      setChallenges(challengesData.content || mockChallenges);
      setValidations(validationsData.content || mockValidations);
      if (challengesData.content && challengesData.content.length > 0) {
        const allEvidences = await Promise.all(
          challengesData.content.map((c: ChallengeResponseDto) =>
            evidenceApi.getEvidence(c.id, { page: 0, size: 5 })
          )
        );
        setEvidences(allEvidences.flatMap((e) => e.content || []));
      } else {
        setEvidences(mockEvidences);
      }
      postEvent('dashboard_view', { userId: userData?.id });
    } catch (e: any) {
      if (!navigator.onLine) setOffline(true);
      setError({ code: 'FETCH_ERROR', message: e?.message || 'Error cargando datos', correlationId: 'corr-123' });
    } finally {
      setLoading(false);
    }
  }, [postEvent]);

  useEffect(() => {
    fetchDashboard();
    const handleOnline = () => { setOffline(false); fetchDashboard(); };
    const handleOffline = () => setOffline(true);
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);
    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, [fetchDashboard]);

  // Handlers
  const handleNewChallenge = () => {
    postEvent('dashboard_new_challenge_click', { userId: user?.id });
    // Navigate to new challenge
  };

  const handleUploadEvidence = () => {
    postEvent('dashboard_upload_evidence_click', { userId: user?.id });
    // Navigate to upload
  };

  const handleDecideValidation = (validationId: string) => {
    setUpdating(true);
    // Mock decide
    setTimeout(() => {
      setToast({ type: 'success', message: 'Validación decidida' });
      setUpdating(false);
      postEvent('dashboard_validation_decided', { validationId });
    }, 1000);
  };

  const handleUpgrade = () => {
    postEvent('dashboard_upgrade_click', { userId: user?.id });
    // Navigate to pricing
  };

  // Loading skeleton
  if (loading) {
    return (
      <div className="container-app">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 lg:gap-6">
          <div className="lg:col-span-2 space-y-6">
            <div className="animate-pulse">
              <div className="h-8 bg-gray-200 rounded w-1/4 mb-4"></div>
              <div className="h-12 bg-gray-200 rounded mb-6"></div>
              <div className="space-y-4">
                <div className="h-20 bg-gray-200 rounded"></div>
                <div className="h-20 bg-gray-200 rounded"></div>
              </div>
            </div>
          </div>
          <div className="lg:col-span-1 space-y-6">
            <div className="animate-pulse">
              <div className="h-6 bg-gray-200 rounded w-1/3 mb-4"></div>
              <div className="space-y-4">
                <div className="h-16 bg-gray-200 rounded"></div>
                <div className="h-16 bg-gray-200 rounded"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Empty state
  if (challenges.length === 0 && evidences.length === 0) {
    return (
      <div className="container-app text-center py-12">
        <h1 className="text-2xl font-bold mb-4">Bienvenido a tu Dashboard</h1>
        <p className="text-gray-600 mb-6">No tienes retos activos ni evidencias recientes. ¡Empieza creando tu primer reto!</p>
        <AppButton onClick={handleNewChallenge} variant="contained">Crear nuevo reto</AppButton>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="container-app text-center py-12">
        <div className="bg-red-50 border border-red-200 rounded p-4 mb-6" role="alert">
          <p className="text-red-800">{error.message}</p>
          <p className="text-sm text-red-600 mt-2">Código: {error.code} | ID: {error.correlationId}</p>
        </div>
        <AppButton onClick={fetchDashboard}>Reintentar</AppButton>
      </div>
    );
  }

  // Offline state
  if (offline) {
    return (
      <div className="container-app">
        <div className="bg-yellow-50 border border-yellow-200 rounded p-4 mb-6" role="alert">
          <p className="text-yellow-800">Estás sin conexión. Los datos pueden estar desactualizados.</p>
        </div>
        <AppButton onClick={fetchDashboard}>Reintentar</AppButton>
      </div>
    );
  }

  return (
    <div className="container-app">
      {/* Toast */}
      {toast && (
        <div className="fixed top-4 right-4 bg-green-500 text-white p-4 rounded shadow-lg z-50" role="alert">
          {toast.message}
          <button onClick={() => setToast(null)} className="ml-4 underline">Cerrar</button>
        </div>
      )}

      {/* Stale data banner */}
      {stale && (
        <div className="bg-orange-50 border border-orange-200 rounded p-2 mb-4 text-center" role="alert">
          Datos desactualizados. <button onClick={fetchDashboard} className="underline">Actualizar</button>
        </div>
      )}

      {/* Email verification banner */}
      {user && !(user as any).emailVerified && (
        <div className="bg-yellow-50 border border-yellow-200 rounded p-4 mb-4" role="alert">
          <Badge variant="warning">Email no verificado</Badge>
          <span className="ml-2">Verifica tu correo para acceder a todas las funciones.</span>
          <AppButton className="ml-auto" size="compact">Verificar ahora</AppButton>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 lg:gap-6">
        {/* Main content (2/3) */}
        <div className="lg:col-span-2 space-y-6">
          {/* Header */}
          <header className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-2xl font-bold">Dashboard</h1>
              <p className="text-gray-600">Tu actividad reciente y retos activos</p>
            </div>
            <div className="flex gap-2">
              <AppButton onClick={handleNewChallenge} variant="contained" disabled={updating}>
                Nuevo reto
              </AppButton>
              <AppButton onClick={handleUploadEvidence} variant="outlined" disabled={updating}>
                Subir avance
              </AppButton>
            </div>
          </header>

          {/* Retos activos */}
          <section aria-label="Retos activos">
            <h2 className="text-lg font-semibold mb-4">Retos activos</h2>
            <div className="space-y-4">
              {challenges.map(challenge => (
                <div key={challenge.id} className="border rounded p-4 shadow-sm">
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="font-medium">{challenge.title}</h3>
                    <Badge variant={challenge.status === 'ACTIVE' ? 'success' : 'info'}>{challenge.status}</Badge>
                  </div>
                  <p className="text-sm text-gray-600 mb-2">Pendientes: {(challenge as any).pending}</p>
                  {(challenge as any).urgent && <Badge variant="error">Urgente</Badge>}
                  <div className="mt-2">
                    <AppButton size="compact" onClick={() => postEvent('dashboard_challenge_detail_click', { challengeId: challenge.id })}>
                      Detalle
                    </AppButton>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Evidencias recientes */}
          <section aria-label="Mis evidencias recientes">
            <h2 className="text-lg font-semibold mb-4">Mis evidencias recientes</h2>
            <div className="space-y-3">
              {evidences.slice(0, 5).map(evidence => (
                <div key={evidence.id} className="border rounded p-3 shadow-sm">
                  <div className="flex items-center justify-between">
                    <span className="font-medium">{(evidence as any).title}</span>
                    <Badge variant={
                      evidence.status === 'APPROVED' ? 'success' :
                      evidence.status === 'REJECTED' ? 'error' : 'warning'
                    }>{evidence.status}</Badge>
                  </div>
                  <p className="text-sm text-gray-600 mt-1">
                    {new Date((evidence as any).createdAt).toLocaleDateString()}
                    <span className="ml-2" title={new Date((evidence as any).createdAt).toISOString()}>hace {Math.floor((Date.now() - new Date((evidence as any).createdAt).getTime()) / (1000 * 60 * 60))}h</span>
                  </p>
                </div>
              ))}
            </div>
          </section>

          {/* Upsell */}
          {config.isBillingEnabled && (
            <section aria-label="Mejora tu plan">
              <div className="border rounded p-4 bg-blue-50">
                <h3 className="font-semibold mb-2">Mejora tu experiencia</h3>
                <p className="text-sm text-gray-600 mb-4">Has usado el 75% de tu cuota mensual.</p>
                <div className="w-full bg-gray-200 rounded-full h-2 mb-4">
                  <div className="bg-blue-600 h-2 rounded-full" style={{ width: '75%' }}></div>
                </div>
                <AppButton onClick={handleUpgrade} variant="contained">Ver plan / upgrade</AppButton>
              </div>
            </section>
          )}
        </div>

        {/* Right rail (1/3) */}
        <aside className="lg:col-span-1 space-y-6">
          <section aria-label="Para mí">
            <h2 className="text-lg font-semibold mb-4">Para mí ({validations.length} en cola)</h2>
            <div className="space-y-3">
              {validations.map(validation => (
                <div key={validation.id} className="border rounded p-3 shadow-sm">
                  <p className="text-sm mb-2">Validación pendiente para evidencia {validation.evidenceId}</p>
                  <AppButton size="compact" onClick={() => handleDecideValidation((validation as any).id)} disabled={updating}>
                    Decidir
                  </AppButton>
                </div>
              ))}
            </div>
          </section>
        </aside>
      </div>
    </div>
  );
};

export default Dashboard;
