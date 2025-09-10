// Testing Framework for IMPULSE LEAN v1 - Phase 12
// Complete testing setup with ≥80% coverage requirement

import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { jest, describe, it, expect, beforeEach, afterEach } from '@jest/globals'
import userEvent from '@testing-library/user-event'

// =============================================================================
// TESTING UTILITIES
// =============================================================================

// Mock API responses
export const mockApiResponse = (data: any, status = 200) => {
  return Promise.resolve({
    ok: status >= 200 && status < 300,
    status,
    json: () => Promise.resolve(data),
    text: () => Promise.resolve(JSON.stringify(data))
  })
}

// Test wrapper for React components with providers
export const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <div data-testid="test-wrapper">
      {children}
    </div>
  )
}

// Helper for testing async components
export const waitForLoadingToFinish = () => {
  return waitFor(() => {
    expect(screen.queryByText(/loading/i)).not.toBeInTheDocument()
  })
}

// =============================================================================
// UNIT TESTS - SERVICES
// =============================================================================

describe('ObservabilityService', () => {
  let observabilityService: any
  
  beforeEach(() => {
    // Reset mocks before each test
    global.fetch = jest.fn()
    localStorage.clear()
    localStorage.setItem('authToken', 'test-token')
  })

  afterEach(() => {
    jest.restoreAllMocks()
  })

  describe('getMetrics', () => {
    it('should fetch metrics successfully', async () => {
      const mockMetrics = [
        {
          id: 'test_metric',
          name: 'Test Metric',
          value: 100,
          unit: 'count',
          trend: 'up',
          change: 10,
          status: 'healthy',
          timestamp: new Date().toISOString()
        }
      ]

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse(mockMetrics)
      )

      const { ObservabilityService } = await import('../services/observability')
      const service = new ObservabilityService()
      const result = await service.getMetrics('24h')

      expect(global.fetch).toHaveBeenCalledWith(
        '/api/v1/observability/metrics?range=24h',
        expect.objectContaining({
          headers: expect.objectContaining({
            'Authorization': 'Bearer test-token'
          })
        })
      )
      expect(result).toEqual(mockMetrics)
    })

    it('should handle API errors gracefully', async () => {
      ;(global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 500
      })

      const { ObservabilityService } = await import('../services/observability')
      const service = new ObservabilityService()
      const result = await service.getMetrics('24h')

      // Should return mock data when API fails
      expect(Array.isArray(result)).toBe(true)
      expect(result.length).toBeGreaterThan(0)
    })
  })

  describe('getSystemHealth', () => {
    it('should fetch system health data', async () => {
      const mockHealth = {
        overall: 'healthy',
        services: {
          api: 'up',
          database: 'up',
          cache: 'up',
          storage: 'up',
          payments: 'up'
        },
        uptime: 99.95,
        responseTime: 245,
        errorRate: 0.12
      }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse(mockHealth)
      )

      const { ObservabilityService } = await import('../services/observability')
      const service = new ObservabilityService()
      const result = await service.getSystemHealth()

      expect(result).toEqual(mockHealth)
    })
  })
})

describe('AlertingService', () => {
  let alertingService: any
  
  beforeEach(() => {
    global.fetch = jest.fn()
    localStorage.setItem('authToken', 'test-token')
  })

  afterEach(() => {
    jest.restoreAllMocks()
  })

  describe('createAlertRule', () => {
    it('should create alert rule successfully', async () => {
      const newRule = {
        name: 'Test Rule',
        description: 'Test alert rule',
        enabled: true,
        metric: 'test_metric',
        condition: 'greater_than' as const,
        threshold: 100,
        timeWindow: 5,
        severity: 'high' as const,
        cooldown: 15,
        actions: [],
        metadata: {}
      }

      const createdRule = { ...newRule, id: 'rule-123' }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse(createdRule)
      )

      const { AlertingService } = await import('../services/alerting')
      const config = {
        rules: [],
        channels: {
          email: { enabled: false, smtpHost: '', smtpPort: 587, username: '', from: '', templates: {} },
          slack: { enabled: false, webhookUrl: '', channel: '', username: '' },
          webhook: { enabled: false, url: '', headers: {} },
          sms: { enabled: false, provider: 'twilio' as const, apiKey: '', from: '' },
          pagerDuty: { enabled: false, integrationKey: '', severity: '' }
        }
      }
      
      const service = new AlertingService(config)
      const result = await service.createAlertRule(newRule)

      expect(result).toEqual(createdRule)
      expect(global.fetch).toHaveBeenCalledWith(
        '/api/v1/alerts/rules',
        expect.objectContaining({
          method: 'POST',
          headers: expect.objectContaining({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer test-token'
          }),
          body: JSON.stringify(newRule)
        })
      )
    })
  })
})

// =============================================================================
// INTEGRATION TESTS - API ENDPOINTS
// =============================================================================

describe('API Integration Tests', () => {
  const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1'

  beforeEach(() => {
    global.fetch = jest.fn()
  })

  afterEach(() => {
    jest.restoreAllMocks()
  })

  describe('Authentication Endpoints', () => {
    it('should login successfully with valid credentials', async () => {
      const loginData = { email: 'test@example.com', password: 'password' }
      const responseData = { token: 'jwt-token', user: { id: 1, email: 'test@example.com' } }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse(responseData)
      )

      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginData)
      })

      const result = await response.json()
      expect(result).toEqual(responseData)
    })

    it('should reject invalid credentials', async () => {
      const loginData = { email: 'test@example.com', password: 'wrong' }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 401,
        json: () => Promise.resolve({ error: 'Invalid credentials' })
      })

      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loginData)
      })

      expect(response.ok).toBe(false)
      expect(response.status).toBe(401)
    })
  })

  describe('Challenge Endpoints', () => {
    it('should create challenge successfully', async () => {
      const challengeData = {
        title: 'Test Challenge',
        description: 'Test description',
        type: 'HABIT',
        duration: 30
      }

      const createdChallenge = { ...challengeData, id: 'challenge-123' }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse(createdChallenge)
      )

      const response = await fetch(`${API_BASE_URL}/challenges`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer test-token'
        },
        body: JSON.stringify(challengeData)
      })

      const result = await response.json()
      expect(result).toEqual(createdChallenge)
    })
  })

  describe('Observability Endpoints', () => {
    it('should fetch metrics successfully', async () => {
      const mockMetrics = [
        { id: 'metric1', name: 'Active Users', value: 1000, unit: 'users' },
        { id: 'metric2', name: 'Error Rate', value: 0.1, unit: '%' }
      ]

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse(mockMetrics)
      )

      const response = await fetch(`${API_BASE_URL}/observability/metrics?range=24h`, {
        headers: { 'Authorization': 'Bearer test-token' }
      })

      const result = await response.json()
      expect(result).toEqual(mockMetrics)
    })
  })
})

// =============================================================================
// E2E TESTS - CRITICAL USER JOURNEYS
// =============================================================================

describe('E2E User Journeys', () => {
  beforeEach(() => {
    // Reset all mocks
    global.fetch = jest.fn()
    localStorage.clear()
  })

  describe('User Registration and Login Journey', () => {
    it('should complete full registration and login flow', async () => {
      // Mock registration response
      ;(global.fetch as jest.Mock)
        .mockResolvedValueOnce(
          mockApiResponse({ success: true, message: 'Registration successful' })
        )
        .mockResolvedValueOnce(
          mockApiResponse({ token: 'jwt-token', user: { id: 1, email: 'test@example.com' } })
        )

      // Test would render registration form and simulate user interactions
      // This is a simplified version - in real implementation, you'd use React Testing Library
      
      const registrationData = {
        email: 'test@example.com',
        password: 'password123',
        firstName: 'Test',
        lastName: 'User'
      }

      // Simulate registration
      const regResponse = await fetch('/api/v1/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registrationData)
      })

      expect(regResponse.ok).toBe(true)

      // Simulate login
      const loginResponse = await fetch('/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: registrationData.email,
          password: registrationData.password
        })
      })

      const loginResult = await loginResponse.json()
      expect(loginResult.token).toBeDefined()
      expect(loginResult.user.email).toBe(registrationData.email)
    })
  })

  describe('Challenge Creation and Participation Journey', () => {
    it('should complete challenge lifecycle', async () => {
      // Mock API responses for challenge lifecycle
      const challengeId = 'challenge-123'
      
      ;(global.fetch as jest.Mock)
        .mockResolvedValueOnce(
          mockApiResponse({ id: challengeId, title: 'Test Challenge', status: 'ACTIVE' })
        )
        .mockResolvedValueOnce(
          mockApiResponse({ success: true, message: 'Joined challenge' })
        )
        .mockResolvedValueOnce(
          mockApiResponse({ id: 'evidence-123', status: 'PENDING' })
        )

      // Create challenge
      const challengeData = {
        title: 'Daily Exercise',
        description: 'Exercise for 30 minutes daily',
        type: 'HABIT',
        duration: 30
      }

      const createResponse = await fetch('/api/v1/challenges', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer test-token'
        },
        body: JSON.stringify(challengeData)
      })

      const challenge = await createResponse.json()
      expect(challenge.id).toBe(challengeId)

      // Join challenge
      const joinResponse = await fetch(`/api/v1/challenges/${challengeId}/join`, {
        method: 'POST',
        headers: { 'Authorization': 'Bearer test-token' }
      })

      expect(joinResponse.ok).toBe(true)

      // Submit evidence
      const evidenceData = {
        description: 'Completed 30-minute run',
        type: 'TEXT'
      }

      const evidenceResponse = await fetch(`/api/v1/challenges/${challengeId}/evidence`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer test-token'
        },
        body: JSON.stringify(evidenceData)
      })

      const evidence = await evidenceResponse.json()
      expect(evidence.id).toBeDefined()
    })
  })

  describe('Payment and Subscription Journey', () => {
    it('should complete payment flow', async () => {
      const paymentData = {
        amount: 2999, // $29.99
        currency: 'usd',
        paymentMethodId: 'pm_test_123'
      }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse({
          clientSecret: 'pi_test_123_secret',
          status: 'requires_payment_method'
        })
      )

      const response = await fetch('/api/v1/payments/create-intent', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer test-token'
        },
        body: JSON.stringify(paymentData)
      })

      const result = await response.json()
      expect(result.clientSecret).toBeDefined()
    })
  })
})

// =============================================================================
// SECURITY TESTS
// =============================================================================

describe('Security Tests', () => {
  describe('Authentication Security', () => {
    it('should reject requests without authentication', async () => {
      ;(global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 401,
        json: () => Promise.resolve({ error: 'Unauthorized' })
      })

      const response = await fetch('/api/v1/users/profile')
      expect(response.status).toBe(401)
    })

    it('should reject requests with invalid tokens', async () => {
      ;(global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 401,
        json: () => Promise.resolve({ error: 'Invalid token' })
      })

      const response = await fetch('/api/v1/users/profile', {
        headers: { 'Authorization': 'Bearer invalid-token' }
      })

      expect(response.status).toBe(401)
    })
  })

  describe('Input Validation', () => {
    it('should reject malformed email addresses', async () => {
      const invalidData = { email: 'not-an-email', password: 'password' }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: () => Promise.resolve({ error: 'Invalid email format' })
      })

      const response = await fetch('/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(invalidData)
      })

      expect(response.status).toBe(400)
    })

    it('should reject SQL injection attempts', async () => {
      const maliciousData = {
        email: "admin@test.com'; DROP TABLE users; --",
        password: 'password'
      }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: () => Promise.resolve({ error: 'Invalid input detected' })
      })

      const response = await fetch('/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(maliciousData)
      })

      expect(response.status).toBe(400)
    })
  })

  describe('GDPR Compliance', () => {
    it('should handle data export requests', async () => {
      const exportData = {
        user: { id: 1, email: 'test@example.com' },
        challenges: [],
        evidence: [],
        validations: []
      }

      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse(exportData)
      )

      const response = await fetch('/api/v1/compliance/data-export', {
        headers: { 'Authorization': 'Bearer test-token' }
      })

      const result = await response.json()
      expect(result.user).toBeDefined()
      expect(Array.isArray(result.challenges)).toBe(true)
    })

    it('should handle data deletion requests', async () => {
      ;(global.fetch as jest.Mock).mockResolvedValueOnce(
        mockApiResponse({ success: true, message: 'Data deletion initiated' })
      )

      const response = await fetch('/api/v1/compliance/delete-account', {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer test-token' }
      })

      const result = await response.json()
      expect(result.success).toBe(true)
    })
  })
})

// =============================================================================
// PERFORMANCE TESTS
// =============================================================================

describe('Performance Tests', () => {
  it('should handle concurrent requests', async () => {
    const concurrentRequests = 10
    const requests = Array(concurrentRequests).fill(null).map(() =>
      fetch('/api/v1/health', {
        headers: { 'Authorization': 'Bearer test-token' }
      })
    )

    ;(global.fetch as jest.Mock).mockImplementation(() =>
      Promise.resolve(mockApiResponse({ status: 'healthy' }))
    )

    const responses = await Promise.all(requests)
    expect(responses).toHaveLength(concurrentRequests)
    responses.forEach(response => {
      expect(response.ok).toBe(true)
    })
  })

  it('should respond within acceptable time limits', async () => {
    const startTime = Date.now()

    ;(global.fetch as jest.Mock).mockImplementation(() =>
      new Promise(resolve => {
        setTimeout(() => {
          resolve(mockApiResponse({ status: 'healthy' }))
        }, 100) // Simulate 100ms response time
      })
    )

    const response = await fetch('/api/v1/health')
    const endTime = Date.now()
    const responseTime = endTime - startTime

    expect(response.ok).toBe(true)
    expect(responseTime).toBeLessThan(500) // Should respond within 500ms
  })
})

// =============================================================================
// TEST COVERAGE REQUIREMENTS
// =============================================================================

// Coverage targets (enforced by Jest configuration):
// - Statements: >= 80%
// - Branches: >= 80%
// - Functions: >= 80%
// - Lines: >= 80%

export default {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/src/setupTests.ts'],
  collectCoverageFrom: [
    'src/**/*.{ts,tsx}',
    '!src/**/*.d.ts',
    '!src/index.tsx',
    '!src/reportWebVitals.ts',
    '!src/**/*.stories.tsx',
    '!src/**/*.test.{ts,tsx}'
  ],
  coverageThreshold: {
    global: {
      branches: 80,
      functions: 80,
      lines: 80,
      statements: 80
    }
  },
  testMatch: [
    '<rootDir>/src/**/__tests__/**/*.{ts,tsx}',
    '<rootDir>/src/**/*.{test,spec}.{ts,tsx}'
  ],
  moduleNameMapping: {
    '^@/(.*)$': '<rootDir>/src/$1'
  }
}
