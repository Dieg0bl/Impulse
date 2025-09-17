import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import {
  PlusCircle,
  Target,
  TrendingUp,
  Award,
  BarChart3,
  Clock,
  CheckCircle2,
  AlertCircle,
  Trophy
} from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

import { tryListChallenges } from '../services/api';
import { ChallengeResponseDto } from '../types/dtos';
import { ChallengeStatus, ChallengeDifficulty } from '../types/enums';
import PageHeader from '../components/PageHeader';
import AnalyticsService, { BusinessKPI, CohortAnalysis, EngagementMetric, RevenueMetric } from '../services/AnalyticsService';

// Helper functions - removed as they're no longer needed with inline logic

// Mock data for charts - replace with real data
const progressData = [
  { name: 'Ene', value: 12 },
  { name: 'Feb', value: 19 },
  { name: 'Mar', value: 15 },
  { name: 'Abr', value: 25 },
  { name: 'May', value: 22 },
  { name: 'Jun', value: 30 },
];

const categoryData = [
  { name: 'Técnico', value: 45, color: '#0ea5e9' },
  { name: 'Liderazgo', value: 30, color: '#22c55e' },
  { name: 'Innovación', value: 15, color: '#f59e0b' },
  { name: 'Colaboración', value: 10, color: '#ef4444' }
];

const skillData = [
  { skill: 'React', level: 85 },
  { skill: 'TypeScript', level: 78 },
  { skill: 'Node.js', level: 72 },
  { skill: 'Python', level: 65 },
  { skill: 'DevOps', level: 58 }
];


const Dashboard: React.FC = () => {
  const [challenges, setChallenges] = useState<ChallengeResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  // Analytics states
  const [kpis, setKpis] = useState<BusinessKPI[]>([]);
  const [cohorts, setCohorts] = useState<CohortAnalysis[]>([]);
  const [engagement, setEngagement] = useState<EngagementMetric[]>([]);
  const [revenue, setRevenue] = useState<RevenueMetric|null>(null);

  useEffect(() => {
    const loadData = async () => {
      try {
        const challengesData = await tryListChallenges();
        setChallenges(challengesData);
        // Analytics
        setKpis(await AnalyticsService.getBusinessKPIs());
        setCohorts(await AnalyticsService.getCohortAnalysis('monthly'));
        setEngagement(await AnalyticsService.getEngagementMetrics('monthly'));
        setRevenue(await AnalyticsService.getRevenueMetrics('monthly'));
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  // Calculate stats
  const stats = {
    totalChallenges: challenges.length,
    activeChallenges: challenges.filter(c => c.status === ChallengeStatus.ACTIVE).length,
    completedChallenges: challenges.filter(c => c.status === ChallengeStatus.COMPLETED).length,
    points: challenges.reduce((acc, c) => acc + (c.status === ChallengeStatus.COMPLETED ? 100 : 0), 0)
  };

  if (loading) {
    return (
      <div className="bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="w-12 h-12 mx-auto mb-4 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
          <p className="text-gray-600 font-medium">Cargando tu dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 pb-24 space-y-8 max-w-7xl mx-auto">
        {/* Header */}
        <PageHeader
          title={
            <span className="font-extrabold text-primary-700 tracking-tight">
              Dashboard de Impulse
            </span>
          }
          subtitle="Tu centro de control para el crecimiento profesional. Visualiza tu progreso, explora retos y alcanza nuevas metas."
          actions={
            <button className="px-6 py-3 bg-primary-600 text-white rounded-xl font-semibold hover:bg-primary-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 inline-flex items-center gap-2">
              <PlusCircle className="w-5 h-5" />
              Crear Nuevo Reto
            </button>
          }
        />

        {/* --- BUSINESS ANALYTICS DASHBOARD --- */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.05 }}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8"
        >
          {/* KPIs */}
          {kpis.map((kpi) => {
            let displayValue = kpi.value;
            if (typeof kpi.value === 'number') {
              if (kpi.unit === '%') {
                displayValue = `${(kpi.value * 100).toFixed(1)}%`;
              }
            }
            return (
              <div key={kpi.name} className="bg-gradient-to-br from-gray-900 to-primary-800 rounded-2xl shadow-lg text-white p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-primary-100 text-sm font-medium">{kpi.name}</p>
                    <p className="text-3xl font-bold mt-1">{displayValue}</p>
                    <p className="text-primary-100 text-xs mt-2">{kpi.description}</p>
                  </div>
                  <div className="p-3 bg-white/20 rounded-xl backdrop-blur-sm">
                    <BarChart3 className="h-8 w-8 text-white" />
                  </div>
                </div>
                <div className="mt-3 text-xs text-primary-200">Actualizado: {kpi.lastUpdated.toLocaleDateString()}</div>
              </div>
            );
          })}
        </motion.div>

        {/* Cohorts & Engagement/Revenue */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {/* Cohort Analysis */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
            <div className="p-6 border-b border-gray-100">
              <h3 className="text-xl font-bold text-gray-900 flex items-center gap-3">
                <Award className="h-6 w-6 text-primary-600" />
                Cohortes & Retención
              </h3>
            </div>
            <div className="p-6 space-y-4">
              {cohorts.map((cohort) => (
                <div key={cohort.cohortName} className="mb-2">
                  <div className="font-semibold text-gray-800">{cohort.cohortName} <span className="text-xs text-gray-400">({cohort.startDate.toLocaleDateString()})</span></div>
                  <div className="text-xs text-gray-500 mb-1">Usuarios: {cohort.users.length} | LTV Prom: ${cohort.avgLTV.toFixed(2)}</div>
                  <div className="flex gap-2 text-xs">
                    Retención: {cohort.retentionRates.map((r, i) => {
                      const key = `${cohort.cohortName}-ret-${i}`;
                      return <span key={key} className="px-2 py-1 bg-primary-100 text-primary-700 rounded-lg mr-1">{(r*100).toFixed(0)}%</span>;
                    })}
                  </div>
                  <div className="flex gap-2 text-xs mt-1">
                    Conversión: {cohort.conversionRates.map((c, i) => {
                      const key = `${cohort.cohortName}-conv-${i}`;
                      return <span key={key} className="px-2 py-1 bg-green-100 text-green-700 rounded-lg mr-1">{(c*100).toFixed(0)}%</span>;
                    })}
                  </div>
                </div>
              ))}
            </div>
          </div>
          {/* Engagement & Revenue */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
            <div className="p-6 border-b border-gray-100">
              <h3 className="text-xl font-bold text-gray-900 flex items-center gap-3">
                <TrendingUp className="h-6 w-6 text-primary-600" />
                Engagement & Revenue
              </h3>
            </div>
            <div className="p-6 space-y-2">
              {engagement.map((em) => {
                let trendIcon = '●';
                if (em.trend === 'up') trendIcon = '▲';
                else if (em.trend === 'down') trendIcon = '▼';
                let trendColor = 'text-gray-400';
                if (em.trend === 'up') trendColor = 'text-green-600';
                else if (em.trend === 'down') trendColor = 'text-red-600';
                return (
                  <div key={em.metric} className="flex justify-between text-sm">
                    <span className="font-medium text-gray-700">{em.metric}</span>
                    <span className="font-bold text-primary-700">{em.value}</span>
                    <span className={`text-xs ${trendColor}`}>{trendIcon}</span>
                  </div>
                );
              })}
              {revenue && (
                <div className="mt-4">
                  <div className="font-semibold text-gray-700">Revenue (MRR): <span className="text-primary-700 font-bold">${revenue.mrr.toLocaleString()} {revenue.currency}</span></div>
                  <div className="text-xs text-gray-500">ARPU: ${revenue.arpu} | ARR: ${revenue.arr.toLocaleString()} {revenue.currency}</div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Stats Cards (Retos) */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8"
        >
          <div className="bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl shadow-lg text-white p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-primary-100 text-sm font-medium">Total de Retos</p>
                <p className="text-3xl font-bold mt-1">{stats.totalChallenges}</p>
                <p className="text-primary-100 text-xs mt-2">Activos en plataforma</p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl backdrop-blur-sm">
                <Target className="h-8 w-8 text-white" />
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-green-500 to-green-700 rounded-2xl shadow-lg text-white p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-green-100 text-sm font-medium">Completados</p>
                <p className="text-3xl font-bold mt-1">{stats.completedChallenges}</p>
                <p className="text-green-100 text-xs mt-2">Con éxito finalizado</p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl backdrop-blur-sm">
                <CheckCircle2 className="h-8 w-8 text-white" />
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-orange-500 to-red-600 rounded-2xl shadow-lg text-white p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-orange-100 text-sm font-medium">En Progreso</p>
                <p className="text-3xl font-bold mt-1">{stats.activeChallenges}</p>
                <p className="text-orange-100 text-xs mt-2">Actualmente activos</p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl backdrop-blur-sm">
                <Clock className="h-8 w-8 text-white" />
              </div>
            </div>
          </div>

          <div className="bg-gradient-to-br from-purple-500 to-indigo-700 rounded-2xl shadow-lg text-white p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-purple-100 text-sm font-medium">Puntos Ganados</p>
                <p className="text-3xl font-bold mt-1">{stats.points}</p>
                <p className="text-purple-100 text-xs mt-2">Acumulados totales</p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl backdrop-blur-sm">
                <Trophy className="h-8 w-8 text-white" />
              </div>
            </div>
          </div>
        </motion.div>

        {/* Charts Row */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Progress Chart */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200 h-96">
              <div className="p-6 border-b border-gray-100">
                <h3 className="text-xl font-bold text-gray-900 flex items-center gap-3">
                  <TrendingUp className="h-6 w-6 text-primary-600" />
                  Progreso Mensual
                </h3>
              </div>
              <div className="p-6">
                <ResponsiveContainer width="100%" height={250}>
                  <AreaChart data={progressData}>
                    <defs>
                      <linearGradient id="colorProgress" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#3B82F6" stopOpacity={0.3}/>
                        <stop offset="95%" stopColor="#3B82F6" stopOpacity={0}/>
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
                    <XAxis dataKey="name" stroke="#6B7280" fontSize={12} />
                    <YAxis stroke="#6B7280" fontSize={12} />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: 'white',
                        border: '1px solid #D1D5DB',
                        borderRadius: '12px',
                        boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)'
                      }}
                    />
                    <Area
                      type="monotone"
                      dataKey="value"
                      stroke="#3B82F6"
                      strokeWidth={3}
                      fillOpacity={1}
                      fill="url(#colorProgress)"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
          </motion.div>

          {/* Category Distribution */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200 h-96">
              <div className="p-6 border-b border-gray-100">
                <h3 className="text-xl font-bold text-gray-900 flex items-center gap-3">
                  <BarChart3 className="h-6 w-6 text-primary-600" />
                  Distribución por Categoría
                </h3>
              </div>
              <div className="p-6">
                <ResponsiveContainer width="100%" height={220}>
                  <PieChart>
                    <Pie
                      data={categoryData}
                      cx="50%"
                      cy="50%"
                      outerRadius={80}
                      dataKey="value"
                      label={(entry: any) => `${entry.name} ${(entry.percent * 100).toFixed(0)}%`}
                    >
                      {categoryData.map((entry) => (
                        <Cell key={`cell-${entry.name}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </div>
          </motion.div>
        </div>

        {/* Bottom Row */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Recent Challenges */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.4 }}
            className="lg:col-span-2"
          >
            <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
              <div className="p-6 border-b border-gray-100">
                <h3 className="text-xl font-bold text-gray-900 flex items-center gap-3">
                  <Target className="h-6 w-6 text-primary-600" />
                  Retos Recientes
                </h3>
              </div>
              <div className="p-6">
                {challenges.length === 0 ? (
                  <div className="text-center py-12">
                    <AlertCircle className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-500 mb-4">No hay retos disponibles</p>
                    <button className="px-6 py-3 bg-primary-600 text-white rounded-xl font-semibold hover:bg-primary-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5">
                      Crear primer reto
                    </button>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {challenges.slice(0, 5).map((challenge, index) => {
                      const getDifficultyColor = () => {
                        switch (challenge.difficulty) {
                          case ChallengeDifficulty.BEGINNER: return 'bg-green-100 text-green-800 border-green-200';
                          case ChallengeDifficulty.INTERMEDIATE: return 'bg-yellow-100 text-yellow-800 border-yellow-200';
                          case ChallengeDifficulty.ADVANCED: return 'bg-orange-100 text-orange-800 border-orange-200';
                          case ChallengeDifficulty.EXPERT: return 'bg-red-100 text-red-800 border-red-200';
                          default: return 'bg-gray-100 text-gray-800 border-gray-200';
                        }
                      };

                      const getStatusColor = () => {
                        switch (challenge.status) {
                          case ChallengeStatus.COMPLETED: return 'bg-green-100 text-green-800 border-green-200';
                          case ChallengeStatus.ACTIVE: return 'bg-blue-100 text-blue-800 border-blue-200';
                          default: return 'bg-gray-100 text-gray-800 border-gray-200';
                        }
                      };

                      return (
                        <motion.div
                          key={challenge.id}
                          initial={{ opacity: 0, x: -20 }}
                          animate={{ opacity: 1, x: 0 }}
                          transition={{ delay: index * 0.1 }}
                          className="flex items-center justify-between p-5 bg-gradient-to-r from-gray-50 via-white to-gray-50 rounded-xl border border-gray-200 hover:shadow-lg transition-all duration-200 hover:border-primary-200"
                        >
                          <div className="flex-1">
                            <h4 className="font-semibold text-gray-900 text-lg">{challenge.title}</h4>
                            <p className="text-sm text-gray-600 mt-1 line-clamp-2">{challenge.description}</p>
                            <div className="flex items-center gap-2 mt-3">
                              <span className="px-3 py-1 rounded-lg text-xs font-medium bg-gray-100 text-gray-800 border border-gray-200">
                                {challenge.category}
                              </span>
                              <span className={`px-3 py-1 rounded-lg text-xs font-medium border ${getDifficultyColor()}`}>
                                {challenge.difficulty}
                              </span>
                              <span className={`px-3 py-1 rounded-lg text-xs font-medium border ${getStatusColor()}`}>
                                {challenge.status}
                              </span>
                            </div>
                          </div>
                          <button className="px-4 py-2 text-primary-600 hover:text-primary-700 hover:bg-primary-50 rounded-lg font-medium transition-all duration-200">
                            Ver detalles
                          </button>
                        </motion.div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>
          </motion.div>

          {/* Skills Progress */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.5 }}
          >
            <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
              <div className="p-6 border-b border-gray-100">
                <h3 className="text-xl font-bold text-gray-900 flex items-center gap-3">
                  <Award className="h-6 w-6 text-primary-600" />
                  Progreso de Habilidades
                </h3>
              </div>
              <div className="p-6">
                <div className="space-y-6">
                  {skillData.map((skill, index) => (
                    <motion.div
                      key={skill.skill}
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: index * 0.1 }}
                      className="space-y-3"
                    >
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-semibold text-gray-900">{skill.skill}</span>
                        <span className="text-sm font-bold text-primary-600">{skill.level}%</span>
                      </div>
                      <div className="h-3 rounded-full bg-gray-200 overflow-hidden">
                        <motion.div
                          className="h-3 rounded-full bg-gradient-to-r from-primary-500 to-primary-600"
                          initial={{ width: 0 }}
                          animate={{ width: `${skill.level}%` }}
                          transition={{ duration: 1, delay: index * 0.2 }}
                        />
                      </div>
                    </motion.div>
                  ))}
                </div>
              </div>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
