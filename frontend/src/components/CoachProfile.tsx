// Coach Profile Component for IMPULSE LEAN v1 - Phase 10
// Complete coach profile view with booking integration

import React, { useState, useEffect } from 'react'
import { 
  Star, 
  MapPin, 
  Clock, 
  DollarSign, 
  MessageCircle, 
  Video,
  Phone,
  Calendar,
  Award,
  Languages,
  Users,
  BookOpen,
  CheckCircle,
  ExternalLink
} from 'lucide-react'
import { Coach, Review, useCoachMarketplace } from '../services/coachMarketplace'
import BookingForm from './BookingForm'

interface CoachProfileProps {
  coachId: string
  onBack?: () => void
}

const CoachProfile: React.FC<CoachProfileProps> = ({ coachId, onBack }) => {
  const [coach, setCoach] = useState<Coach | null>(null)
  const [reviews, setReviews] = useState<Review[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [showBookingForm, setShowBookingForm] = useState(false)
  const [selectedSessionType, setSelectedSessionType] = useState(null)
  const [activeTab, setActiveTab] = useState<'overview' | 'reviews' | 'credentials'>('overview')

  const { getCoachProfile, getCoachReviews } = useCoachMarketplace()

  useEffect(() => {
    loadCoachData()
  }, [coachId])

  const loadCoachData = async () => {
    setIsLoading(true)
    try {
      const [coachData, reviewsData] = await Promise.all([
        getCoachProfile(coachId),
        getCoachReviews(coachId, { limit: 10, offset: 0 })
      ])
      
      setCoach(coachData)
      setReviews(reviewsData.reviews || [])
    } catch (error) {
      console.error('Failed to load coach data:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleBookingSuccess = (bookingId: string) => {
    setShowBookingForm(false)
    // Could show success message or redirect
    console.log('Booking created:', bookingId)
  }

  const formatPrice = (cents: number) => {
    return `$${(cents / 100).toFixed(2)}`
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (!coach) {
    return (
      <div className="text-center py-12">
        <h2 className="text-xl font-semibold text-gray-900">Coach not found</h2>
        <p className="text-gray-600 mt-2">The coach profile you're looking for doesn't exist.</p>
        {onBack && (
          <button
            onClick={onBack}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Go Back
          </button>
        )}
      </div>
    )
  }

  if (showBookingForm) {
    return (
      <BookingForm
        coach={coach}
        sessionType={selectedSessionType}
        onSuccess={handleBookingSuccess}
        onCancel={() => setShowBookingForm(false)}
      />
    )
  }

  return (
    <div className="max-w-7xl mx-auto py-8 px-4">
      {/* Header */}
      <div className="bg-white shadow rounded-lg overflow-hidden mb-6">
        <div className="relative h-48 bg-gradient-to-r from-blue-600 to-purple-600">
          <div className="absolute inset-0 bg-black bg-opacity-20"></div>
          {onBack && (
            <button
              onClick={onBack}
              className="absolute top-4 left-4 px-3 py-1 bg-white bg-opacity-20 text-white rounded-md hover:bg-opacity-30"
            >
              ← Back
            </button>
          )}
        </div>
        
        <div className="relative px-6 pb-6">
          <div className="flex items-end space-x-5 -mt-12">
            <div className="w-24 h-24 bg-white rounded-full border-4 border-white shadow-lg flex items-center justify-center">
              <span className="text-2xl font-bold text-blue-600">
                {coach.businessName[0].toUpperCase()}
              </span>
            </div>
            <div className="flex-1 min-w-0 pt-1.5">
              <div className="flex items-center space-x-3">
                <h1 className="text-2xl font-bold text-gray-900">{coach.businessName}</h1>
                {coach.isVerified && (
                  <CheckCircle className="w-6 h-6 text-green-500" />
                )}
              </div>
              <div className="flex items-center space-x-4 mt-2">
                <div className="flex items-center">
                  <Star className="w-5 h-5 text-yellow-400 fill-current" />
                  <span className="ml-1 text-sm font-medium text-gray-900">
                    {coach.rating.toFixed(1)}
                  </span>
                  <span className="ml-1 text-sm text-gray-600">
                    ({coach.reviewCount} reviews)
                  </span>
                </div>
                <div className="flex items-center text-sm text-gray-600">
                  <MapPin className="w-4 h-4 mr-1" />
                  {coach.location || 'Remote'}
                </div>
                <div className="flex items-center text-sm text-gray-600">
                  <Languages className="w-4 h-4 mr-1" />
                  {coach.languages.join(', ')}
                </div>
              </div>
            </div>
            <div className="flex space-x-3">
              <button
                onClick={() => setShowBookingForm(true)}
                className="px-6 py-2 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700"
              >
                Book Session
              </button>
              <button className="px-4 py-2 border border-gray-300 text-gray-700 font-medium rounded-md hover:bg-gray-50">
                Message
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Navigation Tabs */}
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8">
              {[
                { id: 'overview', label: 'Overview', icon: Users },
                { id: 'reviews', label: 'Reviews', icon: Star },
                { id: 'credentials', label: 'Credentials', icon: Award }
              ].map(({ id, label, icon: Icon }) => (
                <button
                  key={id}
                  onClick={() => setActiveTab(id as any)}
                  className={`flex items-center py-2 px-1 border-b-2 font-medium text-sm ${
                    activeTab === id
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  <Icon className="w-4 h-4 mr-2" />
                  {label}
                </button>
              ))}
            </nav>
          </div>

          {/* Tab Content */}
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* About */}
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">About</h2>
                <p className="text-gray-700 leading-relaxed">{coach.description}</p>
              </div>

              {/* Specializations */}
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Specializations</h2>
                <div className="flex flex-wrap gap-2">
                  {coach.specializations.map((spec, index) => (
                    <span
                      key={`spec-${index}`}
                      className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                    >
                      {spec.name}
                    </span>
                  ))}
                </div>
              </div>

              {/* Experience */}
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Experience</h2>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div className="text-center p-4 bg-gray-50 rounded-lg">
                    <div className="text-2xl font-bold text-blue-600">
                      {coach.experience?.yearsOfExperience || 0}+
                    </div>
                    <div className="text-sm text-gray-600">Years Experience</div>
                  </div>
                  <div className="text-center p-4 bg-gray-50 rounded-lg">
                    <div className="text-2xl font-bold text-blue-600">
                      {coach.experience?.clientsServed || 0}+
                    </div>
                    <div className="text-sm text-gray-600">Clients Served</div>
                  </div>
                  <div className="text-center p-4 bg-gray-50 rounded-lg">
                    <div className="text-2xl font-bold text-blue-600">
                      {coach.experience?.sessionsCompleted || 0}+
                    </div>
                    <div className="text-sm text-gray-600">Sessions Completed</div>
                  </div>
                </div>
              </div>

              {/* Coaching Approach */}
              {coach.coachingStyle && (
                <div className="bg-white shadow rounded-lg p-6">
                  <h2 className="text-lg font-medium text-gray-900 mb-4">Coaching Approach</h2>
                  <p className="text-gray-700 leading-relaxed">{coach.coachingStyle}</p>
                </div>
              )}
            </div>
          )}

          {activeTab === 'reviews' && (
            <div className="space-y-6">
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Client Reviews</h2>
                <div className="space-y-6">
                  {reviews.map((review) => (
                    <ReviewCard key={review.id} review={review} />
                  ))}
                  {reviews.length === 0 && (
                    <div className="text-center py-8">
                      <Star className="mx-auto h-8 w-8 text-gray-400" />
                      <p className="mt-2 text-sm text-gray-500">No reviews yet.</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'credentials' && (
            <div className="space-y-6">
              <div className="bg-white shadow rounded-lg p-6">
                <h2 className="text-lg font-medium text-gray-900 mb-4">Credentials & Certifications</h2>
                <div className="space-y-4">
                  {coach.credentials?.map((credential, index) => (
                    <div key={`credential-${index}`} className="border border-gray-200 rounded-lg p-4">
                      <div className="flex items-start space-x-3">
                        <Award className="w-5 h-5 text-blue-600 mt-0.5" />
                        <div className="flex-1">
                          <h3 className="font-medium text-gray-900">{credential.title}</h3>
                          <p className="text-sm text-gray-600">{credential.issuer}</p>
                          <p className="text-sm text-gray-500">
                            Issued: {new Date(credential.issueDate).toLocaleDateString()}
                          </p>
                          {credential.credentialUrl && (
                            <a
                              href={credential.credentialUrl}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="inline-flex items-center text-sm text-blue-600 hover:text-blue-800 mt-2"
                            >
                              <ExternalLink className="w-4 h-4 mr-1" />
                              View Credential
                            </a>
                          )}
                        </div>
                      </div>
                    </div>
                  )) || (
                    <div className="text-center py-8">
                      <BookOpen className="mx-auto h-8 w-8 text-gray-400" />
                      <p className="mt-2 text-sm text-gray-500">No credentials listed.</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Session Types */}
          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Session Types</h3>
            <div className="space-y-3">
              {coach.pricing.sessionTypes.map((sessionType) => (
                <div
                  key={sessionType.id}
                  className="border border-gray-200 rounded-lg p-4 hover:border-blue-300 transition-colors"
                >
                  <div className="flex justify-between items-start mb-2">
                    <h4 className="font-medium text-gray-900">{sessionType.name}</h4>
                    <span className="text-lg font-semibold text-blue-600">
                      {formatPrice(sessionType.price)}
                    </span>
                  </div>
                  <p className="text-sm text-gray-600 mb-2">{sessionType.description}</p>
                  <div className="flex items-center text-sm text-gray-500">
                    <Clock className="w-4 h-4 mr-1" />
                    {sessionType.duration} minutes
                  </div>
                  <button
                    onClick={() => {
                      setSelectedSessionType(sessionType)
                      setShowBookingForm(true)
                    }}
                    className="w-full mt-3 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700"
                  >
                    Book This Session
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Contact Options */}
          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Available Communication</h3>
            <div className="space-y-3">
              {coach.communicationPreferences.videoCall && (
                <div className="flex items-center text-sm">
                  <Video className="w-4 h-4 text-green-600 mr-2" />
                  <span>Video Calls</span>
                </div>
              )}
              {coach.communicationPreferences.phoneCall && (
                <div className="flex items-center text-sm">
                  <Phone className="w-4 h-4 text-green-600 mr-2" />
                  <span>Phone Calls</span>
                </div>
              )}
              {coach.communicationPreferences.messaging && (
                <div className="flex items-center text-sm">
                  <MessageCircle className="w-4 h-4 text-green-600 mr-2" />
                  <span>Messaging</span>
                </div>
              )}
              {coach.communicationPreferences.inPerson && (
                <div className="flex items-center text-sm">
                  <Users className="w-4 h-4 text-green-600 mr-2" />
                  <span>In-Person (if local)</span>
                </div>
              )}
            </div>
          </div>

          {/* Availability */}
          <div className="bg-white shadow rounded-lg p-6">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Availability</h3>
            <div className="space-y-2">
              {coach.availability.schedule.map((slot) => (
                <div key={`${slot.dayOfWeek}-${slot.startTime}`} className="flex justify-between text-sm">
                  <span className="text-gray-600">
                    {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][slot.dayOfWeek]}
                  </span>
                  <span className="text-gray-900">
                    {slot.startTime} - {slot.endTime}
                  </span>
                </div>
              ))}
            </div>
            <p className="text-xs text-gray-500 mt-3">
              Times shown in {coach.availability.timezone}
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

// =============================================================================
// REVIEW COMPONENT
// =============================================================================

interface ReviewCardProps {
  review: Review
}

const ReviewCard: React.FC<ReviewCardProps> = ({ review }) => {
  return (
    <div className="border-b border-gray-200 pb-6 last:border-b-0">
      <div className="flex items-start space-x-4">
        <div className="w-10 h-10 bg-gray-300 rounded-full flex items-center justify-center">
          <span className="text-sm font-medium text-gray-700">
            {review.clientName?.[0]?.toUpperCase() || 'A'}
          </span>
        </div>
        <div className="flex-1">
          <div className="flex items-center space-x-2 mb-1">
            <span className="font-medium text-gray-900">
              {review.clientName || 'Anonymous'}
            </span>
            <div className="flex items-center">
              {[...Array(5)].map((_, i) => (
                <Star
                  key={`star-${i}`}
                  className={`w-4 h-4 ${
                    i < review.rating
                      ? 'text-yellow-400 fill-current'
                      : 'text-gray-300'
                  }`}
                />
              ))}
            </div>
            <span className="text-sm text-gray-500">
              {new Date(review.createdAt).toLocaleDateString()}
            </span>
          </div>
          <p className="text-gray-700 text-sm leading-relaxed">
            {review.comment}
          </p>
        </div>
      </div>
    </div>
  )
}

export default CoachProfile
