// API Client completo para IMPULSE LEAN v1
// Maneja todas las llamadas REST con autenticación JWT

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

// Configuración de interceptores para JWT
class ApiClient {
  private baseUrl: string
  private defaultHeaders: Record<string, string>

  constructor(baseUrl: string = API_BASE) {
    this.baseUrl = baseUrl
    this.defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }
  }

  // JWT Token Management
  private getAuthToken(): string | null {
    return localStorage.getItem('auth_token')
  }

  private setAuthToken(token: string): void {
    localStorage.setItem('auth_token', token)
  }

  private removeAuthToken(): void {
    localStorage.removeItem('auth_token')
  }

  // Request interceptor para añadir JWT
  private getHeaders(): Record<string, string> {
    const headers = { ...this.defaultHeaders }
    const token = this.getAuthToken()
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    
    return headers
  }

  // Manejo de errores centralizado
  private async handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
      if (response.status === 401) {
        this.removeAuthToken()
        window.location.href = '/login'
        throw new Error('Unauthorized - redirecting to login')
      }
      
      let errorMessage = `HTTP ${response.status}: ${response.statusText}`
      
      try {
        const errorData = await response.json()
        errorMessage = errorData.message || errorData.error || errorMessage
      } catch {
        // Si no se puede parsear como JSON, usar el status text
      }
      
      throw new Error(errorMessage)
    }

    try {
      return await response.json()
    } catch {
      return {} as T
    }
  }

  // Retry logic para requests fallidos
  private async requestWithRetry<T>(
    url: string, 
    options: RequestInit, 
    retries: number = 3
  ): Promise<T> {
    for (let i = 0; i < retries; i++) {
      try {
        const response = await fetch(url, options)
        return await this.handleResponse<T>(response)
      } catch (error) {
        if (i === retries - 1) throw error
        
        // Exponential backoff
        await new Promise(resolve => setTimeout(resolve, Math.pow(2, i) * 1000))
      }
    }
    throw new Error('Max retries exceeded')
  }

  // HTTP Methods genericos
  async get<T>(endpoint: string): Promise<T> {
    return this.requestWithRetry(`${this.baseUrl}${endpoint}`, {
      method: 'GET',
      headers: this.getHeaders()
    })
  }

  async post<T>(endpoint: string, data?: any): Promise<T> {
    return this.requestWithRetry(`${this.baseUrl}${endpoint}`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: data ? JSON.stringify(data) : undefined
    })
  }

  async put<T>(endpoint: string, data?: any): Promise<T> {
    return this.requestWithRetry(`${this.baseUrl}${endpoint}`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: data ? JSON.stringify(data) : undefined
    })
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.requestWithRetry(`${this.baseUrl}${endpoint}`, {
      method: 'DELETE',
      headers: this.getHeaders()
    })
  }

  async upload<T>(endpoint: string, formData: FormData): Promise<T> {
    const headers = { ...this.getHeaders() }
    delete headers['Content-Type'] // Let browser set multipart boundary
    
    return this.requestWithRetry(`${this.baseUrl}${endpoint}`, {
      method: 'POST',
      headers,
      body: formData
    })
  }
}

// Instancia singleton
const apiClient = new ApiClient()

// =============================================================================
// AUTH API
// =============================================================================

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  user: User
  expiresIn: number
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  privacyConsent: boolean
  marketingConsent?: boolean
}

export interface User {
  id: number
  uuid: string
  username: string
  email: string
  firstName?: string
  lastName?: string
  role: 'GUEST' | 'USER' | 'VALIDATOR' | 'MODERATOR' | 'ADMIN' | 'SUPER_ADMIN'
  status: 'PENDING' | 'ACTIVE' | 'SUSPENDED' | 'BANNED'
  emailVerified: boolean
  avatar?: string
  profileVisibility: 'PUBLIC' | 'PRIVATE' | 'FRIENDS'
  createdAt: string
  lastLoginAt?: string
}

export const authApi = {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>('/api/v1/auth/login', credentials)
    apiClient['setAuthToken'](response.token)
    return response
  },

  async register(userData: RegisterRequest): Promise<User> {
    return apiClient.post<User>('/api/v1/auth/register', userData)
  },

  async logout(): Promise<void> {
    await apiClient.post('/api/v1/auth/logout')
    apiClient['removeAuthToken']()
  },

  async getCurrentUser(): Promise<User> {
    return apiClient.get<User>('/api/v1/auth/me')
  },

  async refreshToken(): Promise<LoginResponse> {
    const response = await apiClient.post<LoginResponse>('/api/v1/auth/refresh')
    apiClient['setAuthToken'](response.token)
    return response
  },

  async verifyEmail(token: string): Promise<void> {
    return apiClient.post(`/api/v1/auth/verify-email`, { token })
  },

  async resetPassword(email: string): Promise<void> {
    return apiClient.post('/api/v1/auth/reset-password', { email })
  },

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    return apiClient.put('/api/v1/auth/change-password', {
      currentPassword,
      newPassword
    })
  }
}

// =============================================================================
// USERS API
// =============================================================================

export interface UserProfile {
  bio?: string
  location?: string
  website?: string
  interests: string[]
  privacySettings: {
    profileVisibility: 'PUBLIC' | 'PRIVATE' | 'FRIENDS'
    showEmail: boolean
    showStats: boolean
    allowMessages: boolean
  }
}

export interface UserStats {
  totalChallenges: number
  completedChallenges: number
  activeChallenges: number
  totalPoints: number
  currentLevel: number
  streakDays: number
  validationsCompleted: number
  evidenceSubmitted: number
}

export const usersApi = {
  async getProfile(uuid: string): Promise<User & UserProfile> {
    return apiClient.get<User & UserProfile>(`/api/v1/users/${uuid}/profile`)
  },

  async updateProfile(profileData: Partial<UserProfile>): Promise<User & UserProfile> {
    return apiClient.put<User & UserProfile>('/api/v1/users/profile', profileData)
  },

  async getStats(uuid: string): Promise<UserStats> {
    return apiClient.get<UserStats>(`/api/v1/users/${uuid}/stats`)
  },

  async uploadAvatar(file: File): Promise<{ avatarUrl: string }> {
    const formData = new FormData()
    formData.append('avatar', file)
    return apiClient.upload('/api/v1/users/avatar', formData)
  },

  async searchUsers(query: string, page = 0, size = 20): Promise<{
    content: User[]
    totalElements: number
    totalPages: number
  }> {
    return apiClient.get(`/api/v1/users/search?q=${encodeURIComponent(query)}&page=${page}&size=${size}`)
  }
}

// =============================================================================
// CHALLENGES API
// =============================================================================

export interface Challenge {
  id: number
  uuid: string
  title: string
  description: string
  category: 'HEALTH' | 'EDUCATION' | 'WELLNESS' | 'SPORTS' | 'CREATIVITY' | 'OTHER'
  difficulty: 'LOW' | 'MEDIUM' | 'HIGH' | 'EXTREME'
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED' | 'DELETED'
  durationDays: number
  maxParticipants?: number
  participantCount: number
  completedCount: number
  rewardPoints: number
  featured: boolean
  evidenceRequired: boolean
  evidenceDescription?: string
  validationMethod: 'NONE' | 'SELF' | 'PEER' | 'EXPERT' | 'MIXED'
  validationCriteria?: string
  startDate?: string
  endDate?: string
  tags: string[]
  rules?: string
  creator: {
    uuid: string
    username: string
    avatar?: string
  }
  isUserParticipating: boolean
  canJoin: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateChallengeRequest {
  title: string
  description: string
  category: Challenge['category']
  difficulty: Challenge['difficulty']
  durationDays: number
  maxParticipants?: number
  rewardPoints: number
  evidenceRequired: boolean
  evidenceDescription?: string
  validationMethod: Challenge['validationMethod']
  validationCriteria?: string
  startDate?: string
  endDate?: string
  tags: string[]
  rules?: string
}

export interface ChallengeFilters {
  category?: string
  difficulty?: string
  status?: string
  featured?: boolean
  searchTerm?: string
  creatorUuid?: string
}

export const challengesApi = {
  async getChallenges(
    filters: ChallengeFilters = {}, 
    page = 0, 
    size = 20
  ): Promise<{
    content: Challenge[]
    totalElements: number
    totalPages: number
  }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...Object.fromEntries(
        Object.entries(filters).filter(([_, value]) => value != null && value !== '')
      )
    })
    
    return apiClient.get(`/api/v1/challenges?${params}`)
  },

  async getChallenge(uuid: string): Promise<Challenge> {
    return apiClient.get<Challenge>(`/api/v1/challenges/${uuid}`)
  },

  async createChallenge(challengeData: CreateChallengeRequest): Promise<Challenge> {
    return apiClient.post<Challenge>('/api/v1/challenges', challengeData)
  },

  async updateChallenge(uuid: string, challengeData: Partial<CreateChallengeRequest>): Promise<Challenge> {
    return apiClient.put<Challenge>(`/api/v1/challenges/${uuid}`, challengeData)
  },

  async deleteChallenge(uuid: string): Promise<void> {
    return apiClient.delete(`/api/v1/challenges/${uuid}`)
  },

  async joinChallenge(uuid: string): Promise<ChallengeParticipation> {
    return apiClient.post<ChallengeParticipation>(`/api/v1/challenges/${uuid}/join`)
  },

  async leaveChallenge(uuid: string): Promise<void> {
    return apiClient.delete(`/api/v1/challenges/${uuid}/leave`)
  },

  async getFeaturedChallenges(): Promise<Challenge[]> {
    return apiClient.get<Challenge[]>('/api/v1/challenges/featured')
  },

  async getRecommendedChallenges(): Promise<Challenge[]> {
    return apiClient.get<Challenge[]>('/api/v1/challenges/recommended')
  },

  async getChallengeStats(): Promise<{
    total: number
    active: number
    completed: number
    totalParticipants: number
  }> {
    return apiClient.get('/api/v1/challenges/stats')
  }
}

// =============================================================================
// EVIDENCE API
// =============================================================================

export interface Evidence {
  id: number
  uuid: string
  challengeUuid: string
  challengeTitle: string
  participantUuid: string
  participantName: string
  dayNumber: number
  evidenceType: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO' | 'MIXED'
  textContent?: string
  mediaUrl?: string
  mediaType?: string
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'FLAGGED'
  submittedAt: string
  validationScore?: number
  validationsNeeded: number
  validationsReceived: number
  validations: EvidenceValidation[]
}

export interface EvidenceValidation {
  id: number
  uuid: string
  validatorUuid: string
  validatorName: string
  decision: 'APPROVE' | 'REJECT'
  score: number
  comment?: string
  validatedAt: string
}

export interface SubmitEvidenceRequest {
  challengeUuid: string
  dayNumber: number
  textContent?: string
  evidenceType: Evidence['evidenceType']
}

export interface ValidateEvidenceRequest {
  evidenceUuid: string
  decision: 'APPROVE' | 'REJECT'
  score: number
  comment?: string
}

export const evidenceApi = {
  async getEvidence(uuid: string): Promise<Evidence> {
    return apiClient.get<Evidence>(`/api/v1/evidence/${uuid}`)
  },

  async submitEvidence(evidenceData: SubmitEvidenceRequest, mediaFile?: File): Promise<Evidence> {
    if (mediaFile) {
      const formData = new FormData()
      formData.append('evidence', JSON.stringify(evidenceData))
      formData.append('media', mediaFile)
      return apiClient.upload<Evidence>('/api/v1/evidence', formData)
    } else {
      return apiClient.post<Evidence>('/api/v1/evidence', evidenceData)
    }
  },

  async updateEvidence(uuid: string, evidenceData: Partial<SubmitEvidenceRequest>, mediaFile?: File): Promise<Evidence> {
    if (mediaFile) {
      const formData = new FormData()
      formData.append('evidence', JSON.stringify(evidenceData))
      formData.append('media', mediaFile)
      return apiClient.upload<Evidence>(`/api/v1/evidence/${uuid}`, formData)
    } else {
      return apiClient.put<Evidence>(`/api/v1/evidence/${uuid}`, evidenceData)
    }
  },

  async deleteEvidence(uuid: string): Promise<void> {
    return apiClient.delete(`/api/v1/evidence/${uuid}`)
  },

  async validateEvidence(validationData: ValidateEvidenceRequest): Promise<EvidenceValidation> {
    return apiClient.post<EvidenceValidation>('/api/v1/evidence/validate', validationData)
  },

  async getEvidenceForValidation(page = 0, size = 20): Promise<{
    content: Evidence[]
    totalElements: number
    totalPages: number
  }> {
    return apiClient.get(`/api/v1/evidence/for-validation?page=${page}&size=${size}`)
  },

  async flagEvidence(uuid: string, reason: string): Promise<void> {
    return apiClient.post(`/api/v1/evidence/${uuid}/flag`, { reason })
  }
}

// =============================================================================
// PARTICIPATIONS API
// =============================================================================

export interface ChallengeParticipation {
  id: number
  uuid: string
  challengeUuid: string
  challengeTitle: string
  userUuid: string
  status: 'ENROLLED' | 'ACTIVE' | 'COMPLETED' | 'ABANDONED'
  progress: number
  joinedAt: string
  completedAt?: string
  evidenceSubmitted: number
  evidenceApproved: number
  currentStreak: number
  maxStreak: number
  pointsEarned: number
}

export const participationsApi = {
  async getMyParticipations(): Promise<ChallengeParticipation[]> {
    return apiClient.get<ChallengeParticipation[]>('/api/v1/participations/my')
  },

  async getParticipation(uuid: string): Promise<ChallengeParticipation> {
    return apiClient.get<ChallengeParticipation>(`/api/v1/participations/${uuid}`)
  },

  async updateProgress(uuid: string, progress: number): Promise<ChallengeParticipation> {
    return apiClient.put<ChallengeParticipation>(`/api/v1/participations/${uuid}/progress`, { progress })
  },

  async completeParticipation(uuid: string): Promise<ChallengeParticipation> {
    return apiClient.post<ChallengeParticipation>(`/api/v1/participations/${uuid}/complete`)
  },

  async abandonParticipation(uuid: string, reason?: string): Promise<void> {
    return apiClient.post(`/api/v1/participations/${uuid}/abandon`, { reason })
  }
}

// =============================================================================
// VALIDATORS API
// =============================================================================

export interface ValidatorInvitation {
  id: number
  uuid: string
  inviterUuid: string
  inviterName: string
  inviteeEmail: string
  challengeUuid?: string
  challengeTitle?: string
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'EXPIRED'
  sentAt: string
  respondedAt?: string
  expiresAt: string
}

export interface ValidatorStats {
  totalValidations: number
  averageScore: number
  trustScore: number
  validationsThisWeek: number
  rank: number
  badges: string[]
}

export const validatorsApi = {
  async inviteValidator(email: string, challengeUuid?: string): Promise<ValidatorInvitation> {
    return apiClient.post<ValidatorInvitation>('/api/v1/validators/invite', {
      email,
      challengeUuid
    })
  },

  async acceptInvitation(invitationUuid: string): Promise<void> {
    return apiClient.post(`/api/v1/validators/invitations/${invitationUuid}/accept`)
  },

  async declineInvitation(invitationUuid: string, reason?: string): Promise<void> {
    return apiClient.post(`/api/v1/validators/invitations/${invitationUuid}/decline`, { reason })
  },

  async getMyInvitations(): Promise<ValidatorInvitation[]> {
    return apiClient.get<ValidatorInvitation[]>('/api/v1/validators/invitations/my')
  },

  async getValidatorStats(): Promise<ValidatorStats> {
    return apiClient.get<ValidatorStats>('/api/v1/validators/stats')
  },

  async getValidatorDashboard(): Promise<{
    pendingValidations: number
    completedToday: number
    averageTimeSpent: number
    topCategories: Array<{ category: string; count: number }>
  }> {
    return apiClient.get('/api/v1/validators/dashboard')
  }
}

// =============================================================================
// BILLING API (Conditional on BILLING_ON)
// =============================================================================

export interface SubscriptionPlan {
  id: string
  name: string
  description: string
  price: number
  currency: string
  billingPeriod: 'MONTHLY' | 'YEARLY'
  features: string[]
  limitations: {
    maxChallenges: number
    maxEvidenceUploads: number
    maxStorageGB: number
    allowsMultimedia: boolean
  }
  popular: boolean
  stripePriceId?: string
  paypalPlanId?: string
}

export interface UserSubscription {
  id: number
  uuid: string
  planId: string
  planName: string
  status: 'TRIAL' | 'ACTIVE' | 'CANCELED' | 'PAST_DUE' | 'PAUSED'
  provider: 'STRIPE' | 'PAYPAL'
  startDate: string
  endDate?: string
  nextBillingDate?: string
  cancelAtPeriodEnd: boolean
  trialEndDate?: string
  usage: {
    challengesUsed: number
    evidenceUploadsUsed: number
    storageUsedMB: number
  }
}

export interface CheckoutRequest {
  planId: string
  billingPeriod: 'MONTHLY' | 'YEARLY'
  provider: 'STRIPE' | 'PAYPAL'
  successUrl: string
  cancelUrl: string
  couponCode?: string
}

export const billingApi = {
  async getPlans(): Promise<SubscriptionPlan[]> {
    return apiClient.get<SubscriptionPlan[]>('/api/v1/billing/plans')
  },

  async getCurrentSubscription(): Promise<UserSubscription | null> {
    try {
      return await apiClient.get<UserSubscription>('/api/v1/billing/subscription')
    } catch (error) {
      if (error instanceof Error && error.message.includes('404')) {
        return null
      }
      throw error
    }
  },

  async createCheckoutSession(checkoutData: CheckoutRequest): Promise<{ checkoutUrl: string }> {
    return apiClient.post<{ checkoutUrl: string }>('/api/v1/billing/checkout', checkoutData)
  },

  async cancelSubscription(reason?: string): Promise<void> {
    return apiClient.post('/api/v1/billing/subscription/cancel', { reason })
  },

  async reactivateSubscription(): Promise<void> {
    return apiClient.post('/api/v1/billing/subscription/reactivate')
  },

  async createCustomerPortalSession(): Promise<{ portalUrl: string }> {
    return apiClient.post<{ portalUrl: string }>('/api/v1/billing/customer-portal')
  },

  async validateCoupon(code: string): Promise<{
    valid: boolean
    discount: number
    description: string
  }> {
    return apiClient.get(`/api/v1/billing/coupons/${code}/validate`)
  }
}

// Export del client para uso directo si es necesario
export { apiClient }
