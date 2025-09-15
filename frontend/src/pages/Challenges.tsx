// src/pages/Challenges.tsx
import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Plus,
  Search,
  LayoutGrid,   // Usamos LayoutGrid en lugar de Grid3x3 para evitar conflictos
  List as ListIcon,
  Calendar,
  Users,
  Clock,
  Target,
  TrendingUp,
  Award,
  ChevronDown,
  SlidersHorizontal,
} from 'lucide-react';

import mockStore, { Challenge } from '../services/mockStore';
import { ChallengeStatus, ChallengeDifficulty } from '../types/enums';
import { Card, CardHeader, CardTitle, CardContent, Button, Badge, Progress } from '../components/ui';

interface FilterState {
  category: string;
  difficulty: string;
  status: string;
  searchTerm: string;
}

const Challenges: React.FC = () => {
  const [challenges, setChallenges] = useState<Challenge[]>([]);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [showFilters, setShowFilters] = useState(false);
  const [filters, setFilters] = useState<FilterState>({
    category: 'all',
    difficulty: 'all',
    status: 'all',
    searchTerm: '',
  });

  useEffect(() => {
    const loadChallenges = async () => {
      try {
        setLoading(true);
        const challengesData = await mockStore.listChallenges();
        setChallenges(challengesData);
      } catch (error) {
        console.error('Error loading challenges:', error);
      } finally {
        setLoading(false);
      }
    };
    loadChallenges();
  }, []);

  // Helpers
  const getDifficultyColor = (difficulty: ChallengeDifficulty | string) => {
    switch (difficulty) {
      case ChallengeDifficulty.BEGINNER:
        return 'success';
      case ChallengeDifficulty.INTERMEDIATE:
        return 'warning';
      case ChallengeDifficulty.ADVANCED:
        return 'error';
      default:
        return 'secondary';
    }
  };

  const getStatusColor = (status: ChallengeStatus | string) => {
    switch (status) {
      case ChallengeStatus.COMPLETED:
        return 'success';
      case ChallengeStatus.ACTIVE:
        return 'primary';
      case ChallengeStatus.DRAFT:
        return 'secondary';
      default:
        return 'secondary';
    }
  };

  // Animations
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: { opacity: 1, transition: { staggerChildren: 0.1 } },
  };
  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: { y: 0, opacity: 1, transition: { duration: 0.5 } },
  };

  // Filtering
  const filteredChallenges = challenges.filter((challenge) => {
    const matchesSearch =
      challenge.title.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
      challenge.description.toLowerCase().includes(filters.searchTerm.toLowerCase());

    const matchesCategory = filters.category === 'all' || challenge.category === filters.category;
    const matchesDifficulty =
      filters.difficulty === 'all' || challenge.difficulty === (filters.difficulty as ChallengeDifficulty);
    const matchesStatus = filters.status === 'all' || challenge.status === (filters.status as ChallengeStatus);

    return matchesSearch && matchesCategory && matchesDifficulty && matchesStatus;
  });

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="spinner w-12 h-12 mx-auto mb-4" />
          <p className="text-gray-600">Cargando retos...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <motion.div
        className="container mx-auto px-6 py-8"
        variants={containerVariants}
        initial="hidden"
        animate="visible"
      >
        {/* Header */}
        <motion.div variants={itemVariants} className="mb-8">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
            <div>
              <h1 className="text-4xl font-bold bg-gradient-to-r from-primary-600 to-primary-800 bg-clip-text text-transparent">
                Retos Impulse
              </h1>
              <p className="text-gray-600 mt-2">
                Descubre y participa en retos que impulsen tu crecimiento personal y profesional
              </p>
            </div>
            <div className="flex items-center gap-3">
              <Button variant="outline" size="lg">
                <Users className="w-5 h-5 mr-2" />
                Mis Retos
              </Button>
              <Link to="/challenges/create">
                <Button variant="primary" size="lg" className="shadow-colored">
                  <Plus className="w-5 h-5 mr-2" />
                  Crear Reto
                </Button>
              </Link>
            </div>
          </div>
        </motion.div>

        {/* Stats */}
        <motion.div variants={itemVariants} className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="text-center">
            <CardContent className="p-6">
              <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                <Target className="w-6 h-6 text-primary-600" />
              </div>
              <div className="text-2xl font-bold text-gray-900">{challenges.length}</div>
              <div className="text-sm text-gray-600">Total Retos</div>
            </CardContent>
          </Card>

          <Card className="text-center">
            <CardContent className="p-6">
              <div className="w-12 h-12 bg-success-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                <Award className="w-6 h-6 text-success-600" />
              </div>
              <div className="text-2xl font-bold text-gray-900">
                {challenges.filter((c) => c.status === ChallengeStatus.COMPLETED).length}
              </div>
              <div className="text-sm text-gray-600">Completados</div>
            </CardContent>
          </Card>

          <Card className="text-center">
            <CardContent className="p-6">
              <div className="w-12 h-12 bg-warning-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                <Clock className="w-6 h-6 text-warning-600" />
              </div>
              <div className="text-2xl font-bold text-gray-900">
                {challenges.filter((c) => c.status === ChallengeStatus.ACTIVE).length}
              </div>
              <div className="text-sm text-gray-600">En Progreso</div>
            </CardContent>
          </Card>

          <Card className="text-center">
            <CardContent className="p-6">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                <TrendingUp className="w-6 h-6 text-blue-600" />
              </div>
              <div className="text-2xl font-bold text-gray-900">85%</div>
              <div className="text-sm text-gray-600">Tasa Éxito</div>
            </CardContent>
          </Card>
        </motion.div>

        {/* Search & Filters */}
        <motion.div variants={itemVariants} className="mb-6">
          <Card>
            <CardContent className="p-6">
              <div className="flex flex-col lg:flex-row gap-4">
                {/* Search */}
                <div className="flex-1">
                  <div className="relative">
                    <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input
                      type="text"
                      placeholder="Buscar retos..."
                      value={filters.searchTerm}
                      onChange={(e) => setFilters((prev) => ({ ...prev, searchTerm: e.target.value }))}
                      className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
                    />
                  </div>
                </div>

                {/* View Mode */}
                <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden">
                  <button
                    onClick={() => setViewMode('grid')}
                    className={`p-3 transition-colors ${
                      viewMode === 'grid' ? 'bg-primary-500 text-white' : 'bg-white text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    <LayoutGrid className="w-5 h-5" />
                  </button>
                  <button
                    onClick={() => setViewMode('list')}
                    className={`p-3 transition-colors ${
                      viewMode === 'list' ? 'bg-primary-500 text-white' : 'bg-white text-gray-600 hover:bg-gray-50'
                    }`}
                  >
                    <ListIcon className="w-5 h-5" />
                  </button>
                </div>

                {/* Filters toggle */}
                <Button variant="outline" onClick={() => setShowFilters((v) => !v)} className="flex items-center gap-2">
                  <SlidersHorizontal className="w-5 h-5" />
                  Filtros
                  <ChevronDown className={`w-4 h-4 transition-transform ${showFilters ? 'rotate-180' : ''}`} />
                </Button>
              </div>

              {/* Filters panel */}
              {showFilters && (
                <motion.div
                  initial={{ height: 0, opacity: 0 }}
                  animate={{ height: 'auto', opacity: 1 }}
                  className="mt-6 pt-6 border-t border-gray-200"
                >
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {/* Category */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Categoría</label>
                      <select
                        value={filters.category}
                        onChange={(e) => setFilters((prev) => ({ ...prev, category: e.target.value }))}
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      >
                        <option value="all">Todas las categorías</option>
                        <option value="fitness">Fitness</option>
                        <option value="health">Salud</option>
                        <option value="education">Educación</option>
                        <option value="career">Carrera</option>
                      </select>
                    </div>

                    {/* Difficulty */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Dificultad</label>
                      <select
                        value={filters.difficulty}
                        onChange={(e) => setFilters((prev) => ({ ...prev, difficulty: e.target.value }))}
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      >
                        <option value="all">Todas las dificultades</option>
                        <option value={ChallengeDifficulty.BEGINNER}>Principiante</option>
                        <option value={ChallengeDifficulty.INTERMEDIATE}>Intermedio</option>
                        <option value={ChallengeDifficulty.ADVANCED}>Avanzado</option>
                      </select>
                    </div>

                    {/* Status */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Estado</label>
                      <select
                        value={filters.status}
                        onChange={(e) => setFilters((prev) => ({ ...prev, status: e.target.value }))}
                        className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                      >
                        <option value="all">Todos los estados</option>
                        <option value={ChallengeStatus.ACTIVE}>Activo</option>
                        <option value={ChallengeStatus.COMPLETED}>Completado</option>
                        <option value={ChallengeStatus.DRAFT}>Borrador</option>
                      </select>
                    </div>
                  </div>

                  <div className="mt-4 flex justify-end">
                    <Button
                      variant="outline"
                      onClick={() =>
                        setFilters({
                          category: 'all',
                          difficulty: 'all',
                          status: 'all',
                          searchTerm: '',
                        })
                      }
                    >
                      Limpiar Filtros
                    </Button>
                  </div>
                </motion.div>
              )}
            </CardContent>
          </Card>
        </motion.div>

        {/* Results counter */}
        <motion.div variants={itemVariants} className="mb-6">
          <p className="text-gray-600">
            Mostrando <span className="font-semibold">{filteredChallenges.length}</span> de{' '}
            <span className="font-semibold">{challenges.length}</span> retos
          </p>
        </motion.div>

        {/* Challenges Grid/List */}
        <motion.div variants={itemVariants}>
          {filteredChallenges.length === 0 ? (
            <Card className="text-center py-12">
              <CardContent>
                <Target className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-semibold text-gray-900 mb-2">No se encontraron retos</h3>
                <p className="text-gray-600 mb-6">Intenta ajustar tus filtros o crear un nuevo reto</p>
                <Link to="/challenges/create">
                  <Button variant="primary">
                    <Plus className="w-4 h-4 mr-2" />
                    Crear tu primer reto
                  </Button>
                </Link>
              </CardContent>
            </Card>
          ) : (
            <div className={viewMode === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6' : 'space-y-4'}>
              {filteredChallenges.map((challenge, index) => {
                const percent = Math.round((challenge.participantCount / challenge.maxParticipants) * 100);
                return (
                  <motion.div
                    key={challenge.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: index * 0.1 }}
                  >
                    {viewMode === 'grid' ? (
                      <Card className="h-full hover:shadow-lg transition-all duration-300">
                        <CardContent className="p-6">
                          <div className="flex items-start justify-between mb-4">
                            <div className="flex-1">
                              <h3 className="text-xl font-semibold text-gray-900 mb-2 hover:text-primary-600 transition-colors">
                                {challenge.title}
                              </h3>
                              <p className="text-gray-600 text-sm line-clamp-2">{challenge.description}</p>
                            </div>
                            <Badge variant={getStatusColor(challenge.status)}>{challenge.status}</Badge>
                          </div>

                          <div className="flex items-center gap-4 mb-4 text-sm text-gray-600">
                            <div className="flex items-center gap-1">
                              <Calendar className="w-4 h-4" />
                              <span>7 días</span>
                            </div>
                            <div className="flex items-center gap-1">
                              <Users className="w-4 h-4" />
                              <span>
                                {challenge.participantCount}/{challenge.maxParticipants}
                              </span>
                            </div>
                          </div>

                          <div className="mb-4">
                            <div className="flex items-center justify-between text-sm mb-1">
                              <span className="text-gray-600">Progreso</span>
                              <span className="text-gray-900 font-medium">{percent}%</span>
                            </div>
                            <Progress value={percent} className="h-2" />
                          </div>

                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <Badge variant={getDifficultyColor(challenge.difficulty)} size="sm">
                                {challenge.difficulty}
                              </Badge>
                              <span className="text-sm font-medium text-gray-900">${challenge.rewardAmount}</span>
                            </div>
                            <Link to={`/challenges/${challenge.id}`}>
                              <Button variant="primary" size="sm">Ver Detalle</Button>
                            </Link>
                          </div>
                        </CardContent>
                      </Card>
                    ) : (
                      <Card>
                        <CardContent className="p-6">
                          <div className="flex items-center justify-between">
                            <div className="flex items-start gap-4">
                              <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center flex-shrink-0">
                                <Target className="w-6 h-6 text-primary-600" />
                              </div>
                              <div className="flex-1">
                                <h3 className="text-lg font-semibold text-gray-900 mb-1 hover:text-primary-600 transition-colors">
                                  {challenge.title}
                                </h3>
                                <p className="text-gray-600 mb-3">{challenge.description}</p>
                                <div className="flex items-center gap-2">
                                  <Badge variant="secondary" size="sm">{challenge.category}</Badge>
                                  <Badge variant={getDifficultyColor(challenge.difficulty)} size="sm">
                                    {challenge.difficulty}
                                  </Badge>
                                  <Badge variant={getStatusColor(challenge.status)} size="sm">
                                    {challenge.status}
                                  </Badge>
                                </div>
                              </div>
                            </div>

                            <div className="flex items-center gap-4 ml-4">
                              <div className="text-center">
                                <div className="text-sm font-medium text-gray-900">{percent}%</div>
                                <div className="text-xs text-gray-600">Progreso</div>
                              </div>
                              <div className="text-center">
                                <div className="text-sm font-medium text-gray-900">{challenge.participantCount}</div>
                                <div className="text-xs text-gray-600">Participantes</div>
                              </div>
                              <Link to={`/challenges/${challenge.id}`}>
                                <Button variant="outline" size="sm">Ver detalles</Button>
                              </Link>
                            </div>
                          </div>
                        </CardContent>
                      </Card>
                    )}
                  </motion.div>
                );
              })}
            </div>
          )}
        </motion.div>

        {/* Load more (placeholder) */}
        {filteredChallenges.length > 0 && (
          <motion.div variants={itemVariants} className="text-center mt-12">
            <Button variant="outline" size="lg">Cargar Más Retos</Button>
          </motion.div>
        )}
      </motion.div>
    </div>
  );
};

export default Challenges;
