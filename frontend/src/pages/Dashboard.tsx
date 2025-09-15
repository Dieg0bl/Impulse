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
import { Card, CardHeader, CardTitle, CardContent, Button, Badge, Progress } from '../components/ui';

// Helper functions
const getDifficultyVariant = (difficulty: ChallengeDifficulty) => {
  switch (difficulty) {
    case ChallengeDifficulty.BEGINNER:
      return 'success';
    case ChallengeDifficulty.INTERMEDIATE:
      return 'warning';
    case ChallengeDifficulty.ADVANCED:
    case ChallengeDifficulty.EXPERT:
      return 'error';
    default:
      return 'secondary';
  }
};

const getStatusVariant = (status: ChallengeStatus) => {
  switch (status) {
    case ChallengeStatus.COMPLETED:
      return 'success';
    case ChallengeStatus.ACTIVE:
      return 'primary';
    default:
      return 'secondary';
  }
};

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

  useEffect(() => {
    const loadData = async () => {
      try {
        const challengesData = await tryListChallenges();
        setChallenges(challengesData);
      } catch (error) {
        console.error('Error loading challenges:', error);
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

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: { duration: 0.5 }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="spinner w-12 h-12 mx-auto mb-4"></div>
          <p className="text-gray-600">Cargando dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      <motion.div
        className="container mx-auto px-6 py-8"
        variants={containerVariants}
        initial="hidden"
        animate="visible"
      >
        {/* Header */}
        <motion.div variants={itemVariants} className="mb-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-4xl font-bold bg-gradient-to-r from-primary-600 to-primary-800 bg-clip-text text-transparent">
                Dashboard Impulse
              </h1>
              <p className="text-gray-600 mt-2">
                Bienvenido de vuelta. Aquí tienes tu progreso y estadísticas.
              </p>
            </div>
            <Button
              variant="primary"
              size="lg"
              icon={<PlusCircle size={20} />}
              className="shadow-colored"
            >
              Nuevo Reto
            </Button>
          </div>
        </motion.div>

        {/* Stats Cards */}
        <motion.div
          variants={itemVariants}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8"
        >
          <Card hover className="gradient-primary text-white border-0">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-white/80 text-sm font-medium">Total Retos</p>
                  <p className="text-3xl font-bold">{stats.totalChallenges}</p>
                </div>
                <Target className="h-8 w-8 text-white/80" />
              </div>
            </CardContent>
          </Card>

          <Card hover className="gradient-success text-white border-0">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-white/80 text-sm font-medium">Completados</p>
                  <p className="text-3xl font-bold">{stats.completedChallenges}</p>
                </div>
                <CheckCircle2 className="h-8 w-8 text-white/80" />
              </div>
            </CardContent>
          </Card>

          <Card hover className="gradient-warm text-white border-0">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-white/80 text-sm font-medium">En Progreso</p>
                  <p className="text-3xl font-bold">{stats.activeChallenges}</p>
                </div>
                <Clock className="h-8 w-8 text-white/80" />
              </div>
            </CardContent>
          </Card>

          <Card hover className="gradient-cool text-white border-0">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-white/80 text-sm font-medium">Puntos</p>
                  <p className="text-3xl font-bold">{stats.points}</p>
                </div>
                <Trophy className="h-8 w-8 text-white/80" />
              </div>
            </CardContent>
          </Card>
        </motion.div>

        {/* Charts Row */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Progress Chart */}
          <motion.div variants={itemVariants}>
            <Card className="h-96">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <TrendingUp className="h-5 w-5 text-primary-600" />
                  Progreso Mensual
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={250}>
                  <AreaChart data={progressData}>
                    <defs>
                      <linearGradient id="colorProgress" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#0ea5e9" stopOpacity={0.3}/>
                        <stop offset="95%" stopColor="#0ea5e9" stopOpacity={0}/>
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
                    <XAxis dataKey="name" className="text-sm" />
                    <YAxis className="text-sm" />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: 'white',
                        border: '1px solid #e5e7eb',
                        borderRadius: '8px',
                        boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
                      }}
                    />
                    <Area
                      type="monotone"
                      dataKey="value"
                      stroke="#0ea5e9"
                      strokeWidth={3}
                      fillOpacity={1}
                      fill="url(#colorProgress)"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </motion.div>

          {/* Category Distribution */}
          <motion.div variants={itemVariants}>
            <Card className="h-96">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <BarChart3 className="h-5 w-5 text-primary-600" />
                  Distribución por Categoría
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={200}>
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
              </CardContent>
            </Card>
          </motion.div>
        </div>

        {/* Bottom Row */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Recent Challenges */}
          <motion.div variants={itemVariants} className="lg:col-span-2">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Target className="h-5 w-5 text-primary-600" />
                  Retos Recientes
                </CardTitle>
              </CardHeader>
              <CardContent>
                {challenges.length === 0 ? (
                  <div className="text-center py-12">
                    <AlertCircle className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-500">No hay retos disponibles</p>
                    <Button variant="primary" className="mt-4">
                      Crear primer reto
                    </Button>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {challenges.slice(0, 5).map((challenge, index) => (
                      <motion.div
                        key={challenge.id}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: index * 0.1 }}
                        className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                      >
                        <div className="flex-1">
                          <h4 className="font-semibold text-gray-900">{challenge.title}</h4>
                          <p className="text-sm text-gray-600 mt-1">{challenge.description}</p>
                          <div className="flex items-center gap-2 mt-2">
                            <Badge variant="secondary" size="sm">{challenge.category}</Badge>
                            <Badge
                              variant={getDifficultyVariant(challenge.difficulty)}
                              size="sm"
                            >
                              {challenge.difficulty}
                            </Badge>
                            <Badge
                              variant={getStatusVariant(challenge.status)}
                              size="sm"
                            >
                              {challenge.status}
                            </Badge>
                          </div>
                        </div>
                        <Button variant="ghost" size="sm">
                          Ver detalles
                        </Button>
                      </motion.div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </motion.div>

          {/* Skills Progress */}
          <motion.div variants={itemVariants}>
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Award className="h-5 w-5 text-primary-600" />
                  Habilidades
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {skillData.map((skill, index) => (
                    <motion.div
                      key={skill.skill}
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: index * 0.1 }}
                      className="space-y-2"
                    >
                      <div className="flex justify-between items-center">
                        <span className="text-sm font-medium text-gray-700">{skill.skill}</span>
                        <span className="text-sm text-gray-500">{skill.level}%</span>
                      </div>
                      <Progress
                        value={skill.level}
                        variant="primary"
                        size="sm"
                        animate={true}
                      />
                    </motion.div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </motion.div>
        </div>
      </motion.div>
    </div>
  );
};

export default Dashboard;
