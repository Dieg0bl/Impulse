import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/Card'
import { Badge } from '../components/ui/Badge'
import { Button } from '../components/ui/Button'
import { Activity, TrendingUp, Users, Award, Target, BarChart3 } from 'lucide-react'

interface DashboardStats {
  activeChallenges: number
  completedChallenges: number
  totalParticipants: number
  streakDays: number
  totalPoints: number
  currentLevel: number
}

interface RecentChallenge {
  id: string
  title: string
  progress: number
  daysRemaining: number
  status: 'ACTIVE' | 'COMPLETED' | 'PENDING'
  participants: number
  category: string
}

interface Achievement {
  id: string
  title: string
  description: string
  earnedAt: string
  icon: string
}

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    activeChallenges: 0,
    completedChallenges: 0,
    totalParticipants: 0,
    streakDays: 0,
    totalPoints: 0,
    currentLevel: 1
  })

  const [recentChallenges, setRecentChallenges] = useState<RecentChallenge[]>([])
  const [recentAchievements, setRecentAchievements] = useState<Achievement[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadDashboardData()
  }, [])

  const loadDashboardData = async () => {
    try {
      setLoading(true)
      
      // Simulamos datos hasta que tengamos la API completa
      const mockStats: DashboardStats = {
        activeChallenges: 3,
        completedChallenges: 12,
        totalParticipants: 156,
        streakDays: 7,
        totalPoints: 2450,
        currentLevel: 8
      }

      const mockChallenges: RecentChallenge[] = [
        {
          id: '1',
          title: '30 Days of Fitness',
          progress: 65,
          daysRemaining: 10,
          status: 'ACTIVE',
          participants: 45,
          category: 'HEALTH'
        },
        {
          id: '2',
          title: 'Daily Reading Challenge',
          progress: 100,
          daysRemaining: 0,
          status: 'COMPLETED',
          participants: 23,
          category: 'EDUCATION'
        },
        {
          id: '3',
          title: 'Meditation Journey',
          progress: 40,
          daysRemaining: 18,
          status: 'ACTIVE',
          participants: 67,
          category: 'WELLNESS'
        }
      ]

      const mockAchievements: Achievement[] = [
        {
          id: '1',
          title: 'First Challenge Completed',
          description: 'Complete your first challenge',
          earnedAt: '2024-01-15',
          icon: '🏆'
        },
        {
          id: '2',
          title: 'Week Warrior',
          description: 'Maintain a 7-day streak',
          earnedAt: '2024-01-20',
          icon: '🔥'
        }
      ]

      setStats(mockStats)
      setRecentChallenges(mockChallenges)
      setRecentAchievements(mockAchievements)
    } catch (error) {
      console.error('Error loading dashboard data:', error)
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800'
      case 'COMPLETED':
        return 'bg-blue-100 text-blue-800'
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800'
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
      default:
        return 'bg-gray-100 text-gray-800'
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
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Dashboard</h1>
        <p className="text-gray-600">Welcome back! Here's your progress overview.</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-6 mb-8">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active Challenges</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.activeChallenges}</div>
            <p className="text-xs text-muted-foreground">
              +2 from last month
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Completed</CardTitle>
            <Target className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.completedChallenges}</div>
            <p className="text-xs text-muted-foreground">
              +3 from last month
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Community</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalParticipants}</div>
            <p className="text-xs text-muted-foreground">
              Total participants
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Streak</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.streakDays}</div>
            <p className="text-xs text-muted-foreground">
              Days in a row
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Points</CardTitle>
            <Award className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalPoints}</div>
            <p className="text-xs text-muted-foreground">
              Total earned
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Level</CardTitle>
            <BarChart3 className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.currentLevel}</div>
            <p className="text-xs text-muted-foreground">
              Progress to level {stats.currentLevel + 1}
            </p>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Recent Challenges */}
        <Card>
          <CardHeader>
            <CardTitle>Recent Challenges</CardTitle>
            <CardDescription>Your latest challenge activities</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentChallenges.map((challenge) => (
                <div key={challenge.id} className="flex items-center justify-between p-4 border rounded-lg">
                  <div className="flex-1">
                    <h4 className="font-medium text-sm">{challenge.title}</h4>
                    <div className="flex items-center gap-2 mt-1">
                      <Badge className={getStatusColor(challenge.status)}>
                        {challenge.status}
                      </Badge>
                      <Badge className={getCategoryColor(challenge.category)}>
                        {challenge.category}
                      </Badge>
                    </div>
                    <div className="flex items-center gap-4 mt-2 text-xs text-gray-500">
                      <span>Progress: {challenge.progress}%</span>
                      <span>{challenge.participants} participants</span>
                      {challenge.daysRemaining > 0 && (
                        <span>{challenge.daysRemaining} days left</span>
                      )}
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2 mt-2">
                      <div 
                        className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                        style={{ width: `${challenge.progress}%` }}
                      ></div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            <div className="mt-4">
              <Button variant="outline" className="w-full">
                View All Challenges
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Recent Achievements */}
        <Card>
          <CardHeader>
            <CardTitle>Recent Achievements</CardTitle>
            <CardDescription>Your latest accomplishments</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentAchievements.map((achievement) => (
                <div key={achievement.id} className="flex items-center p-4 border rounded-lg">
                  <div className="text-2xl mr-3">{achievement.icon}</div>
                  <div className="flex-1">
                    <h4 className="font-medium text-sm">{achievement.title}</h4>
                    <p className="text-xs text-gray-500 mt-1">{achievement.description}</p>
                    <p className="text-xs text-gray-400 mt-1">
                      Earned on {new Date(achievement.earnedAt).toLocaleDateString()}
                    </p>
                  </div>
                </div>
              ))}
              {recentAchievements.length === 0 && (
                <div className="text-center py-8 text-gray-500">
                  <Award className="h-12 w-12 mx-auto mb-2 opacity-50" />
                  <p>No achievements yet. Start a challenge to earn your first badge!</p>
                </div>
              )}
            </div>
            <div className="mt-4">
              <Button variant="outline" className="w-full">
                View All Achievements
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions */}
      <Card className="mt-8">
        <CardHeader>
          <CardTitle>Quick Actions</CardTitle>
          <CardDescription>Get started with these common tasks</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Button className="h-16 flex flex-col items-center justify-center">
              <Target className="h-6 w-6 mb-1" />
              Create Challenge
            </Button>
            <Button variant="outline" className="h-16 flex flex-col items-center justify-center">
              <Users className="h-6 w-6 mb-1" />
              Browse Community
            </Button>
            <Button variant="outline" className="h-16 flex flex-col items-center justify-center">
              <BarChart3 className="h-6 w-6 mb-1" />
              View Progress
            </Button>
            <Button variant="outline" className="h-16 flex flex-col items-center justify-center">
              <Award className="h-6 w-6 mb-1" />
              Validate Evidence
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export default Dashboard
