// Coach Marketplace Dashboard for IMPULSE LEAN v1 - Phase 10
// Complete marketplace interface for coaches and clients

import React, { useState, useEffect } from 'react'
import { 
  DollarSign, 
  Calendar, 
  Star, 
  Users, 
  TrendingUp, 
  Settings,
  ExternalLink,
  Clock,
  CheckCircle,
  AlertCircle,
  CreditCard,
  BarChart3,
  Filter,
  Search
} from 'lucide-react'
import { 
  useCoachMarketplace, 
  useCoachDashboard, 
  useStripeConnect,
  Coach,
  Booking,
  CoachingSession
} from '../services/coachMarketplace'
import { useAuth } from '../services/store'

interface MarketplaceDashboardProps {
  userType: 'coach' | 'client'
}

const MarketplaceDashboard: React.FC<MarketplaceDashboardProps> = ({ userType }) => {
  const { user } = useAuth()
  const [activeTab, setActiveTab] = useState<'overview' | 'sessions' | 'earnings' | 'settings'>('overview')

  if (userType === 'coach') {
    return <CoachDashboard user={user} activeTab={activeTab} onTabChange={setActiveTab} />
  } else {
    return <ClientDashboard user={user} />
  }
}

// =============================================================================
// COACH DASHBOARD
// =============================================================================

interface CoachDashboardProps {
  user: any
  activeTab: string
  onTabChange: (tab: 'overview' | 'sessions' | 'earnings' | 'settings') => void
}

const CoachDashboard: React.FC<CoachDashboardProps> = ({ user, activeTab, onTabChange }) => {
  const coachId = user?.coachProfile?.id || 'demo-coach-id'
  const { getEarnings, getBookings, setupStripeAccount } = useCoachDashboard(coachId)
  const { createAccount, getOnboardingLink, getDashboardLink } = useStripeConnect(coachId)
  
  const [earnings, setEarnings] = useState<any>(null)
  const [bookings, setBookings] = useState<Booking[]>([])
  const [stripeSetupNeeded, setStripeSetupNeeded] = useState(true)
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    loadDashboardData()
  }, [])

  const loadDashboardData = async () => {
    setIsLoading(true)
    try {
      const [earningsData, bookingsData] = await Promise.all([
        getEarnings('MONTH'),
        getBookings()
      ])
      
      setEarnings(earningsData)
      setBookings(bookingsData)
    } catch (error) {
      console.error('Failed to load dashboard data:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleStripeSetup = async () => {
    try {
      const { url } = await getOnboardingLink(
        window.location.href,
        `${window.location.origin}/marketplace/onboarding-complete`
      )
      window.location.href = url
    } catch (error) {
      console.error('Failed to setup Stripe account:', error)
    }
  }

  const openStripeDashboard = async () => {
    try {
      const { url } = await getDashboardLink()
      window.open(url, '_blank')
    } catch (error) {
      console.error('Failed to open Stripe dashboard:', error)
    }
  }

  return (
    <div className="max-w-7xl mx-auto py-8 px-4">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Coach Dashboard</h1>
        <p className="text-gray-600">Manage your coaching business and track your performance.</p>
      </div>

      {/* Stripe Setup Banner */}
      {stripeSetupNeeded && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
          <div className="flex items-center">
            <AlertCircle className="w-5 h-5 text-yellow-400 mr-3" />
            <div className="flex-1">
              <h3 className="text-sm font-medium text-yellow-800">
                Payment Setup Required
              </h3>
              <p className="text-sm text-yellow-700 mt-1">
                Complete your Stripe account setup to start receiving payments from clients.
              </p>
            </div>
            <button
              onClick={handleStripeSetup}
              className="ml-4 px-4 py-2 bg-yellow-600 text-white text-sm font-medium rounded-md hover:bg-yellow-700"
            >
              Setup Payments
            </button>
          </div>
        </div>
      )}

      {/* Tab Navigation */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8">
          {[
            { id: 'overview', label: 'Overview', icon: BarChart3 },
            { id: 'sessions', label: 'Sessions', icon: Calendar },
            { id: 'earnings', label: 'Earnings', icon: DollarSign },
            { id: 'settings', label: 'Settings', icon: Settings }
          ].map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              onClick={() => onTabChange(id as any)}
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
          {/* Stats Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <StatsCard
              title="Monthly Earnings"
              value={earnings ? `$${(earnings.totalEarnings / 100).toFixed(2)}` : '$0.00'}
              icon={DollarSign}
              change="+12.5%"
              changeType="positive"
            />
            <StatsCard
              title="Total Sessions"
              value={earnings?.totalSessions || 0}
              icon={Calendar}
              change="+8.3%"
              changeType="positive"
            />
            <StatsCard
              title="Average Rating"
              value={earnings ? earnings.averageRating.toFixed(1) : '5.0'}
              icon={Star}
              change="+0.2"
              changeType="positive"
            />
            <StatsCard
              title="Upcoming Sessions"
              value={earnings?.upcomingSessions || 0}
              icon={Clock}
              change=""
              changeType="neutral"
            />
          </div>

          {/* Recent Bookings */}
          <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200">
              <h2 className="text-lg font-medium text-gray-900">Recent Bookings</h2>
            </div>
            <div className="divide-y divide-gray-200">
              {bookings.slice(0, 5).map((booking) => (
                <BookingRow key={booking.id} booking={booking} />
              ))}
              {bookings.length === 0 && (
                <div className="px-6 py-8 text-center">
                  <Calendar className="mx-auto h-8 w-8 text-gray-400" />
                  <p className="mt-2 text-sm text-gray-500">No bookings yet.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'earnings' && (
        <EarningsTab earnings={earnings} onOpenStripeDashboard={openStripeDashboard} />
      )}

      {activeTab === 'sessions' && (
        <SessionsTab bookings={bookings} />
      )}

      {activeTab === 'settings' && (
        <SettingsTab />
      )}
    </div>
  )
}

// =============================================================================
// CLIENT DASHBOARD
// =============================================================================

interface ClientDashboardProps {
  user: any
}

const ClientDashboard: React.FC<ClientDashboardProps> = ({ user }) => {
  const { searchCoaches } = useCoachMarketplace()
  const [coaches, setCoaches] = useState<Coach[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [searchFilters, setSearchFilters] = useState({
    specializations: [] as string[],
    minRating: 0,
    maxPrice: 1000,
    languages: [] as string[]
  })

  useEffect(() => {
    loadCoaches()
  }, [])

  const loadCoaches = async () => {
    setIsLoading(true)
    try {
      const { coaches } = await searchCoaches({
        limit: 20,
        offset: 0,
        ...searchFilters
      })
      setCoaches(coaches)
    } catch (error) {
      console.error('Failed to load coaches:', error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="max-w-7xl mx-auto py-8 px-4">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Find Your Coach</h1>
        <p className="text-gray-600">
          Connect with expert coaches to achieve your goals and unlock your potential.
        </p>
      </div>

      {/* Search & Filters */}
      <div className="bg-white shadow rounded-lg p-6 mb-6">
        <div className="flex items-center gap-4 mb-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Search coaches by name or specialization..."
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>
          <button className="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50">
            <Filter className="w-4 h-4 mr-2" />
            Filters
          </button>
        </div>
      </div>

      {/* Coaches Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {coaches.map((coach) => (
          <CoachCard key={coach.id} coach={coach} />
        ))}
      </div>

      {coaches.length === 0 && !isLoading && (
        <div className="text-center py-12">
          <Users className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">No coaches found</h3>
          <p className="mt-1 text-sm text-gray-500">
            Try adjusting your search criteria to find more coaches.
          </p>
        </div>
      )}
    </div>
  )
}

// =============================================================================
// COMPONENTS
// =============================================================================

interface StatsCardProps {
  title: string
  value: string | number
  icon: React.ComponentType<any>
  change: string
  changeType: 'positive' | 'negative' | 'neutral'
}

const StatsCard: React.FC<StatsCardProps> = ({ title, value, icon: Icon, change, changeType }) => {
  const changeColor = {
    positive: 'text-green-600',
    negative: 'text-red-600',
    neutral: 'text-gray-600'
  }[changeType]

  return (
    <div className="bg-white shadow rounded-lg p-6">
      <div className="flex items-center">
        <div className="flex-shrink-0">
          <Icon className="h-8 w-8 text-blue-600" />
        </div>
        <div className="ml-5 w-0 flex-1">
          <dl>
            <dt className="text-sm font-medium text-gray-500 truncate">{title}</dt>
            <dd className="flex items-baseline">
              <div className="text-2xl font-semibold text-gray-900">{value}</div>
              {change && (
                <div className={`ml-2 flex items-baseline text-sm font-semibold ${changeColor}`}>
                  {change}
                </div>
              )}
            </dd>
          </dl>
        </div>
      </div>
    </div>
  )
}

interface BookingRowProps {
  booking: Booking
}

const BookingRow: React.FC<BookingRowProps> = ({ booking }) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800'
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800'
      case 'CANCELLED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div className="px-6 py-4">
      <div className="flex items-center justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-900">
            Booking #{booking.id.slice(-8)}
          </p>
          <p className="text-sm text-gray-500">
            {booking.scheduledAt 
              ? new Date(booking.scheduledAt).toLocaleDateString()
              : 'Time not scheduled'
            }
          </p>
        </div>
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(booking.status)}`}>
          {booking.status.toLowerCase()}
        </span>
      </div>
    </div>
  )
}

interface CoachCardProps {
  coach: Coach
}

const CoachCard: React.FC<CoachCardProps> = ({ coach }) => {
  return (
    <div className="bg-white shadow rounded-lg overflow-hidden">
      <div className="p-6">
        <div className="flex items-center mb-4">
          <div className="w-12 h-12 bg-blue-600 rounded-full flex items-center justify-center">
            <span className="text-white font-medium">
              {coach.businessName[0].toUpperCase()}
            </span>
          </div>
          <div className="ml-4">
            <h3 className="text-lg font-medium text-gray-900">{coach.businessName}</h3>
            <div className="flex items-center">
              <Star className="w-4 h-4 text-yellow-400 fill-current" />
              <span className="ml-1 text-sm text-gray-600">
                {coach.rating.toFixed(1)} ({coach.reviewCount} reviews)
              </span>
            </div>
          </div>
        </div>
        
        <p className="text-sm text-gray-600 mb-4 line-clamp-3">
          {coach.description}
        </p>
        
        <div className="flex flex-wrap gap-2 mb-4">
          {coach.specializations.slice(0, 3).map((spec, index) => (
            <span 
              key={index}
              className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
            >
              {spec.name}
            </span>
          ))}
        </div>
        
        <div className="flex items-center justify-between">
          <div className="text-sm text-gray-600">
            Starting at ${coach.pricing.sessionTypes[0]?.price ? (coach.pricing.sessionTypes[0].price / 100).toFixed(0) : '0'}/session
          </div>
          <button className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700">
            View Profile
          </button>
        </div>
      </div>
    </div>
  )
}

// Placeholder components for other tabs
const EarningsTab: React.FC<{ earnings: any; onOpenStripeDashboard: () => void }> = ({ earnings, onOpenStripeDashboard }) => (
  <div className="space-y-6">
    <div className="flex justify-between items-center">
      <h2 className="text-lg font-medium text-gray-900">Earnings & Payouts</h2>
      <button
        onClick={onOpenStripeDashboard}
        className="flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700"
      >
        <ExternalLink className="w-4 h-4 mr-2" />
        Open Stripe Dashboard
      </button>
    </div>
    
    <div className="bg-white shadow rounded-lg p-6">
      <p className="text-center text-gray-500">Earnings details coming soon...</p>
    </div>
  </div>
)

const SessionsTab: React.FC<{ bookings: Booking[] }> = ({ bookings }) => (
  <div className="space-y-6">
    <h2 className="text-lg font-medium text-gray-900">Session Management</h2>
    <div className="bg-white shadow rounded-lg p-6">
      <p className="text-center text-gray-500">Session management coming soon...</p>
    </div>
  </div>
)

const SettingsTab: React.FC = () => (
  <div className="space-y-6">
    <h2 className="text-lg font-medium text-gray-900">Coach Settings</h2>
    <div className="bg-white shadow rounded-lg p-6">
      <p className="text-center text-gray-500">Settings panel coming soon...</p>
    </div>
  </div>
)

export default MarketplaceDashboard
