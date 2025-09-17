// src/pages/Challenges.tsx
import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Plus,
  Search,
  LayoutGrid,
  List as ListIcon,
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
import PageHeader from '../components/PageHeader';
import DataState from '../components/DataState';

interface FilterState {
  category: string;
  difficulty: string;
  status: string;
  searchTerm: string;
}

const Challenges: React.FC = () => {
  const [challenges, setChallenges] = useState<Challenge[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
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
        setError(null);
        const challengesData = await mockStore.listChallenges();
        setChallenges(challengesData);
      } catch (e) {
        console.error('Error loading challenges:', e);
        setError('No se pudieron cargar los retos. Intenta nuevamente.');
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

  return (
    <div className="bg-gradient-to-br from-gray-50 via-white to-blue-50 min-h-screen">
      <div className="container-app pt-10 pb-24 space-y-8 max-w-7xl mx-auto">
        <PageHeader
          title={
            <span className="font-extrabold text-primary-700 tracking-tight">
              Retos de Impulse
            </span>
          }
          subtitle="Descubre desafíos únicos para impulsar tu crecimiento personal y profesional. Conecta, compite y alcanza nuevas metas."
          actions={
            <div className="flex items-center gap-3">
              <button className="hidden sm:inline-flex items-center gap-2 px-4 py-2 bg-white/80 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-white hover:shadow-md transition-all duration-200">
                <Users className="w-4 h-4" />
                Mis Retos
              </button>
              <Link to="/challenges/create">
                <button className="px-6 py-3 bg-primary-600 text-white rounded-xl font-semibold hover:bg-primary-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 inline-flex items-center gap-2">
                  <Plus className="w-5 h-5" />
                  Crear Reto
                </button>
              </Link>
            </div>
          }
        />

        {/* Data wrapper */}
        <DataState
          loading={loading}
          error={error}
          onRetry={() => {
            setLoading(true);
            mockStore
              .listChallenges()
              .then(setChallenges)
              .catch(() => setError('No se pudieron cargar los retos.'))
              .finally(() => setLoading(false));
          }}
          empty={challenges.length === 0}
          emptyTitle={
            <span className="text-xl font-bold text-gray-900">No hay retos todavía</span>
          }
          emptyDescription={
            <span className="text-gray-600">Crea tu primer reto para comenzar a impulsar a la comunidad</span>
          }
        >
          {challenges.length === 0 && !loading && !error && (
            <Link to="/challenges/create">
              <button className="mt-4 px-6 py-3 bg-primary-600 text-white rounded-xl font-semibold hover:bg-primary-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 inline-flex items-center gap-2">
                <Plus className="w-5 h-5" />
                Crear reto
              </button>
            </Link>
          )}

        {/* Stats */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
          className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-6 mb-6 md:mb-8"
        >
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200 text-center p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="w-12 h-12 bg-gradient-to-br from-primary-100 to-primary-200 rounded-xl flex items-center justify-center mx-auto mb-4">
              <Target className="w-6 h-6 text-primary-600" />
            </div>
            <div className="text-2xl font-bold text-gray-900">{challenges.length}</div>
            <div className="text-sm font-medium text-gray-600">Total Retos</div>
          </div>

          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200 text-center p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="w-12 h-12 bg-gradient-to-br from-green-100 to-green-200 rounded-xl flex items-center justify-center mx-auto mb-4">
              <Award className="w-6 h-6 text-green-600" />
            </div>
            <div className="text-2xl font-bold text-gray-900">
              {challenges.filter((c) => c.status === ChallengeStatus.COMPLETED).length}
            </div>
            <div className="text-sm font-medium text-gray-600">Completados</div>
          </div>

          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200 text-center p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="w-12 h-12 bg-gradient-to-br from-yellow-100 to-yellow-200 rounded-xl flex items-center justify-center mx-auto mb-4">
              <Clock className="w-6 h-6 text-yellow-600" />
            </div>
            <div className="text-2xl font-bold text-gray-900">
              {challenges.filter((c) => c.status === ChallengeStatus.ACTIVE).length}
            </div>
            <div className="text-sm font-medium text-gray-600">En Progreso</div>
          </div>

          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200 text-center p-6 hover:shadow-xl transition-all duration-200 transform hover:-translate-y-1">
            <div className="w-12 h-12 bg-gradient-to-br from-blue-100 to-blue-200 rounded-xl flex items-center justify-center mx-auto mb-4">
              <TrendingUp className="w-6 h-6 text-blue-600" />
            </div>
            <div className="text-2xl font-bold text-gray-900">85%</div>
            <div className="text-sm font-medium text-gray-600">Tasa Éxito</div>
          </div>
        </motion.div>

        {/* Search & Filters */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="mb-6"
        >
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
            <div className="p-4 md:p-6">
              <div className="flex flex-col lg:flex-row gap-4">
                {/* Search */}
                <div className="flex-1">
                  <div className="relative">
                    <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input
                      type="text"
                      placeholder="Buscar retos por título o descripción..."
                      value={filters.searchTerm}
                      onChange={(e) => setFilters((prev) => ({ ...prev, searchTerm: e.target.value }))}
                      className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all text-sm font-medium"
                      aria-label="Buscar retos"
                    />
                  </div>
                </div>

                {/* View Mode */}
                <div className="flex items-center bg-gray-100 rounded-xl overflow-hidden self-start">
                  <fieldset className="flex" aria-label="Modo de vista">
                    <legend className="sr-only">Seleccionar modo de vista</legend>
                    <button
                      onClick={() => setViewMode('grid')}
                      className={`p-3 transition-all duration-200 ${viewMode === 'grid' ? 'bg-primary-500 text-white shadow-md' : 'bg-transparent text-gray-600 hover:bg-gray-200'}`}
                      aria-pressed={viewMode === 'grid'}
                      aria-label="Vista de cuadrícula"
                    >
                      <LayoutGrid className="w-5 h-5" />
                    </button>
                    <button
                      onClick={() => setViewMode('list')}
                      className={`p-3 transition-all duration-200 ${viewMode === 'list' ? 'bg-primary-500 text-white shadow-md' : 'bg-transparent text-gray-600 hover:bg-gray-200'}`}
                      aria-pressed={viewMode === 'list'}
                      aria-label="Vista de lista"
                    >
                      <ListIcon className="w-5 h-5" />
                    </button>
                  </fieldset>
                </div>

                {/* Filters toggle */}
                <button
                  onClick={() => setShowFilters((v) => !v)}
                  className="flex items-center gap-2 px-4 py-3 bg-white border border-gray-300 text-gray-700 rounded-xl font-medium hover:bg-gray-50 hover:border-gray-400 transition-all duration-200 self-start"
                  aria-expanded={showFilters}
                  aria-controls="panel-filtros"
                >
                  <SlidersHorizontal className="w-5 h-5" />
                  Filtros
                  <ChevronDown className={`w-4 h-4 transition-transform ${showFilters ? 'rotate-180' : ''}`} />
                </button>
              </div>

              {/* Filters panel */}
              {showFilters && (
                <motion.div
                  id="panel-filtros"
                  initial={{ height: 0, opacity: 0 }}
                  animate={{ height: 'auto', opacity: 1 }}
                  className="mt-6 pt-6 border-t border-gray-200"
                >
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {/* Category */}
                    <div>
                      <label htmlFor="category-filter" className="block text-sm font-semibold text-gray-700 mb-2">Categoría</label>
                      <select
                        id="category-filter"
                        value={filters.category}
                        onChange={(e) => setFilters((prev) => ({ ...prev, category: e.target.value }))}
                        className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent text-sm font-medium bg-white"
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
                      <label htmlFor="difficulty-filter" className="block text-sm font-semibold text-gray-700 mb-2">Dificultad</label>
                      <select
                        id="difficulty-filter"
                        value={filters.difficulty}
                        onChange={(e) => setFilters((prev) => ({ ...prev, difficulty: e.target.value }))}
                        className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent text-sm font-medium bg-white"
                      >
                        <option value="all">Todas las dificultades</option>
                        <option value={ChallengeDifficulty.BEGINNER}>Principiante</option>
                        <option value={ChallengeDifficulty.INTERMEDIATE}>Intermedio</option>
                        <option value={ChallengeDifficulty.ADVANCED}>Avanzado</option>
                      </select>
                    </div>

                    {/* Status */}
                    <div>
                      <label htmlFor="status-filter" className="block text-sm font-semibold text-gray-700 mb-2">Estado</label>
                      <select
                        id="status-filter"
                        value={filters.status}
                        onChange={(e) => setFilters((prev) => ({ ...prev, status: e.target.value }))}
                        className="w-full p-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-transparent text-sm font-medium bg-white"
                      >
                        <option value="all">Todos los estados</option>
                        <option value={ChallengeStatus.ACTIVE}>Activo</option>
                        <option value={ChallengeStatus.COMPLETED}>Completado</option>
                        <option value={ChallengeStatus.DRAFT}>Borrador</option>
                      </select>
                    </div>
                  </div>

                  <div className="mt-6 flex justify-end">
                    <button
                      onClick={() =>
                        setFilters({
                          category: 'all',
                          difficulty: 'all',
                          status: 'all',
                          searchTerm: '',
                        })
                      }
                      className="px-6 py-2 bg-white border border-gray-300 text-gray-700 rounded-xl font-medium hover:bg-gray-50 hover:border-gray-400 transition-all duration-200"
                    >
                      Limpiar Filtros
                    </button>
                  </div>
                </motion.div>
              )}
            </div>
          </div>
        </motion.div>

        {/* Results counter */}
        <motion.div variants={itemVariants} className="mb-4 md:mb-6">
          <p className="text-gray-600 text-sm md:text-base">
            Mostrando <span className="font-semibold">{filteredChallenges.length}</span> de{' '}
            <span className="font-semibold">{challenges.length}</span> retos
          </p>
        </motion.div>

        {/* Challenges Grid/List */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.4 }}
        >
          {filteredChallenges.length > 0 && (
            <div className={viewMode === 'grid' ? 'grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4 md:gap-6' : 'space-y-4'}>
              {filteredChallenges.map((challenge, index) => {
                const max = challenge.maxParticipants || 1;
                const percent = Math.round((challenge.participantCount / max) * 100);
                return (
                  <motion.div
                    key={challenge.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: index * 0.1 }}
                  >
                    {viewMode === 'grid' ? (
                      <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200 h-full hover:shadow-xl transition-all duration-300 group">
                        <div className="p-6">
                          <div className="flex items-start justify-between mb-4">
                            <div className="flex-1">
                              <h3 className="text-xl font-bold text-gray-900 mb-2 group-hover:text-primary-600 transition-colors">
                                {challenge.title}
                              </h3>
                              <p className="text-gray-600 text-sm line-clamp-2">{challenge.description}</p>
                            </div>
                            <span className={`px-3 py-1 text-xs font-semibold rounded-full ${getStatusColor(challenge.status)}`}>
                              {challenge.status}
                            </span>
                          </div>

                          <div className="flex items-center gap-4 mb-4 text-sm text-gray-600">
                            <div className="flex items-center gap-1">
                              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                              </svg>
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
                              <span className="text-gray-900 font-semibold">{percent}%</span>
                            </div>
                            <div className="w-full bg-gray-200 rounded-full h-2">
                              <div
                                className="h-2 bg-gradient-to-r from-primary-500 to-primary-600 rounded-full transition-all duration-300"
                                style={{ width: `${percent}%` }}
                              />
                            </div>
                          </div>

                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getDifficultyColor(challenge.difficulty)}`}>
                                {challenge.difficulty}
                              </span>
                              <span className="text-sm font-bold text-gray-900">${challenge.rewardAmount}</span>
                            </div>
                            <Link to={`/challenges/${challenge.id}`}>
                              <button className="px-4 py-2 bg-primary-500 text-white text-sm font-semibold rounded-xl hover:bg-primary-600 transition-all duration-200 hover:shadow-md">
                                Ver Detalle
                              </button>
                            </Link>
                          </div>
                        </div>
                      </div>
                    ) : (
                      <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
                        <div className="p-6">
                          <div className="flex items-center justify-between">
                            <div className="flex items-start gap-4">
                              <div className="w-12 h-12 bg-gradient-to-br from-primary-100 to-primary-200 rounded-xl flex items-center justify-center flex-shrink-0">
                                <Target className="w-6 h-6 text-primary-600" />
                              </div>
                              <div className="flex-1">
                                <h3 className="text-lg font-bold text-gray-900 mb-1 hover:text-primary-600 transition-colors">
                                  {challenge.title}
                                </h3>
                                <p className="text-gray-600 mb-3">{challenge.description}</p>
                                <div className="flex items-center gap-2">
                                  <span className="px-2 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-700">
                                    {challenge.category}
                                  </span>
                                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getDifficultyColor(challenge.difficulty)}`}>
                                    {challenge.difficulty}
                                  </span>
                                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(challenge.status)}`}>
                                    {challenge.status}
                                  </span>
                                </div>
                              </div>
                            </div>

                            <div className="flex items-center gap-4 ml-4">
                              <div className="text-center">
                                <div className="text-sm font-bold text-gray-900">{percent}%</div>
                                <div className="text-xs text-gray-600">Progreso</div>
                              </div>
                              <div className="text-center">
                                <div className="text-sm font-bold text-gray-900">{challenge.participantCount}</div>
                                <div className="text-xs text-gray-600">Participantes</div>
                              </div>
                              <Link to={`/challenges/${challenge.id}`}>
                                <button className="px-4 py-2 bg-white border border-gray-300 text-gray-700 text-sm font-semibold rounded-xl hover:bg-gray-50 hover:border-gray-400 transition-all duration-200">
                                  Ver detalles
                                </button>
                              </Link>
                            </div>
                          </div>
                        </div>
                      </div>
                    )}
                  </motion.div>
                );
              })}
            </div>
          )}
        </motion.div>

        {/* Load more (placeholder) */}
        {filteredChallenges.length > 0 && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.6 }}
            className="text-center mt-10 md:mt-12"
          >
            <button className="px-6 py-3 bg-white border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 hover:border-gray-400 transition-all duration-200">
              Cargar Más Retos
            </button>
          </motion.div>
        )}
        </DataState>
      </div>
    </div>
  );
};

export default Challenges;
