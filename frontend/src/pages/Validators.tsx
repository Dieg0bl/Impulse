import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/Card'
import { Badge } from '../components/ui/Badge'
import { Button } from '../components/ui/Button'
import { 
  Check, 
  X, 
  Eye, 
  MessageSquare, 
  Star, 
  Clock,
  AlertTriangle,
  Filter,
  Search,
  Image,
  Video,
  FileText,
  Mic
} from 'lucide-react'

interface Evidence {
  id: string
  uuid: string
  challengeTitle: string
  challengeCategory: string
  participantName: string
  participantAvatar?: string
  dayNumber: number
  evidenceType: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO' | 'MIXED'
  textContent?: string
  mediaUrl?: string
  submittedAt: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'FLAGGED'
  validationsNeeded: number
  validationsReceived: number
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
  tags: string[]
}

interface ValidationFilters {
  status: string
  evidenceType: string
  priority: string
  searchTerm: string
  category: string
}

const Validators: React.FC = () => {
  const [evidenceList, setEvidenceList] = useState<Evidence[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedEvidence, setSelectedEvidence] = useState<Evidence | null>(null)
  const [validationComment, setValidationComment] = useState('')
  const [validationScore, setValidationScore] = useState(5)
  const [filters, setFilters] = useState<ValidationFilters>({
    status: 'PENDING',
    evidenceType: '',
    priority: '',
    searchTerm: '',
    category: ''
  })

  useEffect(() => {
    loadEvidenceForValidation()
  }, [filters])

  const loadEvidenceForValidation = async () => {
    try {
      setLoading(true)
      
      // Mock data hasta que tengamos la API completa
      const mockEvidence: Evidence[] = [
        {
          id: '1',
          uuid: 'evidence-1',
          challengeTitle: '30 Days of Fitness',
          challengeCategory: 'HEALTH',
          participantName: 'John Doe',
          participantAvatar: '/avatars/john.jpg',
          dayNumber: 15,
          evidenceType: 'IMAGE',
          textContent: 'Completed my 45-minute workout today! Feeling stronger every day.',
          mediaUrl: '/evidence/workout-day15.jpg',
          submittedAt: '2024-01-15T14:30:00Z',
          status: 'PENDING',
          validationsNeeded: 3,
          validationsReceived: 1,
          priority: 'MEDIUM',
          tags: ['workout', 'fitness', 'progress']
        },
        {
          id: '2',
          uuid: 'evidence-2',
          challengeTitle: 'Daily Reading Challenge',
          challengeCategory: 'EDUCATION',
          participantName: 'Jane Smith',
          participantAvatar: '/avatars/jane.jpg',
          dayNumber: 8,
          evidenceType: 'TEXT',
          textContent: 'Today I read chapter 3 of "The Power of Habit". Key insight: The habit loop consists of a cue, routine, and reward. I can apply this to building better reading habits by setting a specific time (cue), reading for 30 minutes (routine), and enjoying a cup of tea afterward (reward).',
          submittedAt: '2024-01-15T20:15:00Z',
          status: 'PENDING',
          validationsNeeded: 2,
          validationsReceived: 0,
          priority: 'HIGH',
          tags: ['reading', 'reflection', 'insights']
        },
        {
          id: '3',
          uuid: 'evidence-3',
          challengeTitle: 'Meditation Journey',
          challengeCategory: 'WELLNESS',
          participantName: 'Mike Wilson',
          participantAvatar: '/avatars/mike.jpg',
          dayNumber: 5,
          evidenceType: 'AUDIO',
          textContent: 'Recorded my meditation session today. Focused on breathing techniques.',
          mediaUrl: '/evidence/meditation-day5.mp3',
          submittedAt: '2024-01-15T08:00:00Z',
          status: 'PENDING',
          validationsNeeded: 2,
          validationsReceived: 2,
          priority: 'LOW',
          tags: ['meditation', 'mindfulness', 'breathing']
        },
        {
          id: '4',
          uuid: 'evidence-4',
          challengeTitle: 'Creative Writing',
          challengeCategory: 'CREATIVITY',
          participantName: 'Sarah Johnson',
          participantAvatar: '/avatars/sarah.jpg',
          dayNumber: 12,
          evidenceType: 'VIDEO',
          textContent: 'Sharing my creative writing process and today\'s poem.',
          mediaUrl: '/evidence/writing-day12.mp4',
          submittedAt: '2024-01-15T16:45:00Z',
          status: 'PENDING',
          validationsNeeded: 3,
          validationsReceived: 0,
          priority: 'URGENT',
          tags: ['writing', 'creativity', 'poetry']
        }
      ]

      // Aplicar filtros
      let filteredEvidence = mockEvidence

      if (filters.status) {
        filteredEvidence = filteredEvidence.filter(evidence => evidence.status === filters.status)
      }

      if (filters.evidenceType) {
        filteredEvidence = filteredEvidence.filter(evidence => evidence.evidenceType === filters.evidenceType)
      }

      if (filters.priority) {
        filteredEvidence = filteredEvidence.filter(evidence => evidence.priority === filters.priority)
      }

      if (filters.category) {
        filteredEvidence = filteredEvidence.filter(evidence => evidence.challengeCategory === filters.category)
      }

      if (filters.searchTerm) {
        filteredEvidence = filteredEvidence.filter(evidence =>
          evidence.challengeTitle.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
          evidence.participantName.toLowerCase().includes(filters.searchTerm.toLowerCase()) ||
          (evidence.textContent?.toLowerCase().includes(filters.searchTerm.toLowerCase()) ?? false)
        )
      }

      setEvidenceList(filteredEvidence)
    } catch (error) {
      console.error('Error loading evidence for validation:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleValidateEvidence = async (evidenceUuid: string, decision: 'APPROVE' | 'REJECT', comment: string, score: number) => {
    try {
      // TODO: Implementar API call
      console.log('Validating evidence:', { evidenceUuid, decision, comment, score })
      
      // Actualizar el estado local
      setEvidenceList(prev => prev.filter(evidence => evidence.uuid !== evidenceUuid))
      setSelectedEvidence(null)
      setValidationComment('')
      setValidationScore(5)
    } catch (error) {
      console.error('Error validating evidence:', error)
    }
  }

  const handleFlagEvidence = async (evidenceUuid: string, reason: string) => {
    try {
      // TODO: Implementar API call
      console.log('Flagging evidence:', { evidenceUuid, reason })
      
      // Actualizar el estado local
      setEvidenceList(prev => prev.map(evidence =>
        evidence.uuid === evidenceUuid
          ? { ...evidence, status: 'FLAGGED' as const }
          : evidence
      ))
      setSelectedEvidence(null)
    } catch (error) {
      console.error('Error flagging evidence:', error)
    }
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'LOW':
        return 'bg-green-100 text-green-800'
      case 'MEDIUM':
        return 'bg-yellow-100 text-yellow-800'
      case 'HIGH':
        return 'bg-orange-100 text-orange-800'
      case 'URGENT':
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
      case 'CREATIVITY':
        return 'bg-pink-100 text-pink-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const getEvidenceTypeIcon = (type: string) => {
    switch (type) {
      case 'TEXT':
        return <FileText className="h-4 w-4" />
      case 'IMAGE':
        return <Image className="h-4 w-4" />
      case 'VIDEO':
        return <Video className="h-4 w-4" />
      case 'AUDIO':
        return <Mic className="h-4 w-4" />
      default:
        return <FileText className="h-4 w-4" />
    }
  }

  const formatTimeAgo = (dateString: string) => {
    const now = new Date()
    const submitted = new Date(dateString)
    const diffInHours = Math.floor((now.getTime() - submitted.getTime()) / (1000 * 60 * 60))
    
    if (diffInHours < 1) return 'Just now'
    if (diffInHours < 24) return `${diffInHours}h ago`
    return `${Math.floor(diffInHours / 24)}d ago`
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
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Evidence Validation</h1>
        <p className="text-gray-600">Review and validate evidence submitted by challenge participants</p>
      </div>

      {/* Filters */}
      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Filters</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
              <input
                type="text"
                placeholder="Search evidence..."
                value={filters.searchTerm}
                onChange={(e) => setFilters(prev => ({ ...prev, searchTerm: e.target.value }))}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <select
              value={filters.status}
              onChange={(e) => setFilters(prev => ({ ...prev, status: e.target.value }))}
              className="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Status</option>
              <option value="PENDING">Pending</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
              <option value="FLAGGED">Flagged</option>
            </select>

            <select
              value={filters.evidenceType}
              onChange={(e) => setFilters(prev => ({ ...prev, evidenceType: e.target.value }))}
              className="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Types</option>
              <option value="TEXT">Text</option>
              <option value="IMAGE">Image</option>
              <option value="VIDEO">Video</option>
              <option value="AUDIO">Audio</option>
            </select>

            <select
              value={filters.priority}
              onChange={(e) => setFilters(prev => ({ ...prev, priority: e.target.value }))}
              className="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Priorities</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="URGENT">Urgent</option>
            </select>

            <select
              value={filters.category}
              onChange={(e) => setFilters(prev => ({ ...prev, category: e.target.value }))}
              className="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Categories</option>
              <option value="HEALTH">Health</option>
              <option value="EDUCATION">Education</option>
              <option value="WELLNESS">Wellness</option>
              <option value="CREATIVITY">Creativity</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {/* Evidence List */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div>
          <h2 className="text-xl font-semibold mb-4">Evidence Queue ({evidenceList.length})</h2>
          
          {evidenceList.length === 0 ? (
            <Card>
              <CardContent className="text-center py-12">
                <Eye className="h-12 w-12 mx-auto mb-4 text-gray-400" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">No evidence to validate</h3>
                <p className="text-gray-500">All evidence has been reviewed or try adjusting your filters</p>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-4">
              {evidenceList.map((evidence) => (
                <Card 
                  key={evidence.id} 
                  className={`cursor-pointer transition-all duration-200 hover:shadow-lg ${
                    selectedEvidence?.id === evidence.id ? 'ring-2 ring-blue-500' : ''
                  }`}
                  onClick={() => setSelectedEvidence(evidence)}
                >
                  <CardHeader className="pb-3">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <h3 className="font-medium text-sm">{evidence.challengeTitle}</h3>
                          <span className="text-xs text-gray-500">Day {evidence.dayNumber}</span>
                        </div>
                        <p className="text-xs text-gray-600">by {evidence.participantName}</p>
                      </div>
                      <div className="flex items-center gap-1">
                        {getEvidenceTypeIcon(evidence.evidenceType)}
                        <span className="text-xs">{formatTimeAgo(evidence.submittedAt)}</span>
                      </div>
                    </div>

                    <div className="flex flex-wrap gap-1 mt-2">
                      <Badge className={getCategoryColor(evidence.challengeCategory)}>
                        {evidence.challengeCategory}
                      </Badge>
                      <Badge className={getPriorityColor(evidence.priority)}>
                        {evidence.priority}
                      </Badge>
                      <Badge variant="outline">
                        {evidence.validationsReceived}/{evidence.validationsNeeded} validations
                      </Badge>
                    </div>
                  </CardHeader>

                  <CardContent className="pt-0">
                    {evidence.textContent && (
                      <p className="text-sm text-gray-700 line-clamp-2 mb-2">
                        {evidence.textContent}
                      </p>
                    )}

                    <div className="flex flex-wrap gap-1">
                      {evidence.tags.map((tag) => (
                        <span key={tag} className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded">
                          #{tag}
                        </span>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>

        {/* Evidence Detail & Validation */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Validation Panel</h2>
          
          {selectedEvidence ? (
            <Card>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div>
                    <CardTitle className="text-lg">{selectedEvidence.challengeTitle}</CardTitle>
                    <CardDescription>
                      Day {selectedEvidence.dayNumber} • {selectedEvidence.participantName}
                    </CardDescription>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge className={getCategoryColor(selectedEvidence.challengeCategory)}>
                      {selectedEvidence.challengeCategory}
                    </Badge>
                    <Badge className={getPriorityColor(selectedEvidence.priority)}>
                      {selectedEvidence.priority}
                    </Badge>
                  </div>
                </div>
              </CardHeader>

              <CardContent className="space-y-4">
                {/* Evidence Content */}
                <div>
                  <h4 className="font-medium mb-2">Evidence Content</h4>
                  {selectedEvidence.textContent && (
                    <div className="bg-gray-50 p-3 rounded-md">
                      <p className="text-sm">{selectedEvidence.textContent}</p>
                    </div>
                  )}
                  
                  {selectedEvidence.mediaUrl && (
                    <div className="bg-gray-50 p-3 rounded-md mt-2">
                      <div className="flex items-center gap-2 text-sm text-gray-600">
                        {getEvidenceTypeIcon(selectedEvidence.evidenceType)}
                        <span>Media file: {selectedEvidence.evidenceType.toLowerCase()}</span>
                      </div>
                    </div>
                  )}
                </div>

                {/* Tags */}
                <div>
                  <h4 className="font-medium mb-2">Tags</h4>
                  <div className="flex flex-wrap gap-1">
                    {selectedEvidence.tags.map((tag) => (
                      <span key={tag} className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded">
                        #{tag}
                      </span>
                    ))}
                  </div>
                </div>

                {/* Validation Form */}
                <div className="border-t pt-4">
                  <h4 className="font-medium mb-3">Your Validation</h4>
                  
                  <div className="space-y-3">
                    <div>
                      <label className="block text-sm font-medium mb-1">Score (1-10)</label>
                      <div className="flex items-center gap-2">
                        <input
                          type="range"
                          min="1"
                          max="10"
                          value={validationScore}
                          onChange={(e) => setValidationScore(Number(e.target.value))}
                          className="flex-1"
                        />
                        <span className="w-8 text-center">{validationScore}</span>
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium mb-1">Comment (optional)</label>
                      <textarea
                        value={validationComment}
                        onChange={(e) => setValidationComment(e.target.value)}
                        placeholder="Add your feedback or comments..."
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 resize-none"
                        rows={3}
                      />
                    </div>

                    <div className="flex gap-2">
                      <Button
                        onClick={() => handleValidateEvidence(
                          selectedEvidence.uuid, 
                          'APPROVE', 
                          validationComment, 
                          validationScore
                        )}
                        className="flex-1 bg-green-600 hover:bg-green-700"
                      >
                        <Check className="h-4 w-4 mr-2" />
                        Approve
                      </Button>
                      
                      <Button
                        onClick={() => handleValidateEvidence(
                          selectedEvidence.uuid, 
                          'REJECT', 
                          validationComment, 
                          validationScore
                        )}
                        variant="destructive"
                        className="flex-1"
                      >
                        <X className="h-4 w-4 mr-2" />
                        Reject
                      </Button>
                    </div>

                    <Button
                      onClick={() => handleFlagEvidence(selectedEvidence.uuid, 'Inappropriate content')}
                      variant="outline"
                      className="w-full text-orange-600 border-orange-300 hover:bg-orange-50"
                    >
                      <AlertTriangle className="h-4 w-4 mr-2" />
                      Flag as Inappropriate
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardContent className="text-center py-12">
                <MessageSquare className="h-12 w-12 mx-auto mb-4 text-gray-400" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">Select evidence to validate</h3>
                <p className="text-gray-500">Choose an evidence item from the queue to start validation</p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>

      {/* Stats */}
      <div className="mt-8 grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-orange-600">
              {evidenceList.filter(e => e.status === 'PENDING').length}
            </div>
            <div className="text-sm text-gray-500">Pending Validation</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-red-600">
              {evidenceList.filter(e => e.priority === 'URGENT').length}
            </div>
            <div className="text-sm text-gray-500">Urgent Priority</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-blue-600">
              {evidenceList.reduce((sum, e) => sum + e.validationsReceived, 0)}
            </div>
            <div className="text-sm text-gray-500">Total Validations</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="text-center py-4">
            <div className="text-2xl font-bold text-green-600">
              {evidenceList.reduce((sum, e) => sum + e.validationsNeeded, 0)}
            </div>
            <div className="text-sm text-gray-500">Validations Needed</div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default Validators
