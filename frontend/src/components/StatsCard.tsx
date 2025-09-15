import React from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { UserStatsDto, ChallengeStatsDto, PlatformStatsDto } from "../types/dtos";
import {
  TrendingUp,
  TrendingDown,
  Users,
  Target,
  Trophy,
  CheckCircle,
  Clock,
  Star,
  Activity,
  BarChart3,
  PieChart,
  LineChart
} from "lucide-react";

type StatsData = UserStatsDto | ChallengeStatsDto | PlatformStatsDto;

interface StatsCardProps {
  data: StatsData;
  type: "user" | "challenge" | "platform";
  variant?: "compact" | "full" | "summary";
  title?: string;
  showTrend?: boolean;
  className?: string;
}

const StatsCard: React.FC<StatsCardProps> = ({
  data,
  type,
  variant = "compact",
  title,
  showTrend = true,
  className = ""
}) => {
  const getStatsIcon = (statType: string) => {
    const icons: Record<string, JSX.Element> = {
      users: <Users className="w-5 h-5" />,
      challenges: <Target className="w-5 h-5" />,
      completed: <CheckCircle className="w-5 h-5" />,
      active: <Clock className="w-5 h-5" />,
      evidence: <Trophy className="w-5 h-5" />,
      rating: <Star className="w-5 h-5" />,
      platform: <Activity className="w-5 h-5" />,
      success: <TrendingUp className="w-5 h-5" />,
      growth: <LineChart className="w-5 h-5" />
    };

    return icons[statType] || <BarChart3 className="w-5 h-5" />;
  };

  const formatNumber = (num: number) => {
    if (num >= 1000000) {
      return `${(num / 1000000).toFixed(1)}M`;
    } else if (num >= 1000) {
      return `${(num / 1000).toFixed(1)}K`;
    }
    return num.toString();
  };

  const formatPercentage = (value: number) => {
    return `${value.toFixed(1)}%`;
  };

  const getTrendIcon = (value: number, threshold: number = 0) => {
    if (value > threshold) {
      return <TrendingUp className="w-4 h-4 text-green-400" />;
    } else if (value < threshold) {
      return <TrendingDown className="w-4 h-4 text-red-400" />;
    }
    return <Activity className="w-4 h-4 text-gray-400" />;
  };

  const getProgressColor = (percentage: number) => {
    if (percentage >= 80) return "bg-green-500";
    if (percentage >= 60) return "bg-yellow-500";
    if (percentage >= 40) return "bg-orange-500";
    return "bg-red-500";
  };

  const getBadgeVariant = (percentage: number) => {
    if (percentage >= 70) return "success";
    if (percentage >= 50) return "warning";
    return "error";
  };

  const renderUserStats = (stats: UserStatsDto) => {
    const winRate = stats.challengesCompleted > 0 ? (stats.challengesWon / stats.challengesCompleted) * 100 : 0;
    const evidenceApprovalRate = stats.evidenceSubmitted > 0 ? (stats.evidenceApproved / stats.evidenceSubmitted) * 100 : 0;

    if (variant === "summary") {
      return (
        <div className="grid grid-cols-2 gap-4">
          <div className="text-center">
            <div className="text-2xl font-bold text-white">{formatNumber(stats.challengesCreated)}</div>
            <div className="text-sm text-gray-400">Creados</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-green-400">{formatPercentage(winRate)}</div>
            <div className="text-sm text-gray-400">Ganados</div>
          </div>
        </div>
      );
    }

    return (
      <div className="space-y-4">
        <div className="grid grid-cols-3 gap-4">
          <div className="text-center p-3 bg-gray-800/50 rounded-lg">
            <div className="flex items-center justify-center mb-2">
              {getStatsIcon("challenges")}
            </div>
            <div className="text-lg font-semibold text-white">{formatNumber(stats.challengesCreated)}</div>
            <div className="text-xs text-gray-400">Creados</div>
          </div>
          <div className="text-center p-3 bg-gray-800/50 rounded-lg">
            <div className="flex items-center justify-center mb-2">
              {getStatsIcon("completed")}
            </div>
            <div className="text-lg font-semibold text-blue-400">{formatNumber(stats.challengesCompleted)}</div>
            <div className="text-xs text-gray-400">Completados</div>
          </div>
          <div className="text-center p-3 bg-gray-800/50 rounded-lg">
            <div className="flex items-center justify-center mb-2">
              {getStatsIcon("success")}
            </div>
            <div className="text-lg font-semibold text-green-400">{formatNumber(stats.challengesWon)}</div>
            <div className="text-xs text-gray-400">Ganados</div>
          </div>
        </div>

        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Puntos Totales</span>
            <span className="text-sm font-medium text-white">{formatNumber(stats.totalPoints)}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Evidencias Enviadas</span>
            <span className="text-sm font-medium text-white">{formatNumber(stats.evidenceSubmitted)}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Evidencias Aprobadas</span>
            <div className="flex items-center space-x-2">
              <span className="text-sm font-medium text-green-400">{formatNumber(stats.evidenceApproved)}</span>
              {showTrend && getTrendIcon(stats.evidenceApproved)}
            </div>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Racha Actual</span>
            <div className="flex items-center space-x-2">
              <span className="text-sm font-medium text-orange-400">{stats.currentStreak}</span>
              <span className="text-xs text-gray-500">(máx: {stats.longestStreak})</span>
            </div>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Rating Promedio</span>
            <div className="flex items-center space-x-1">
              <Star className="w-4 h-4 text-yellow-400 fill-current" />
              <span className="text-sm font-medium text-white">{stats.averageRating.toFixed(1)}</span>
            </div>
          </div>
        </div>

        <div className="mt-4">
          <div className="flex justify-between text-xs text-gray-400 mb-1">
            <span>Aprobación de Evidencias</span>
            <span>{formatPercentage(evidenceApprovalRate)}</span>
          </div>
          <div className="w-full bg-gray-700 rounded-full h-2">
            <div
              className={`h-2 rounded-full ${getProgressColor(evidenceApprovalRate)}`}
              style={{ width: `${Math.min(evidenceApprovalRate, 100)}%` }}
            ></div>
          </div>
        </div>
      </div>
    );
  };

  const renderChallengeStats = (stats: ChallengeStatsDto) => {
    const completionRate = stats.completionRate || 0;

    if (variant === "summary") {
      return (
        <div className="grid grid-cols-2 gap-4">
          <div className="text-center">
            <div className="text-2xl font-bold text-white">{formatNumber(stats.participantCount)}</div>
            <div className="text-sm text-gray-400">Participantes</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-blue-400">{formatPercentage(completionRate)}</div>
            <div className="text-sm text-gray-400">Completado</div>
          </div>
        </div>
      );
    }

    return (
      <div className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="text-center p-3 bg-gray-800/50 rounded-lg">
            <div className="flex items-center justify-center mb-2">
              {getStatsIcon("users")}
            </div>
            <div className="text-lg font-semibold text-white">{formatNumber(stats.participantCount)}</div>
            <div className="text-xs text-gray-400">Participantes</div>
          </div>
          <div className="text-center p-3 bg-gray-800/50 rounded-lg">
            <div className="flex items-center justify-center mb-2">
              {getStatsIcon("completed")}
            </div>
            <div className="text-lg font-semibold text-green-400">{formatPercentage(completionRate)}</div>
            <div className="text-xs text-gray-400">Completado</div>
          </div>
        </div>

        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Evidencias</span>
            <span className="text-sm font-medium text-white">{formatNumber(stats.evidenceCount)}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Validaciones</span>
            <span className="text-sm font-medium text-purple-400">{formatNumber(stats.validationCount)}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Tiempo Promedio</span>
            <span className="text-sm font-medium text-orange-400">{stats.avgCompletionTime} min</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Rating Promedio</span>
            <div className="flex items-center space-x-1">
              <Star className="w-4 h-4 text-yellow-400 fill-current" />
              <span className="text-sm font-medium text-white">{stats.averageRating?.toFixed(1) || "N/A"}</span>
            </div>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Tasa de Finalización</span>
            <Badge variant={getBadgeVariant(completionRate)}>
              {formatPercentage(completionRate)}
            </Badge>
          </div>
        </div>
      </div>
    );
  };

  const renderPlatformStats = (stats: PlatformStatsDto) => {
    if (variant === "summary") {
      return (
        <div className="grid grid-cols-4 gap-3">
          <div className="text-center">
            <div className="text-xl font-bold text-white">{formatNumber(stats.totalUsers)}</div>
            <div className="text-xs text-gray-400">Usuarios</div>
          </div>
          <div className="text-center">
            <div className="text-xl font-bold text-blue-400">{formatNumber(stats.totalChallenges)}</div>
            <div className="text-xs text-gray-400">Desafíos</div>
          </div>
          <div className="text-center">
            <div className="text-xl font-bold text-green-400">{formatNumber(stats.totalEvidence)}</div>
            <div className="text-xs text-gray-400">Evidencias</div>
          </div>
          <div className="text-center">
            <div className="text-xl font-bold text-purple-400">{formatNumber(stats.activeUsers)}</div>
            <div className="text-xs text-gray-400">Activos</div>
          </div>
        </div>
      );
    }

    return (
      <div className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="text-center p-4 bg-gray-800/50 rounded-lg">
            <div className="flex items-center justify-center mb-2">
              {getStatsIcon("users")}
            </div>
            <div className="text-2xl font-bold text-white">{formatNumber(stats.totalUsers)}</div>
            <div className="text-sm text-gray-400">Usuarios Totales</div>
            <div className="text-xs text-green-400 mt-1">
              {formatNumber(stats.activeUsers)} activos
            </div>
          </div>
          <div className="text-center p-4 bg-gray-800/50 rounded-lg">
            <div className="flex items-center justify-center mb-2">
              {getStatsIcon("challenges")}
            </div>
            <div className="text-2xl font-bold text-blue-400">{formatNumber(stats.totalChallenges)}</div>
            <div className="text-sm text-gray-400">Desafíos</div>
            <div className="text-xs text-blue-400 mt-1">
              {formatNumber(stats.activeChallenges)} activos
            </div>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-3">
          <div className="text-center p-3 bg-gray-800/30 rounded-lg">
            <div className="text-lg font-semibold text-green-400">{formatNumber(stats.totalEvidence)}</div>
            <div className="text-xs text-gray-400">Evidencias</div>
          </div>
          <div className="text-center p-3 bg-gray-800/30 rounded-lg">
            <div className="text-lg font-semibold text-purple-400">{formatNumber(stats.pendingValidations)}</div>
            <div className="text-xs text-gray-400">Pendientes</div>
          </div>
          <div className="text-center p-3 bg-gray-800/30 rounded-lg">
            <div className="text-lg font-semibold text-yellow-400">{formatPercentage(stats.completionRate)}</div>
            <div className="text-xs text-gray-400">Finalización</div>
          </div>
        </div>

        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Rating Promedio</span>
            <div className="flex items-center space-x-1">
              <Star className="w-4 h-4 text-yellow-400 fill-current" />
              <span className="text-sm font-medium text-white">{stats.avgUserRating.toFixed(1)}</span>
            </div>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-400">Tasa de Finalización</span>
            <Badge variant={getBadgeVariant(stats.completionRate)}>
              {formatPercentage(stats.completionRate)}
            </Badge>
          </div>
        </div>
      </div>
    );
  };

  const renderContent = () => {
    switch (type) {
      case "user":
        return renderUserStats(data as UserStatsDto);
      case "challenge":
        return renderChallengeStats(data as ChallengeStatsDto);
      case "platform":
        return renderPlatformStats(data as PlatformStatsDto);
      default:
        return <div className="text-gray-400">Tipo de estadística no reconocido</div>;
    }
  };

  const getDefaultTitle = () => {
    switch (type) {
      case "user":
        return "Estadísticas de Usuario";
      case "challenge":
        return "Estadísticas del Desafío";
      case "platform":
        return "Estadísticas de la Plataforma";
      default:
        return "Estadísticas";
    }
  };

  if (variant === "summary") {
    return (
      <div className={`p-4 bg-gray-800/30 rounded-lg border border-gray-700 ${className}`}>
        {title && (
          <h3 className="text-sm font-medium text-gray-400 mb-3">{title}</h3>
        )}
        {renderContent()}
      </div>
    );
  }

  return (
    <Card className={`${className}`}>
      <div className="p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-center text-white">
              {type === "platform" ? <PieChart className="w-5 h-5" /> : getStatsIcon(type)}
            </div>
            <h3 className="text-lg font-semibold text-white">
              {title || getDefaultTitle()}
            </h3>
          </div>

          <Badge variant="info" size="sm">
            {type.toUpperCase()}
          </Badge>
        </div>

        {renderContent()}
      </div>
    </Card>
  );
};

export default StatsCard;
