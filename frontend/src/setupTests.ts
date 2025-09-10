// Test Setup Configuration for IMPULSE LEAN v1
// Global test utilities and mocks

import '@testing-library/jest-dom'

// =============================================================================
// GLOBAL MOCKS
// =============================================================================

// Mock fetch globally
global.fetch = jest.fn(() =>
  Promise.resolve({
    ok: true,
    status: 200,
    json: () => Promise.resolve({}),
    text: () => Promise.resolve(''),
    headers: new Headers(),
  })
) as jest.Mock

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
}
global.localStorage = localStorageMock as any

// Mock sessionStorage
const sessionStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
}
global.sessionStorage = sessionStorageMock as any

// Mock window.location
delete (window as any).location
window.location = {
  href: 'http://localhost:3000',
  origin: 'http://localhost:3000',
  protocol: 'http:',
  host: 'localhost:3000',
  hostname: 'localhost',
  port: '3000',
  pathname: '/',
  search: '',
  hash: '',
  assign: jest.fn(),
  replace: jest.fn(),
  reload: jest.fn(),
} as any

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(), // deprecated
    removeListener: jest.fn(), // deprecated
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn(),
  })),
})

// Mock IntersectionObserver
global.IntersectionObserver = class IntersectionObserver {
  constructor() {}
  observe() {}
  disconnect() {}
  unobserve() {}
} as any

// Mock ResizeObserver
global.ResizeObserver = class ResizeObserver {
  constructor() {}
  observe() {}
  disconnect() {}
  unobserve() {}
} as any

// Mock WebSocket
global.WebSocket = class WebSocket {
  constructor(url: string) {
    this.url = url
    this.readyState = WebSocket.CONNECTING
  }
  
  url: string
  readyState: number
  onopen: ((event: Event) => void) | null = null
  onclose: ((event: CloseEvent) => void) | null = null
  onmessage: ((event: MessageEvent) => void) | null = null
  onerror: ((event: Event) => void) | null = null
  
  static CONNECTING = 0
  static OPEN = 1
  static CLOSING = 2
  static CLOSED = 3
  
  send(data: string) {}
  close() {}
  addEventListener() {}
  removeEventListener() {}
  dispatchEvent() { return true }
} as any

// =============================================================================
// ENVIRONMENT VARIABLES
// =============================================================================

process.env.REACT_APP_API_URL = 'http://localhost:8080/api/v1'
process.env.REACT_APP_WS_URL = 'ws://localhost:8080/ws'
process.env.REACT_APP_STRIPE_PUBLIC_KEY = 'pk_test_123'
process.env.NODE_ENV = 'test'

// =============================================================================
// GLOBAL TEST UTILITIES
// =============================================================================

// Test data generators
export const createMockUser = (overrides = {}) => ({
  id: 'user-123',
  email: 'test@example.com',
  firstName: 'Test',
  lastName: 'User',
  role: 'USER',
  status: 'ACTIVE',
  createdAt: '2024-01-01T00:00:00Z',
  ...overrides
})

export const createMockChallenge = (overrides = {}) => ({
  id: 'challenge-123',
  title: 'Test Challenge',
  description: 'Test challenge description',
  type: 'HABIT',
  duration: 30,
  status: 'ACTIVE',
  createdBy: 'user-123',
  createdAt: '2024-01-01T00:00:00Z',
  ...overrides
})

export const createMockEvidence = (overrides = {}) => ({
  id: 'evidence-123',
  challengeId: 'challenge-123',
  userId: 'user-123',
  description: 'Test evidence',
  type: 'TEXT',
  status: 'PENDING',
  createdAt: '2024-01-01T00:00:00Z',
  ...overrides
})

export const createMockPayment = (overrides = {}) => ({
  id: 'payment-123',
  amount: 2999,
  currency: 'usd',
  status: 'succeeded',
  userId: 'user-123',
  createdAt: '2024-01-01T00:00:00Z',
  ...overrides
})

// API response helpers
export const mockApiSuccess = (data: any) => ({
  ok: true,
  status: 200,
  json: () => Promise.resolve(data),
  text: () => Promise.resolve(JSON.stringify(data))
})

export const mockApiError = (status: number, message: string) => ({
  ok: false,
  status,
  json: () => Promise.resolve({ error: message }),
  text: () => Promise.resolve(JSON.stringify({ error: message }))
})

// Wait for async operations
export const waitFor = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

// =============================================================================
// JEST EXTENSIONS
// =============================================================================

// Extend expect matchers
expect.extend({
  toHaveBeenCalledWithAuthToken(received, token) {
    const pass = received.mock.calls.some((call: any[]) => {
      const options = call[1]
      return options?.headers?.Authorization === `Bearer ${token}`
    })
    
    if (pass) {
      return {
        message: () => `expected ${received} not to have been called with auth token ${token}`,
        pass: true,
      }
    } else {
      return {
        message: () => `expected ${received} to have been called with auth token ${token}`,
        pass: false,
      }
    }
  },
  
  toHaveValidationError(received, field) {
    const hasError = received.some((error: any) => error.field === field)
    
    if (hasError) {
      return {
        message: () => `expected validation errors not to include field ${field}`,
        pass: true,
      }
    } else {
      return {
        message: () => `expected validation errors to include field ${field}`,
        pass: false,
      }
    }
  }
})

// =============================================================================
// CONSOLE OVERRIDES
// =============================================================================

// Suppress console.error in tests unless VERBOSE is set
const originalError = console.error
beforeAll(() => {
  if (!process.env.VERBOSE) {
    console.error = (...args: any[]) => {
      if (
        typeof args[0] === 'string' &&
        args[0].includes('Warning: ReactDOM.render is deprecated')
      ) {
        return
      }
      originalError.call(console, ...args)
    }
  }
})

afterAll(() => {
  console.error = originalError
})

// =============================================================================
// CLEANUP
// =============================================================================

// Reset all mocks after each test
afterEach(() => {
  jest.clearAllMocks()
  localStorage.clear()
  sessionStorage.clear()
})

// Global test cleanup
afterAll(() => {
  jest.restoreAllMocks()
})
