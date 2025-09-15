import React, { useState, useEffect } from "react";
import { Card } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
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
  BarChart3
} from "lucide-react";

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
    const variants: Record<CoachStatus, "success" | "warning" | "error" | "secondary"> = {
      [CoachStatus.ACTIVE]: "success",
      [CoachStatus.INACTIVE]: "warning",
      [CoachStatus.SUSPENDED]: "error",
      [CoachStatus.PENDING_APPROVAL]: "secondary"
    };

    return <Badge variant={variants[status]}>{status}</Badge>;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-blue-900 to-indigo-900 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="text-center text-white">Cargando marketplace...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-blue-900 to-indigo-900 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-4">Marketplace Dashboard</h1>
          <p className="text-gray-300 text-lg">
            Gestiona el ecosistema de coaches y servicios premium
          </p>
        </div>

        {/* Stats Overview */}
        {stats && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <Card className="bg-gradient-to-r from-blue-600 to-blue-700 border-0">
              <div className="p-6 flex items-center space-x-4">
                <Users className="w-8 h-8 text-white" />
                <div>
                  <p className="text-blue-100 text-sm">Total Usuarios</p>
                  <p className="text-2xl font-bold text-white">{stats.totalUsers?.toLocaleString() || 0}</p>
                </div>
              </div>
            </Card>

            <Card className="bg-gradient-to-r from-green-600 to-green-700 border-0">
              <div className="p-6 flex items-center space-x-4">
                <Award className="w-8 h-8 text-white" />
                <div>
                  <p className="text-green-100 text-sm">Coaches Activos</p>
                  <p className="text-2xl font-bold text-white">{coaches.filter(c => c.status === CoachStatus.ACTIVE).length}</p>
                </div>
              </div>
            </Card>

            <Card className="bg-gradient-to-r from-purple-600 to-purple-700 border-0">
              <div className="p-6 flex items-center space-x-4">
                <TrendingUp className="w-8 h-8 text-white" />
                <div>
                  <p className="text-purple-100 text-sm">Desafíos Activos</p>
                  <p className="text-2xl font-bold text-white">{stats.activeChallenges?.toLocaleString() || 0}</p>
                </div>
              </div>
            </Card>

            <Card className="bg-gradient-to-r from-orange-600 to-orange-700 border-0">
              <div className="p-6 flex items-center space-x-4">
                <BarChart3 className="w-8 h-8 text-white" />
                <div>
                  <p className="text-orange-100 text-sm">Total Evidencias</p>
                  <p className="text-2xl font-bold text-white">{stats.totalEvidence?.toLocaleString() || 0}</p>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* Featured Coaches */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-white">Coaches Destacados</h2>
            <Button variant="secondary" size="sm">
              Ver todos
            </Button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {coaches.slice(0, 4).map((coach) => (
              <Card key={coach.id} className="bg-gray-800/50 border-gray-700 hover:border-blue-500 transition-all">
                <div className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <div className="w-12 h-12 bg-gradient-to-r from-green-500 to-blue-600 rounded-full flex items-center justify-center">
                      <Award className="w-6 h-6 text-white" />
                    </div>
                    {getCoachStatusBadge(coach.status)}
                  </div>

                  <div className="mb-4">
                    <h3 className="font-semibold text-white mb-1">Coach #{coach.id}</h3>
                    <p className="text-sm text-gray-400">{coach.specialties?.join(", ") || "General"}</p>
                  </div>

                  <div className="space-y-2 mb-4">
                    {coach.hourlyRate && (
                      <div className="flex items-center space-x-2">
                        <DollarSign className="w-4 h-4 text-green-500" />
                        <span className="text-sm text-gray-300">
                          ${coach.hourlyRate}/{coach.currency || "USD"} por hora
                        </span>
                      </div>
                    )}

                    <div className="flex items-center space-x-2">
                      <Star className="w-4 h-4 text-yellow-500" />
                      <span className="text-sm text-gray-300">
                        {coach.rating?.toFixed(1) || "N/A"} ({coach.reviewCount || 0} reseñas)
                      </span>
                    </div>
                  </div>

                  <div className="flex space-x-2">
                    <Button variant="primary" size="sm" className="flex-1">
                      <MessageCircle className="w-4 h-4 mr-1" />
                      Contactar
                    </Button>
                    <Button variant="secondary" size="sm">
                      <Calendar className="w-4 h-4" />
                    </Button>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="bg-gray-800/50 border-gray-700">
            <div className="p-6 text-center">
              <Award className="w-12 h-12 text-blue-500 mx-auto mb-4" />
              <h3 className="text-lg font-semibold text-white mb-2">Gestionar Coaches</h3>
              <p className="text-gray-400 text-sm mb-4">
                Revisar aplicaciones y gestionar el estado de los coaches
              </p>
              <Button variant="primary" className="w-full">
                Ir a Gestión
              </Button>
            </div>
          </Card>

          <Card className="bg-gray-800/50 border-gray-700">
            <div className="p-6 text-center">
              <BarChart3 className="w-12 h-12 text-purple-500 mx-auto mb-4" />
              <h3 className="text-lg font-semibold text-white mb-2">Analytics</h3>
              <p className="text-gray-400 text-sm mb-4">
                Ver métricas detalladas del marketplace
              </p>
              <Button variant="secondary" className="w-full">
                Ver Reportes
              </Button>
            </div>
          </Card>

          <Card className="bg-gray-800/50 border-gray-700">
            <div className="p-6 text-center">
              <DollarSign className="w-12 h-12 text-green-500 mx-auto mb-4" />
              <h3 className="text-lg font-semibold text-white mb-2">Pagos</h3>
              <p className="text-gray-400 text-sm mb-4">
                Gestionar pagos y comisiones
              </p>
              <Button variant="secondary" className="w-full">
                Ver Finanzas
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default MarketplaceDashboard;
