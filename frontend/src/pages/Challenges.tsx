import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/Card'
import { Badge } from '../components/ui/Badge'
import { Button } from '../components/ui/Button'
import { 
  Search, 
  Filter, 
  Plus, 
  Target, 
  Users, 
  Calendar, 
  Trophy,
  Clock,
  Star,
  Heart,
  Share2,
  MoreVertical
} from 'lucide-react'

interface Challenge {
  id: string
  uuid: string
  title: string
  description: string
  category: string
  difficulty: 'LOW' | 'MEDIUM' | 'HIGH' | 'EXTREME'
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  durationDays: number
  maxParticipants: number
  participantCount: number
  completedCount: number
  rewardPoints: number
  featured: boolean
  evidenceRequired: boolean
  validationMethod: 'NONE' | 'SELF' | 'PEER' | 'EXPERT' | 'MIXED'
  startDate: string
  endDate: string
  createdAt: string
  creatorName: string
  tags: string[]
  isUserParticipating: boolean
  canJoin: boolean
}

interface ChallengeFilters {
  category: string
  difficulty: string
  status: string
  searchTerm: string
}

const Challenges: React.FC = () => {
  const [challenges, setChallenges] = useState<Challenge[]>([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState<ChallengeFilters>({
    category: '',
    difficulty: '',
    status: '',
    searchTerm: ''
  })
  const [showCreateModal, setShowCreateModal] = useState(false)

  useEffect(() => {
    loadChallenges()
  }, [filters])

  const loadChallenges = async () => {
    try {
      setLoading(true)
      
      // Mock data hasta que tengamos la API completa
      const mockChallenges: Challenge[] = [
        {
          id: '1',
          uuid: 'challenge-1',
          title: '30 Days of Fitness',
          description: 'Complete a daily fitness routine for 30 consecutive days. Track your progress and build healthy habits.',
          category: 'HEALTH',
          difficulty: 'MEDIUM',
          status: 'PUBLISHED',
          durationDays: 30,
          maxParticipants: 100,
          participantCount: 45,
          completedCount: 12,
          rewardPoints: 300,
          featured: true,
          evidenceRequired: true,
          validationMethod: 'PEER',
          startDate: '2024-01-01T00:00:00Z',
          endDate: '2024-01-31T23:59:59Z',
          createdAt: '2023-12-15T10:00:00Z',
          creatorName: 'FitnessGuru',
          tags: ['fitness', 'health', 'daily', 'routine'],
          isUserParticipating: true,
          canJoin: true
        },
        {
          id: '2',
          uuid: 'challenge-2',
          title: 'Daily Reading Challenge',
          description: 'Read for at least 30 minutes every day for 21 days. Share your favorite quotes and insights.',
          category: 'EDUCATION',
          difficulty: 'LOW',
          status: 'PUBLISHED',
          durationDays: 21,
          maxParticipants: 50,
          participantCount: 23,
          completedCount: 18,
          rewardPoints: 200,
          featured: false,
          evidenceRequired: true,
          validationMethod: 'SELF',
          startDate: '2024-01-15T00:00:00Z',
          endDate: '2024-02-05T23:59:59Z',
          createdAt: '2024-01-01T10:00:00Z',
          creatorName: 'BookLover',
          tags: ['reading', 'education', 'self-improvement'],
          isUserParticipating: false,
          canJoin: true
        },
        {
          id: '3',
          uuid: 'challenge-3',
          title: 'Meditation Journey',
          description: 'Practice mindfulness meditation for 10 minutes daily. Learn different techniques and track your mental wellness.',
          category: 'WELLNESS',
          difficulty: 'LOW',
          status: 'PUBLISHED',
          durationDays: 14,
          maxParticipants: 75,
          participantCount: 67,
          completedCount: 34,
          rewardPoints: 150,
          featured: true,
          evidenceRequired: true,
          validationMethod: 'MIXED',
          startDate: '2024-01-20T00:00:00Z',
          endDate: '2024-02-03T23:59:59Z',
          createdAt: '2024-01-10T10:00:00Z',
          creatorName: 'ZenMaster',
          tags: ['meditation', 'mindfulness', 'wellness'],
          isUserParticipating: true,
          canJoin: false
        }
      ]

      // Aplicar filtros
      let filteredChallenges = mockChallenges

      if (filters.searchTerm) {
        filteredChallenges = filteredChallenges.filter(challenge =>
          challenge.title.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
          challenge.description.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
          challenge.tags.some(tag => tag.toLowerCase().includes(filters.searchTerm.toLowerCase()))
        )
      }

      if (filters.category) {
        filteredChallenges = filteredChallenges.filter(challenge =>
          challenge.category === filters.category
        )
      }

      if (filters.difficulty) {
        filteredChallenges = filteredChallenges.filter(challenge =>
          challenge.difficulty === filters.difficulty
        )
      }

      if (filters.status) {
        filteredChallenges = filteredChallenges.filter(challenge =>
          challenge.status === filters.status
        )
      }

      setChallenges(filteredChallenges)
    } catch (error) {
      console.error('Error loading challenges:', error)
    } finally {
      setLoading(false)
    }
  }

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case 'LOW':
        return 'bg-green-100 text-green-800'
      case 'MEDIUM':
        return 'bg-yellow-100 text-yellow-800'
      case 'HIGH':
        return 'bg-orange-100 text-orange-800'
      case 'EXTREME':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const getCategoryColor = (category: string) => {
    switch (category) {
      case 'HEALTH':
        return 'bg-red-100 text-red-800'
      case 'EDUCATION':
        return 'bg-purple-100 text-purple-800'
      case 'WELLNESS':
        return 'bg-green-100 text-green-800'
      case 'SPORTS':
        return 'bg-blue-100 text-blue-800'
      case 'CREATIVITY':
        return 'bg-pink-100 text-pink-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const handleJoinChallenge = async (challengeUuid: string) => {
    try {
      // TODO: Implementar API call
      console.log('Joining challenge:', challengeUuid)
      // Actualizar el estado local
      setChallenges(prev => prev.map(challenge =>
        challenge.uuid === challengeUuid
          ? { ...challenge, isUserParticipating: true, participantCount: challenge.participantCount + 1 }
          : challenge
      ))
    } catch (error) {
      console.error('Error joining challenge:', error)
    }
  }

  const handleLeaveChallenge = async (challengeUuid: string) => {
    try {
      // TODO: Implementar API call
      console.log('Leaving challenge:', challengeUuid)
      // Actualizar el estado local
      setChallenges(prev => prev.map(challenge =>
        challenge.uuid === challengeUuid
          ? { ...challenge, isUserParticipating: false, participantCount: challenge.participantCount - 1 }
          : challenge
      ))
    } catch (error) {
      console.error('Error leaving challenge:', error)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">Challenges</h1>
            <p className="text-gray-600">Discover and join challenges to achieve your goals</p>
          </div>
          <Button onClick={() => setShowCreateModal(true)} className="flex items-center gap-2">
            <Plus className="h-4 w-4" />
            Create Challenge
          </Button>
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4 items-center">
          <div className="relative flex-1 min-w-[300px]">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <input
              type="text"
              placeholder="Search challenges..."
              value={filters.searchTerm}
              onChange={(e) => setFilters(prev => ({ ...prev, searchTerm: e.target.value }))}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <select
            value={filters.category}
            onChange={(e) => setFilters(prev => ({ ...prev, category: e.target.value }))}
            className="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Categories</option>
            <option value="HEALTH">Health</option>
            <option value="EDUCATION">Education</option>
            <option value="WELLNESS">Wellness</option>
            <option value="SPORTS">Sports</option>
            <option value="CREATIVITY">Creativity</option>
          </select>

          <select
            value={filters.difficulty}
            onChange={(e) => setFilters(prev => ({ ...prev, difficulty: e.target.value }))}
            className="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Difficulties</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="EXTREME">Extreme</option>
          </select>

          <Button variant="outline" className="flex items-center gap-2">
            <Filter className="h-4 w-4" />
            More Filters
          </Button>
        </div>
      </div>

      {/* Featured Challenges */}
      <div className="mb-8">
        <h2 className="text-2xl font-semibold mb-4">Featured Challenges</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {challenges.filter(c => c.featured).map((challenge) => (
            <Card key={challenge.id} className="relative overflow-hidden">
              <div className="absolute top-2 right-2">
                <Star className="h-5 w-5 text-yellow-500 fill-current" />
              </div>
              
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <CardTitle className="text-lg mb-2">{challenge.title}</CardTitle>
                    <CardDescription className="text-sm">
                      {challenge.description}
                    </CardDescription>
                  </div>
                </div>

                <div className="flex flex-wrap gap-2 mt-3">
                  <Badge className={getCategoryColor(challenge.category)}>
                    {challenge.category}
                  </Badge>
                  <Badge className={getDifficultyColor(challenge.difficulty)}>
                    {challenge.difficulty}
                  </Badge>
                  <Badge variant="outline" className="flex items-center gap-1">
                    <Trophy className="h-3 w-3" />
                    {challenge.rewardPoints} pts
                  </Badge>
                </div>
              </CardHeader>

              <CardContent>
                <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                  <div className="flex items-center gap-1">
                    <Users className="h-4 w-4" />
                    {challenge.participantCount}/{challenge.maxParticipants}
                  </div>
                  <div className="flex items-center gap-1">
                    <Calendar className="h-4 w-4" />
                    {challenge.durationDays} days
                  </div>
                  <div className="flex items-center gap-1">
                    <Clock className="h-4 w-4" />
                    {challenge.completedCount} completed
                  </div>
                </div>

                <div className="flex flex-wrap gap-1 mb-4">
                  {challenge.tags.map((tag, index) => (
                    <span key={index} className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded">
                      #{tag}
                    </span>
                  ))}
                </div>

                <div className="flex items-center justify-between">
                  {challenge.isUserParticipating ? (
                    <div className="flex gap-2">
                      <Button size="sm" variant="outline">
                        View Progress
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => handleLeaveChallenge(challenge.uuid)}>
                        Leave
                      </Button>
                    </div>
                  ) : (
                    <Button 
                      size="sm" 
                      disabled={!challenge.canJoin}
                      onClick={() => handleJoinChallenge(challenge.uuid)}
                    >
                      {challenge.canJoin ? 'Join Challenge' : 'Full'}
                    </Button>
                  )}
                  
                  <div className="flex gap-1">
                    <Button size="icon" variant="ghost">
                      <Heart className="h-4 w-4" />
                    </Button>
                    <Button size="icon" variant="ghost">
                      <Share2 className="h-4 w-4" />
                    </Button>
                    <Button size="icon" variant="ghost">
                      <MoreVertical className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      {/* All Challenges */}
      <div>
        <h2 className="text-2xl font-semibold mb-4">All Challenges</h2>
        {challenges.length === 0 ? (
          <Card>
            <CardContent className="text-center py-12">
              <Target className="h-12 w-12 mx-auto mb-4 text-gray-400" />
              <h3 className="text-lg font-medium text-gray-900 mb-2">No challenges found</h3>
              <p className="text-gray-500 mb-4">Try adjusting your filters or create a new challenge</p>
              <Button onClick={() => setShowCreateModal(true)}>
                Create Your First Challenge
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {challenges.filter(c => !c.featured).map((challenge) => (
              <Card key={challenge.id} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <CardTitle className="text-lg mb-2">{challenge.title}</CardTitle>
                      <CardDescription className="text-sm">
                        {challenge.description}
                      </CardDescription>
                    </div>
                  </div>

                  <div className="flex flex-wrap gap-2 mt-3">
                    <Badge className={getCategoryColor(challenge.category)}>
                      {challenge.category}
                    </Badge>
                    <Badge className={getDifficultyColor(challenge.difficulty)}>
                      {challenge.difficulty}
                    </Badge>
                    <Badge variant="outline" className="flex items-center gap-1">
                      <Trophy className="h-3 w-3" />
                      {challenge.rewardPoints} pts
                    </Badge>
                  </div>
                </CardHeader>

                <CardContent>
                  <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                    <div className="flex items-center gap-1">
                      <Users className="h-4 w-4" />
                      {challenge.participantCount}/{challenge.maxParticipants}
                    </div>
                    <div className="flex items-center gap-1">
                      <Calendar className="h-4 w-4" />
                      {challenge.durationDays} days
                    </div>
                    <div className="flex items-center gap-1">
                      <Clock className="h-4 w-4" />
                      {challenge.completedCount} completed
                    </div>
                  </div>

                  <div className="flex flex-wrap gap-1 mb-4">
                    {challenge.tags.map((tag, index) => (
                      <span key={index} className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded">
                        #{tag}
                      </span>
                    ))}
                  </div>

                  <div className="flex items-center justify-between">
                    {challenge.isUserParticipating ? (
                      <div className="flex gap-2">
                        <Button size="sm" variant="outline">
                          View Progress
                        </Button>
                        <Button size="sm" variant="destructive" onClick={() => handleLeaveChallenge(challenge.uuid)}>
                          Leave
                        </Button>
                      </div>
                    ) : (
                      <Button 
                        size="sm" 
                        disabled={!challenge.canJoin}
                        onClick={() => handleJoinChallenge(challenge.uuid)}
                      >
                        {challenge.canJoin ? 'Join Challenge' : 'Full'}
                      </Button>
                    )}
                    
                    <div className="flex gap-1">
                      <Button size="icon" variant="ghost">
                        <Heart className="h-4 w-4" />
                      </Button>
                      <Button size="icon" variant="ghost">
                        <Share2 className="h-4 w-4" />
                      </Button>
                      <Button size="icon" variant="ghost">
                        <MoreVertical className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* Stats */}
      <div className="mt-8 grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-blue-600">{challenges.length}</div>
            <div className="text-sm text-gray-500">Total Challenges</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-green-600">
              {challenges.reduce((sum, c) => sum + c.participantCount, 0)}
            </div>
            <div className="text-sm text-gray-500">Total Participants</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-purple-600">
              {challenges.reduce((sum, c) => sum + c.completedCount, 0)}
            </div>
            <div className="text-sm text-gray-500">Completions</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-orange-600">
              {challenges.reduce((sum, c) => sum + c.rewardPoints, 0)}
            </div>
            <div className="text-sm text-gray-500">Total Points Available</div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default Challenges
