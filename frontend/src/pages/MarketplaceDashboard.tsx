import React, { useState, useEffect } from "react";
import { Card, CardContent, CardActions } from "@mui/material";
import { coachApi, statsApi } from "../services/api";
import { CoachResponseDto, PlatformStatsDto } from "../types/dtos";
import { CoachStatus } from "../types/enums";
import {
  Users,
  Star,
  TrendingUp,
  Award,
  MessageCircle,
  Calendar,
  DollarSign,
  BarChart3,
  RefreshCw
} from "lucide-react";
import PageHeader from "../components/PageHeader";
import DataState from "../components/DataState";
import { AppButton } from "../ui/AppButton";

const MarketplaceDashboard: React.FC = () => {
  const [coaches, setCoaches] = useState<CoachResponseDto[]>([]);
  const [stats, setStats] = useState<PlatformStatsDto | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [coachesResponse, statsResponse] = await Promise.all([
        coachApi.getCoaches({ page: 0, size: 8 }),
        statsApi.getPlatformStats()
      ]);

      setCoaches(coachesResponse.content);
      setStats(statsResponse);
    } catch (error) {
      console.error("Error loading marketplace data:", error);
    } finally {
      setLoading(false);
    }
  };

  const getCoachStatusBadge = (status: CoachStatus) => {
    const statusConfig = {
      [CoachStatus.ACTIVE]: "bg-green-100 text-green-800",
      [CoachStatus.INACTIVE]: "bg-yellow-100 text-yellow-800",
      [CoachStatus.SUSPENDED]: "bg-red-100 text-red-800",
      [CoachStatus.PENDING_APPROVAL]: "bg-gray-100 text-gray-800"
    };

    return (
      <span className={`px-2 py-1 text-xs font-semibold rounded-full ${statusConfig[status]}`}>
        {status}
      </span>
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 via-white to-primary-50">
      <div className="container mx-auto px-4 py-8 space-y-8">
        <PageHeader
          title={
            <span className="text-3xl font-extrabold text-primary-700 tracking-tight">
              Marketplace
            </span>
          }
          subtitle={
            <span className="text-gray-600 text-lg leading-relaxed">
              Gestiona el ecosistema de coaches, servicios premium y acciones rápidas de operación
            </span>
          }
          actions={
            <button
              onClick={loadData}
              disabled={loading}
              className="flex items-center gap-2 px-6 py-3 bg-white border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 hover:border-gray-400 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
              {loading ? 'Actualizando' : 'Actualizar'}
            </button>
          }
        />

      <DataState
        loading={loading}
        error={null}
        empty={!loading && !stats}
        emptyTitle="Sin métricas"
        emptyDescription="No se pudieron cargar las métricas del marketplace"
        skeleton={
          <div className="space-y-10" aria-label="Cargando datos del marketplace">
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              {['a','b','c','d'].map(id => (
                <div key={id} className="surface p-5 rounded-xl animate-pulse space-y-3">
                  <div className="h-5 w-1/4 bg-gray-200 dark:bg-gray-700 rounded" />
                  <div className="h-8 w-1/2 bg-gray-200 dark:bg-gray-700 rounded" />
                </div>
              ))}
            </div>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              {['e','f','g','h'].map(id => (
                <div key={id} className="surface p-6 rounded-xl animate-pulse space-y-4">
                  <div className="h-12 w-12 rounded-full bg-gray-200 dark:bg-gray-700" />
                  <div className="h-4 w-1/2 bg-gray-200 dark:bg-gray-700 rounded" />
                  <div className="h-3 w-2/3 bg-gray-200 dark:bg-gray-700 rounded" />
                  <div className="h-24 w-full bg-gray-200 dark:bg-gray-700 rounded" />
                </div>
              ))}
            </div>
          </div>
        }
      >
        {stats && (
          <section aria-labelledby="market-stats-heading" className="space-y-6">
            <h2 id="market-stats-heading" className="heading-2">Resumen de plataforma</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
              <Card className="surface p-5 rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="w-11 h-11 rounded-full bg-blue-100 dark:bg-blue-900/40 flex items-center justify-center">
                    <Users className="w-6 h-6 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div>
                    <p className="text-xs uppercase tracking-wide text-gray-500 dark:text-gray-400">Total Usuarios</p>
                    <p className="text-2xl font-semibold text-gray-900 dark:text-white">{stats.totalUsers?.toLocaleString() || 0}</p>
                  </div>
                </div>
              </Card>
              <Card className="surface p-5 rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="w-11 h-11 rounded-full bg-green-100 dark:bg-green-900/40 flex items-center justify-center">
                    <Award className="w-6 h-6 text-green-600 dark:text-green-400" />
                  </div>
                  <div>
                    <p className="text-xs uppercase tracking-wide text-gray-500 dark:text-gray-400">Coaches Activos</p>
                    <p className="text-2xl font-semibold text-gray-900 dark:text-white">{coaches.filter(c => c.status === CoachStatus.ACTIVE).length}</p>
                  </div>
                </div>
              </Card>
              <Card className="surface p-5 rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="w-11 h-11 rounded-full bg-purple-100 dark:bg-purple-900/40 flex items-center justify-center">
                    <TrendingUp className="w-6 h-6 text-purple-600 dark:text-purple-400" />
                  </div>
                  <div>
                    <p className="text-xs uppercase tracking-wide text-gray-500 dark:text-gray-400">Desafíos Activos</p>
                    <p className="text-2xl font-semibold text-gray-900 dark:text-white">{stats.activeChallenges?.toLocaleString() || 0}</p>
                  </div>
                </div>
              </Card>
              <Card className="surface p-5 rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="w-11 h-11 rounded-full bg-amber-100 dark:bg-amber-900/40 flex items-center justify-center">
                    <BarChart3 className="w-6 h-6 text-amber-600 dark:text-amber-400" />
                  </div>
                  <div>
                    <p className="text-xs uppercase tracking-wide text-gray-500 dark:text-gray-400">Total Evidencias</p>
                    <p className="text-2xl font-semibold text-gray-900 dark:text-white">{stats.totalEvidence?.toLocaleString() || 0}</p>
                  </div>
                </div>
              </Card>
            </div>
          </section>
        )}

        <section aria-labelledby="featured-coaches-heading" className="space-y-6 pt-2">
          <div className="flex items-center justify-between flex-wrap gap-4">
            <h2 id="featured-coaches-heading" className="heading-2">Coaches destacados</h2>
            <AppButton variant="text" size="compact">Ver todos</AppButton>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
            {coaches.slice(0, 4).map((coach) => (
              <Card key={coach.id} className="surface rounded-xl border border-gray-200 dark:border-gray-700/60 hover:border-blue-400 focus-within:border-blue-500 transition-colors">
                <div className="p-5 space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="w-12 h-12 bg-gradient-to-r from-green-500 to-blue-600 rounded-full flex items-center justify-center">
                      <Award className="w-6 h-6 text-white" />
                    </div>
                    {getCoachStatusBadge(coach.status)}
                  </div>
                  <div className="space-y-1">
                    <h3 className="text-sm font-medium text-gray-900 dark:text-white">Coach #{coach.id}</h3>
                    <p className="text-xs text-gray-600 dark:text-gray-400 line-clamp-2">{coach.specialties?.join(", ") || "General"}</p>
                  </div>
                  <div className="space-y-2 text-sm">
                    {coach.hourlyRate && (
                      <div className="flex items-center gap-2 text-gray-700 dark:text-gray-300">
                        <DollarSign className="w-4 h-4 text-green-500" />
                        <span>${coach.hourlyRate}/{coach.currency || "USD"} por hora</span>
                      </div>
                    )}
                    <div className="flex items-center gap-2 text-gray-700 dark:text-gray-300">
                      <Star className="w-4 h-4 text-yellow-500" />
                      <span>{coach.rating?.toFixed(1) || "N/A"} ({coach.reviewCount || 0} reseñas)</span>
                    </div>
                  </div>
                  <div className="flex gap-2 pt-1">
                    <AppButton variant="contained" size="compact" className="flex-1">
                      <MessageCircle className="w-4 h-4 mr-1" />
                      Contactar
                    </AppButton>
                    <AppButton variant="outlined" size="compact" aria-label="Ver calendario">
                      <Calendar className="w-4 h-4" />
                    </AppButton>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </section>

        <section aria-labelledby="quick-actions-heading" className="space-y-6">
          <h2 id="quick-actions-heading" className="heading-2">Acciones rápidas</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            <Card className="surface rounded-xl">
              <div className="p-6 text-center space-y-3">
                <Award className="w-10 h-10 text-blue-500 mx-auto" />
                <h3 className="heading-4">Gestionar Coaches</h3>
                <p className="text-xs text-gray-600 dark:text-gray-400">Revisar aplicaciones y gestionar el estado de los coaches</p>
                <AppButton variant="contained" className="w-full">Ir a Gestión</AppButton>
              </div>
            </Card>
            <Card className="surface rounded-xl">
              <div className="p-6 text-center space-y-3">
                <BarChart3 className="w-10 h-10 text-purple-500 mx-auto" />
                <h3 className="heading-4">Analytics</h3>
                <p className="text-xs text-gray-600 dark:text-gray-400">Ver métricas detalladas del marketplace</p>
                <AppButton variant="outlined" className="w-full">Ver Reportes</AppButton>
              </div>
            </Card>
            <Card className="surface rounded-xl">
              <div className="p-6 text-center space-y-3">
                <DollarSign className="w-10 h-10 text-green-500 mx-auto" />
                <h3 className="heading-4">Pagos</h3>
                <p className="text-xs text-gray-600 dark:text-gray-400">Gestionar pagos y comisiones</p>
                <AppButton variant="outlined" className="w-full">Ver Finanzas</AppButton>
              </div>
            </Card>
          </div>
        </section>
        </DataState>
      </div>
    </div>
  );
};

export default MarketplaceDashboard;
