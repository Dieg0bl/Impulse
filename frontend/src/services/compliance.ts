// GDPR/DSA Compliance Service for IMPULSE LEAN v1 - Phase 9
// Complete legal compliance system with consent management

import React from 'react'
import { apiClient } from './api'

// =============================================================================
// GDPR TYPES & INTERFACES
// =============================================================================

export interface ConsentRecord {
  id: string
  userId: string
  consentType: ConsentType
  granted: boolean
  grantedAt?: string
  revokedAt?: string
  purpose: string
  legalBasis: LegalBasis
  dataCategories: DataCategory[]
  retentionPeriod: number // days
  source: ConsentSource
  ipAddress: string
  userAgent: string
  version: string
}

export type ConsentType = 
  | 'COOKIES_ANALYTICS'
  | 'COOKIES_MARKETING'
  | 'COOKIES_FUNCTIONAL'
  | 'DATA_PROCESSING'
  | 'MARKETING_COMMUNICATIONS'
  | 'PROFILE_VISIBILITY'
  | 'DATA_SHARING'
  | 'AUTOMATED_DECISION_MAKING'

export type LegalBasis =
  | 'CONSENT'
  | 'CONTRACT'
  | 'LEGAL_OBLIGATION'
  | 'VITAL_INTERESTS'
  | 'PUBLIC_TASK'
  | 'LEGITIMATE_INTERESTS'

export type DataCategory =
  | 'PERSONAL_IDENTIFIERS'
  | 'CONTACT_INFORMATION'
  | 'PROFESSIONAL_INFORMATION'
  | 'BEHAVIORAL_DATA'
  | 'TECHNICAL_DATA'
  | 'FINANCIAL_DATA'
  | 'HEALTH_DATA'
  | 'BIOMETRIC_DATA'
  | 'LOCATION_DATA'

export type ConsentSource =
  | 'REGISTRATION'
  | 'COOKIE_BANNER'
  | 'SETTINGS_PAGE'
  | 'FORM_SUBMISSION'
  | 'API_CALL'

export interface DataProcessingRecord {
  id: string
  userId: string
  activity: ProcessingActivity
  purpose: string
  legalBasis: LegalBasis
  dataCategories: DataCategory[]
  timestamp: string
  automated: boolean
  thirdPartySharing?: ThirdPartySharing[]
  retentionApplied: boolean
  anonymized: boolean
}

export type ProcessingActivity =
  | 'DATA_COLLECTION'
  | 'DATA_STORAGE'
  | 'DATA_ANALYSIS'
  | 'DATA_SHARING'
  | 'DATA_TRANSFER'
  | 'DATA_DELETION'
  | 'AUTOMATED_DECISION'
  | 'PROFILING'

export interface ThirdPartySharing {
  recipient: string
  purpose: string
  country: string
  safeguards: string[]
  contractualBasis: string
}

// =============================================================================
// DATA SUBJECT RIGHTS
// =============================================================================

export interface DataSubjectRequest {
  id: string
  userId: string
  requestType: DataSubjectRightType
  status: RequestStatus
  submittedAt: string
  processedAt?: string
  completedAt?: string
  description: string
  verificationRequired: boolean
  verifiedAt?: string
  responseData?: any
  rejectionReason?: string
  requestedBy: string // email/contact
}

export type DataSubjectRightType =
  | 'ACCESS'           // Art. 15 - Right of access
  | 'RECTIFICATION'    // Art. 16 - Right to rectification
  | 'ERASURE'          // Art. 17 - Right to erasure
  | 'RESTRICTION'      // Art. 18 - Right to restriction
  | 'PORTABILITY'      // Art. 20 - Right to data portability
  | 'OBJECTION'        // Art. 21 - Right to object
  | 'AUTOMATED_DECISION' // Art. 22 - Automated decision-making

export type RequestStatus =
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'VERIFICATION_REQUIRED'
  | 'PROCESSING'
  | 'COMPLETED'
  | 'REJECTED'
  | 'PARTIALLY_FULFILLED'

// =============================================================================
// DSA COMPLIANCE TYPES
// =============================================================================

export interface ContentModerationRecord {
  id: string
  contentId: string
  contentType: 'CHALLENGE' | 'EVIDENCE' | 'COMMENT' | 'PROFILE' | 'MESSAGE'
  userId: string
  moderatorId?: string
  action: ModerationAction
  reason: ModerationReason
  automated: boolean
  timestamp: string
  appealable: boolean
  appealDeadline?: string
  transparency: TransparencyReport
}

export type ModerationAction =
  | 'NO_ACTION'
  | 'WARNING'
  | 'CONTENT_REMOVAL'
  | 'CONTENT_RESTRICTION'
  | 'ACCOUNT_SUSPENSION'
  | 'ACCOUNT_TERMINATION'
  | 'AGE_RESTRICTION'
  | 'GEOGRAPHIC_RESTRICTION'

export type ModerationReason =
  | 'ILLEGAL_CONTENT'
  | 'HARMFUL_CONTENT'
  | 'MISINFORMATION'
  | 'HARASSMENT'
  | 'HATE_SPEECH'
  | 'VIOLENCE'
  | 'ADULT_CONTENT'
  | 'SPAM'
  | 'COPYRIGHT_VIOLATION'
  | 'PRIVACY_VIOLATION'
  | 'TERMS_VIOLATION'

export interface TransparencyReport {
  decisionBasis: string
  appliedStandards: string[]
  automatedMeans: boolean
  humanReview: boolean
  appealPossible: boolean
  explanationProvided: boolean
}

// =============================================================================
// GDPR COMPLIANCE SERVICE
// =============================================================================

export class GDPRComplianceService {
  private api: typeof apiClient

  constructor(api: typeof apiClient) {
    this.api = api
  }

  // Consent Management
  async recordConsent(consent: Omit<ConsentRecord, 'id'>): Promise<ConsentRecord> {
    const response = await this.api.post<{ consent: ConsentRecord }>('/gdpr/consent', consent)
    return response.consent
  }

  async getUserConsents(userId: string): Promise<ConsentRecord[]> {
    const response = await this.api.get<{ consents: ConsentRecord[] }>(`/gdpr/users/${userId}/consents`)
    return response.consents
  }

  async updateConsent(consentId: string, granted: boolean): Promise<ConsentRecord> {
    const response = await this.api.put<{ consent: ConsentRecord }>(`/gdpr/consent/${consentId}`, { granted })
    return response.consent
  }

  async revokeConsent(consentId: string): Promise<void> {
    await this.api.delete(`/gdpr/consent/${consentId}`)
  }

  // Data Processing Records
  async recordProcessingActivity(activity: Omit<DataProcessingRecord, 'id'>): Promise<DataProcessingRecord> {
    const response = await this.api.post<{ record: DataProcessingRecord }>('/gdpr/processing', activity)
    return response.record
  }

  async getProcessingHistory(userId: string): Promise<DataProcessingRecord[]> {
    const response = await this.api.get<{ records: DataProcessingRecord[] }>(`/gdpr/users/${userId}/processing`)
    return response.records
  }

  // Data Subject Rights
  async submitDataSubjectRequest(request: Omit<DataSubjectRequest, 'id' | 'submittedAt' | 'status'>): Promise<DataSubjectRequest> {
    const response = await this.api.post<{ request: DataSubjectRequest }>('/gdpr/data-subject-request', request)
    return response.request
  }

  async getDataSubjectRequests(userId: string): Promise<DataSubjectRequest[]> {
    const response = await this.api.get<{ requests: DataSubjectRequest[] }>(`/gdpr/users/${userId}/requests`)
    return response.requests
  }

  async processDataPortability(userId: string): Promise<{ downloadUrl: string; expiresAt: string }> {
    const response = await this.api.post<{ downloadUrl: string; expiresAt: string }>(`/gdpr/users/${userId}/export`, {})
    return response
  }

  async processDataErasure(userId: string, keepLegalBasis: boolean = true): Promise<{ erasedData: string[]; retainedData: string[] }> {
    const response = await this.api.post<{ erasedData: string[]; retainedData: string[] }>(`/gdpr/users/${userId}/erase`, { keepLegalBasis })
    return response
  }

  // Privacy Impact Assessment
  async getCookiePolicy(): Promise<{ policy: string; lastUpdated: string; version: string }> {
    const response = await this.api.get<{ policy: string; lastUpdated: string; version: string }>('/gdpr/cookie-policy')
    return response
  }

  async getPrivacyPolicy(): Promise<{ policy: string; lastUpdated: string; version: string }> {
    const response = await this.api.get<{ policy: string; lastUpdated: string; version: string }>('/gdpr/privacy-policy')
    return response
  }

  async getDataRetentionPolicy(): Promise<{ 
    policies: Array<{
      dataCategory: DataCategory
      retentionPeriod: number
      automaticDeletion: boolean
      legalBasis: string
    }>
  }> {
    const response = await this.api.get<{ 
      policies: Array<{
        dataCategory: DataCategory
        retentionPeriod: number
        automaticDeletion: boolean
        legalBasis: string
      }>
    }>('/gdpr/retention-policy')
    return response
  }
}

// =============================================================================
// DSA COMPLIANCE SERVICE
// =============================================================================

export class DSAComplianceService {
  private api: typeof apiClient

  constructor(api: typeof apiClient) {
    this.api = api
  }

  // Content Moderation
  async recordModerationAction(action: Omit<ContentModerationRecord, 'id' | 'timestamp'>): Promise<ContentModerationRecord> {
    const response = await this.api.post<{ record: ContentModerationRecord }>('/dsa/moderation', action)
    return response.record
  }

  async getModerationHistory(contentId: string): Promise<ContentModerationRecord[]> {
    const response = await this.api.get<{ records: ContentModerationRecord[] }>(`/dsa/content/${contentId}/moderation`)
    return response.records
  }

  async appealModerationDecision(recordId: string, appealReason: string): Promise<{ appealId: string; deadline: string }> {
    const response = await this.api.post<{ appealId: string; deadline: string }>(`/dsa/moderation/${recordId}/appeal`, { appealReason })
    return response
  }

  // Transparency Reporting
  async getTransparencyReport(period: 'MONTHLY' | 'QUARTERLY' | 'YEARLY'): Promise<{
    period: string
    totalContent: number
    moderatedContent: number
    moderationActions: Record<ModerationAction, number>
    automatedActions: number
    humanReviews: number
    appealsReceived: number
    appealsUpheld: number
    averageResponseTime: number
  }> {
    const response = await this.api.get<{
      period: string
      totalContent: number
      moderatedContent: number
      moderationActions: Record<ModerationAction, number>
      automatedActions: number
      humanReviews: number
      appealsReceived: number
      appealsUpheld: number
      averageResponseTime: number
    }>(`/dsa/transparency-report?period=${period}`)
    return response
  }

  // Illegal Content Reporting
  async reportIllegalContent(report: {
    contentId: string
    contentType: string
    reportReason: string
    description: string
    reporterContact?: string
  }): Promise<{ reportId: string; referenceNumber: string }> {
    const response = await this.api.post<{ reportId: string; referenceNumber: string }>('/dsa/report-illegal-content', report)
    return response
  }

  async getIllegalContentReports(userId: string): Promise<Array<{
    reportId: string
    referenceNumber: string
    status: 'SUBMITTED' | 'UNDER_REVIEW' | 'RESOLVED' | 'REJECTED'
    submittedAt: string
    resolvedAt?: string
  }>> {
    const response = await this.api.get<{ reports: Array<{
      reportId: string
      referenceNumber: string
      status: 'SUBMITTED' | 'UNDER_REVIEW' | 'RESOLVED' | 'REJECTED'
      submittedAt: string
      resolvedAt?: string
    }> }>(`/dsa/users/${userId}/reports`)
    return response.reports
  }
}

// =============================================================================
// COMPLIANCE MANAGER
// =============================================================================

export class ComplianceManager {
  private gdprService: GDPRComplianceService
  private dsaService: DSAComplianceService

  constructor() {
    this.gdprService = new GDPRComplianceService(apiClient)
    this.dsaService = new DSAComplianceService(apiClient)
  }

  // GDPR Operations
  get gdpr() {
    return this.gdprService
  }

  // DSA Operations
  get dsa() {
    return this.dsaService
  }

  // Combined compliance check
  async checkUserCompliance(userId: string): Promise<{
    gdpr: {
      hasValidConsents: boolean
      consentCount: number
      lastConsentDate?: string
      pendingRequests: number
    }
    dsa: {
      moderationHistory: number
      activeRestrictions: number
      appealsPending: number
    }
    recommendations: string[]
  }> {
    const [consents, requests, moderationHistory] = await Promise.all([
      this.gdprService.getUserConsents(userId),
      this.gdprService.getDataSubjectRequests(userId),
      this.dsaService.getModerationHistory(userId).catch(() => [])
    ])

    const validConsents = consents.filter(c => c.granted && !c.revokedAt)
    const pendingRequests = requests.filter(r => ['SUBMITTED', 'UNDER_REVIEW', 'PROCESSING'].includes(r.status))
    const activeRestrictions = moderationHistory.filter(r => 
      ['CONTENT_RESTRICTION', 'ACCOUNT_SUSPENSION'].includes(r.action)
    )

    const recommendations: string[] = []
    
    if (validConsents.length === 0) {
      recommendations.push('User needs to provide valid consent for data processing')
    }
    
    if (pendingRequests.length > 0) {
      recommendations.push(`${pendingRequests.length} pending data subject requests require attention`)
    }

    if (activeRestrictions.length > 0) {
      recommendations.push(`User has ${activeRestrictions.length} active content restrictions`)
    }

    return {
      gdpr: {
        hasValidConsents: validConsents.length > 0,
        consentCount: validConsents.length,
        lastConsentDate: validConsents[0]?.grantedAt,
        pendingRequests: pendingRequests.length
      },
      dsa: {
        moderationHistory: moderationHistory.length,
        activeRestrictions: activeRestrictions.length,
        appealsPending: 0 // Would need separate API call
      },
      recommendations
    }
  }

  // Auto-compliance for new users
  async initializeUserCompliance(userId: string, ipAddress: string, userAgent: string): Promise<void> {
    const defaultConsents: Omit<ConsentRecord, 'id'>[] = [
      {
        userId,
        consentType: 'DATA_PROCESSING',
        granted: true,
        purpose: 'Core platform functionality and service delivery',
        legalBasis: 'CONTRACT',
        dataCategories: ['PERSONAL_IDENTIFIERS', 'CONTACT_INFORMATION', 'PROFESSIONAL_INFORMATION'],
        retentionPeriod: 2555, // 7 years
        source: 'REGISTRATION',
        ipAddress,
        userAgent,
        version: '1.0'
      },
      {
        userId,
        consentType: 'COOKIES_FUNCTIONAL',
        granted: true,
        purpose: 'Essential website functionality',
        legalBasis: 'LEGITIMATE_INTERESTS',
        dataCategories: ['TECHNICAL_DATA'],
        retentionPeriod: 365,
        source: 'REGISTRATION',
        ipAddress,
        userAgent,
        version: '1.0'
      }
    ]

    for (const consent of defaultConsents) {
      await this.gdprService.recordConsent(consent)
    }

    // Record initial data processing
    await this.gdprService.recordProcessingActivity({
      userId,
      activity: 'DATA_COLLECTION',
      purpose: 'User registration and account creation',
      legalBasis: 'CONTRACT',
      dataCategories: ['PERSONAL_IDENTIFIERS', 'CONTACT_INFORMATION'],
      timestamp: new Date().toISOString(),
      automated: true,
      retentionApplied: true,
      anonymized: false
    })
  }
}

// =============================================================================
// REACT HOOKS
// =============================================================================

export function useGDPRCompliance() {
  const complianceManager = React.useMemo(() => new ComplianceManager(), [])

  const recordConsent = React.useCallback(
    async (consent: Omit<ConsentRecord, 'id'>) => {
      return complianceManager.gdpr.recordConsent(consent)
    },
    [complianceManager]
  )

  const submitDataRequest = React.useCallback(
    async (request: Omit<DataSubjectRequest, 'id' | 'submittedAt' | 'status'>) => {
      return complianceManager.gdpr.submitDataSubjectRequest(request)
    },
    [complianceManager]
  )

  const exportUserData = React.useCallback(
    async (userId: string) => {
      return complianceManager.gdpr.processDataPortability(userId)
    },
    [complianceManager]
  )

  return {
    recordConsent,
    submitDataRequest,
    exportUserData,
    complianceManager
  }
}

export function useDSACompliance() {
  const complianceManager = React.useMemo(() => new ComplianceManager(), [])

  const reportContent = React.useCallback(
    async (report: {
      contentId: string
      contentType: string
      reportReason: string
      description: string
      reporterContact?: string
    }) => {
      return complianceManager.dsa.reportIllegalContent(report)
    },
    [complianceManager]
  )

  const appealDecision = React.useCallback(
    async (recordId: string, appealReason: string) => {
      return complianceManager.dsa.appealModerationDecision(recordId, appealReason)
    },
    [complianceManager]
  )

  return {
    reportContent,
    appealDecision,
    complianceManager
  }
}

// Export singleton instances
export const complianceManager = new ComplianceManager()
export const gdprService = new GDPRComplianceService(apiClient)
export const dsaService = new DSAComplianceService(apiClient)
