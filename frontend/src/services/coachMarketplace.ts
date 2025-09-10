// Coach Marketplace Service for IMPULSE LEAN v1 - Phase 10
// Stripe Connect integration for coach monetization

import { apiClient } from './api'

// =============================================================================
// COACH MARKETPLACE TYPES
// =============================================================================

export interface Coach {
  id: string
  userId: string
  businessName: string
  description: string
  specializations: Specialization[]
  experience: string
  certifications: Certification[]
  languages: string[]
  timezone: string
  availability: Availability[]
  pricing: CoachPricing
  rating: number
  reviewCount: number
  totalClients: number
  isActive: boolean
  isVerified: boolean
  stripeAccountId?: string
  onboardingComplete: boolean
  createdAt: string
  updatedAt: string
}

export interface Specialization {
  id: string
  name: string
  category: SpecializationCategory
  level: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT'
}

export type SpecializationCategory =
  | 'FITNESS'
  | 'NUTRITION'
  | 'MENTAL_HEALTH'
  | 'CAREER'
  | 'RELATIONSHIPS'
  | 'FINANCE'
  | 'EDUCATION'
  | 'LIFESTYLE'
  | 'BUSINESS'
  | 'CREATIVITY'

export interface Certification {
  id: string
  name: string
  issuer: string
  issueDate: string
  expirationDate?: string
  credentialId?: string
  verificationUrl?: string
  verified: boolean
}

export interface Availability {
  dayOfWeek: number // 0-6 (Sunday-Saturday)
  startTime: string // HH:mm format
  endTime: string // HH:mm format
  isAvailable: boolean
}

export interface CoachPricing {
  currency: string
  sessionTypes: SessionType[]
  packageDeals: PackageDeal[]
  acceptsPaymentPlans: boolean
}

export interface SessionType {
  id: string
  name: string
  duration: number // minutes
  price: number // in cents
  description: string
  maxParticipants: number
  isGroup: boolean
}

export interface PackageDeal {
  id: string
  name: string
  sessions: number
  price: number // in cents
  discount: number // percentage
  validityDays: number
  description: string
}

// =============================================================================
// BOOKING & PAYMENT TYPES
// =============================================================================

export interface CoachingSession {
  id: string
  coachId: string
  clientId: string
  sessionTypeId: string
  scheduledAt: string
  duration: number
  status: SessionStatus
  price: number
  currency: string
  paymentStatus: PaymentStatus
  paymentIntentId?: string
  meetingLink?: string
  notes?: string
  rating?: number
  review?: string
  createdAt: string
}

export type SessionStatus =
  | 'SCHEDULED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'NO_SHOW'
  | 'RESCHEDULED'

export type PaymentStatus =
  | 'PENDING'
  | 'PROCESSING'
  | 'COMPLETED'
  | 'FAILED'
  | 'REFUNDED'
  | 'DISPUTED'

export interface Booking {
  id: string
  coachId: string
  clientId: string
  sessionTypeId: string
  requestedTimes: string[] // Array of preferred times
  message?: string
  status: BookingStatus
  scheduledAt?: string
  createdAt: string
}

export type BookingStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'DECLINED'
  | 'CANCELLED'

// =============================================================================
// STRIPE CONNECT TYPES
// =============================================================================

export interface StripeAccount {
  id: string
  coachId: string
  stripeAccountId: string
  accountStatus: StripeAccountStatus
  onboardingUrl?: string
  dashboardUrl?: string
  detailsSubmitted: boolean
  chargesEnabled: boolean
  payoutsEnabled: boolean
  requirements: {
    currentDeadline?: string
    currentlyDue: string[]
    eventuallyDue: string[]
    pastDue: string[]
    pendingVerification: string[]
  }
  capabilities: {
    cardPayments: 'active' | 'inactive' | 'pending'
    transfers: 'active' | 'inactive' | 'pending'
  }
  createdAt: string
  updatedAt: string
}

export type StripeAccountStatus =
  | 'INCOMPLETE'
  | 'PENDING'
  | 'ACTIVE'
  | 'RESTRICTED'
  | 'REJECTED'

export interface PaymentIntent {
  id: string
  sessionId: string
  amount: number
  currency: string
  status: string
  clientSecret: string
  stripePaymentIntentId: string
  applicationFeeAmount: number // Platform fee
  transferAmount: number // Amount to coach
  createdAt: string
}

export interface Payout {
  id: string
  coachId: string
  amount: number
  currency: string
  status: string
  stripePayoutId: string
  arrivalDate: string
  createdAt: string
}

// =============================================================================
// COACH MARKETPLACE SERVICE
// =============================================================================

export class CoachMarketplaceService {
  private api: typeof apiClient

  constructor(api: typeof apiClient) {
    this.api = api
  }

  // Coach Management
  async createCoachProfile(profile: Omit<Coach, 'id' | 'createdAt' | 'updatedAt' | 'rating' | 'reviewCount' | 'totalClients'>): Promise<Coach> {
    const response = await this.api.post<{ coach: Coach }>('/marketplace/coaches', profile)
    return response.coach
  }

  async updateCoachProfile(coachId: string, updates: Partial<Coach>): Promise<Coach> {
    const response = await this.api.put<{ coach: Coach }>(`/marketplace/coaches/${coachId}`, updates)
    return response.coach
  }

  async getCoachProfile(coachId: string): Promise<Coach> {
    const response = await this.api.get<{ coach: Coach }>(`/marketplace/coaches/${coachId}`)
    return response.coach
  }

  async searchCoaches(filters: {
    specializations?: string[]
    minRating?: number
    maxPrice?: number
    availability?: {
      dayOfWeek: number
      timeSlot: string
    }
    languages?: string[]
    timezone?: string
    limit?: number
    offset?: number
  }): Promise<{ coaches: Coach[]; total: number }> {
    const queryParams = new URLSearchParams()
    
    if (filters.specializations) {
      filters.specializations.forEach(spec => queryParams.append('specializations', spec))
    }
    if (filters.minRating) queryParams.append('minRating', filters.minRating.toString())
    if (filters.maxPrice) queryParams.append('maxPrice', filters.maxPrice.toString())
    if (filters.languages) {
      filters.languages.forEach(lang => queryParams.append('languages', lang))
    }
    if (filters.timezone) queryParams.append('timezone', filters.timezone)
    if (filters.limit) queryParams.append('limit', filters.limit.toString())
    if (filters.offset) queryParams.append('offset', filters.offset.toString())

    const response = await this.api.get<{ coaches: Coach[]; total: number }>(
      `/marketplace/coaches/search?${queryParams.toString()}`
    )
    return response
  }

  // Stripe Connect Integration
  async createStripeAccount(coachId: string): Promise<StripeAccount> {
    const response = await this.api.post<{ account: StripeAccount }>(`/marketplace/coaches/${coachId}/stripe-account`, {})
    return response.account
  }

  async getStripeAccount(coachId: string): Promise<StripeAccount> {
    const response = await this.api.get<{ account: StripeAccount }>(`/marketplace/coaches/${coachId}/stripe-account`)
    return response.account
  }

  async createOnboardingLink(coachId: string, refreshUrl: string, returnUrl: string): Promise<{ url: string }> {
    const response = await this.api.post<{ url: string }>(`/marketplace/coaches/${coachId}/onboarding-link`, {
      refreshUrl,
      returnUrl
    })
    return response
  }

  async createDashboardLink(coachId: string): Promise<{ url: string }> {
    const response = await this.api.post<{ url: string }>(`/marketplace/coaches/${coachId}/dashboard-link`, {})
    return response
  }

  // Booking Management
  async createBooking(booking: Omit<Booking, 'id' | 'createdAt'>): Promise<Booking> {
    const response = await this.api.post<{ booking: Booking }>('/marketplace/bookings', booking)
    return response.booking
  }

  async confirmBooking(bookingId: string, scheduledAt: string): Promise<Booking> {
    const response = await this.api.post<{ booking: Booking }>(`/marketplace/bookings/${bookingId}/confirm`, {
      scheduledAt
    })
    return response.booking
  }

  async cancelBooking(bookingId: string, reason?: string): Promise<Booking> {
    const response = await this.api.post<{ booking: Booking }>(`/marketplace/bookings/${bookingId}/cancel`, {
      reason
    })
    return response.booking
  }

  async getCoachBookings(coachId: string): Promise<Booking[]> {
    const response = await this.api.get<{ bookings: Booking[] }>(`/marketplace/coaches/${coachId}/bookings`)
    return response.bookings
  }

  async getClientBookings(clientId: string): Promise<Booking[]> {
    const response = await this.api.get<{ bookings: Booking[] }>(`/marketplace/clients/${clientId}/bookings`)
    return response.bookings
  }

  // Session Management
  async createSession(session: Omit<CoachingSession, 'id' | 'createdAt'>): Promise<CoachingSession> {
    const response = await this.api.post<{ session: CoachingSession }>('/marketplace/sessions', session)
    return response.session
  }

  async getSession(sessionId: string): Promise<CoachingSession> {
    const response = await this.api.get<{ session: CoachingSession }>(`/marketplace/sessions/${sessionId}`)
    return response.session
  }

  async updateSessionStatus(sessionId: string, status: SessionStatus): Promise<CoachingSession> {
    const response = await this.api.put<{ session: CoachingSession }>(`/marketplace/sessions/${sessionId}/status`, {
      status
    })
    return response.session
  }

  async addSessionReview(sessionId: string, rating: number, review: string): Promise<CoachingSession> {
    const response = await this.api.post<{ session: CoachingSession }>(`/marketplace/sessions/${sessionId}/review`, {
      rating,
      review
    })
    return response.session
  }

  // Payment Processing
  async createPaymentIntent(sessionId: string): Promise<PaymentIntent> {
    const response = await this.api.post<{ paymentIntent: PaymentIntent }>(`/marketplace/sessions/${sessionId}/payment-intent`, {})
    return response.paymentIntent
  }

  async confirmPayment(paymentIntentId: string): Promise<{ success: boolean; session: CoachingSession }> {
    const response = await this.api.post<{ success: boolean; session: CoachingSession }>(
      `/marketplace/payments/${paymentIntentId}/confirm`, 
      {}
    )
    return response
  }

  async refundPayment(sessionId: string, amount?: number, reason?: string): Promise<{ success: boolean; refundId: string }> {
    const response = await this.api.post<{ success: boolean; refundId: string }>(
      `/marketplace/sessions/${sessionId}/refund`,
      { amount, reason }
    )
    return response
  }

  // Analytics & Reporting
  async getCoachEarnings(coachId: string, period: 'WEEK' | 'MONTH' | 'QUARTER' | 'YEAR'): Promise<{
    totalEarnings: number
    totalSessions: number
    averageRating: number
    payouts: Payout[]
    upcomingSessions: number
  }> {
    const response = await this.api.get<{
      totalEarnings: number
      totalSessions: number
      averageRating: number
      payouts: Payout[]
      upcomingSessions: number
    }>(`/marketplace/coaches/${coachId}/earnings?period=${period}`)
    return response
  }

  async getMarketplaceStats(): Promise<{
    totalCoaches: number
    activeCoaches: number
    totalSessions: number
    totalRevenue: number
    averageSessionRating: number
    topSpecializations: Array<{ name: string; count: number }>
  }> {
    const response = await this.api.get<{
      totalCoaches: number
      activeCoaches: number
      totalSessions: number
      totalRevenue: number
      averageSessionRating: number
      topSpecializations: Array<{ name: string; count: number }>
    }>('/marketplace/stats')
    return response
  }
}

// =============================================================================
// REACT HOOKS
// =============================================================================

export function useCoachMarketplace() {
  const marketplaceService = React.useMemo(() => new CoachMarketplaceService(apiClient), [])

  const searchCoaches = React.useCallback(
    async (filters: Parameters<CoachMarketplaceService['searchCoaches']>[0]) => {
      return marketplaceService.searchCoaches(filters)
    },
    [marketplaceService]
  )

  const bookSession = React.useCallback(
    async (booking: Omit<Booking, 'id' | 'createdAt'>) => {
      return marketplaceService.createBooking(booking)
    },
    [marketplaceService]
  )

  const payForSession = React.useCallback(
    async (sessionId: string) => {
      return marketplaceService.createPaymentIntent(sessionId)
    },
    [marketplaceService]
  )

  return {
    searchCoaches,
    bookSession,
    payForSession,
    marketplaceService
  }
}

export function useCoachDashboard(coachId: string) {
  const marketplaceService = React.useMemo(() => new CoachMarketplaceService(apiClient), [])

  const getEarnings = React.useCallback(
    async (period: 'WEEK' | 'MONTH' | 'QUARTER' | 'YEAR') => {
      return marketplaceService.getCoachEarnings(coachId, period)
    },
    [marketplaceService, coachId]
  )

  const getBookings = React.useCallback(
    async () => {
      return marketplaceService.getCoachBookings(coachId)
    },
    [marketplaceService, coachId]
  )

  const setupStripeAccount = React.useCallback(
    async () => {
      return marketplaceService.createStripeAccount(coachId)
    },
    [marketplaceService, coachId]
  )

  return {
    getEarnings,
    getBookings,
    setupStripeAccount,
    marketplaceService
  }
}

export function useStripeConnect(coachId: string) {
  const marketplaceService = React.useMemo(() => new CoachMarketplaceService(apiClient), [])

  const createAccount = React.useCallback(
    async () => {
      return marketplaceService.createStripeAccount(coachId)
    },
    [marketplaceService, coachId]
  )

  const getOnboardingLink = React.useCallback(
    async (refreshUrl: string, returnUrl: string) => {
      return marketplaceService.createOnboardingLink(coachId, refreshUrl, returnUrl)
    },
    [marketplaceService, coachId]
  )

  const getDashboardLink = React.useCallback(
    async () => {
      return marketplaceService.createDashboardLink(coachId)
    },
    [marketplaceService, coachId]
  )

  return {
    createAccount,
    getOnboardingLink,
    getDashboardLink,
    marketplaceService
  }
}

// Export singleton instance
export const coachMarketplaceService = new CoachMarketplaceService(apiClient)
