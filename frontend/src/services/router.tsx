// Routing and Navigation for IMPULSE LEAN v1
// Protected routes, deep links, and error boundaries

import React, { Suspense, lazy } from 'react'
import { createBrowserRouter, RouterProvider, Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../services/store'
import ErrorBoundary from '../components/ErrorBoundary'
import LoadingSpinner from '../components/LoadingSpinner'
import Layout from '../components/Layout'
import NotFound from '../pages/NotFound'

// Lazy load pages for better performance
const Dashboard = lazy(() => import('../pages/Dashboard'))
const Challenges = lazy(() => import('../pages/Challenges'))
const ChallengeDetail = lazy(() => import('../pages/ChallengeDetail'))
const CreateChallenge = lazy(() => import('../pages/CreateChallenge'))
const Validators = lazy(() => import('../pages/Validators'))
const PrivacyModeration = lazy(() => import('../pages/PrivacyModeration'))
const Billing = lazy(() => import('../pages/Billing'))
const Account = lazy(() => import('../pages/Account'))
const Login = lazy(() => import('../pages/Login'))
const Register = lazy(() => import('../pages/Register'))
const EmailVerification = lazy(() => import('../pages/EmailVerification'))
const ResetPassword = lazy(() => import('../pages/ResetPassword'))
const ValidatorInvitation = lazy(() => import('../pages/ValidatorInvitation'))
const PublicProfile = lazy(() => import('../pages/PublicProfile'))

// =============================================================================
// ROUTE GUARDS
// =============================================================================

interface ProtectedRouteProps {
  children: React.ReactNode
  requiredRole?: 'USER' | 'VALIDATOR' | 'MODERATOR' | 'ADMIN' | 'SUPER_ADMIN'
  redirectTo?: string
}

function ProtectedRoute({ children, requiredRole, redirectTo = '/login' }: ProtectedRouteProps) {
  const { isAuthenticated, user } = useAuth()

  if (!isAuthenticated) {
    return <Navigate to={redirectTo} replace />
  }

  if (requiredRole && user) {
    const roleHierarchy = {
      'GUEST': 0,
      'USER': 1,
      'VALIDATOR': 2,
      'MODERATOR': 3,
      'ADMIN': 4,
      'SUPER_ADMIN': 5
    }

    const userLevel = roleHierarchy[user.role] || 0
    const requiredLevel = roleHierarchy[requiredRole] || 0

    if (userLevel < requiredLevel) {
      return <Navigate to="/unauthorized" replace />
    }
  }

  return <>{children}</>
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth()

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  return <>{children}</>
}

// Layout wrapper with navigation
function AppLayout() {
  return (
    <ErrorBoundary>
      <Layout>
        <Suspense fallback={<LoadingSpinner />}>
          <Outlet />
        </Suspense>
      </Layout>
    </ErrorBoundary>
  )
}

// Public layout (no navigation)
function PublicLayout() {
  return (
    <ErrorBoundary>
      <div className="min-h-screen bg-gray-50">
        <Suspense fallback={<LoadingSpinner />}>
          <Outlet />
        </Suspense>
      </div>
    </ErrorBoundary>
  )
}

// =============================================================================
// ROUTER CONFIGURATION
// =============================================================================

const router = createBrowserRouter([
  // Public routes
  {
    path: '/public',
    element: <PublicLayout />,
    children: [
      {
        path: 'login',
        element: (
          <PublicRoute>
            <Login />
          </PublicRoute>
        )
      },
      {
        path: 'register',
        element: (
          <PublicRoute>
            <Register />
          </PublicRoute>
        )
      },
      {
        path: 'verify-email',
        element: <EmailVerification />
      },
      {
        path: 'reset-password',
        element: <ResetPassword />
      },
      {
        path: 'profile/:uuid',
        element: <PublicProfile />
      }
    ]
  },

  // Special invitation routes (accessible when not authenticated)
  {
    path: '/invite/validator/:token',
    element: (
      <PublicLayout>
        <ValidatorInvitation />
      </PublicLayout>
    )
  },

  // Main application routes
  {
    path: '/',
    element: <AppLayout />,
    children: [
      // Redirect root to dashboard
      {
        index: true,
        element: <Navigate to="/dashboard" replace />
      },

      // Dashboard (requires authentication)
      {
        path: 'dashboard',
        element: (
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        )
      },

      // Challenges
      {
        path: 'challenges',
        children: [
          {
            index: true,
            element: (
              <ProtectedRoute>
                <Challenges />
              </ProtectedRoute>
            )
          },
          {
            path: 'create',
            element: (
              <ProtectedRoute>
                <CreateChallenge />
              </ProtectedRoute>
            )
          },
          {
            path: ':uuid',
            element: (
              <ProtectedRoute>
                <ChallengeDetail />
              </ProtectedRoute>
            )
          }
        ]
      },

      // Validators (requires VALIDATOR role or higher)
      {
        path: 'validators',
        element: (
          <ProtectedRoute requiredRole="VALIDATOR">
            <Validators />
          </ProtectedRoute>
        )
      },

      // Privacy & Moderation
      {
        path: 'privacy-moderation',
        element: (
          <ProtectedRoute>
            <PrivacyModeration />
          </ProtectedRoute>
        )
      },

      // Billing (conditional on BILLING_ON flag)
      {
        path: 'billing',
        element: (
          <ProtectedRoute>
            <BillingRouteWrapper />
          </ProtectedRoute>
        )
      },

      // Account settings
      {
        path: 'account',
        element: (
          <ProtectedRoute>
            <Account />
          </ProtectedRoute>
        )
      },

      // Admin routes (requires ADMIN role or higher)
      {
        path: 'admin',
        element: (
          <ProtectedRoute requiredRole="ADMIN">
            <AdminLayout />
          </ProtectedRoute>
        ),
        children: [
          {
            index: true,
            element: <Navigate to="/admin/users" replace />
          },
          {
            path: 'users',
            element: lazy(() => import('../pages/admin/Users'))
          },
          {
            path: 'challenges',
            element: lazy(() => import('../pages/admin/Challenges'))
          },
          {
            path: 'reports',
            element: lazy(() => import('../pages/admin/Reports'))
          },
          {
            path: 'analytics',
            element: lazy(() => import('../pages/admin/Analytics'))
          }
        ]
      }
    ]
  },

  // Legacy redirects
  {
    path: '/login',
    element: <Navigate to="/public/login" replace />
  },
  {
    path: '/register',
    element: <Navigate to="/public/register" replace />
  },

  // Error pages
  {
    path: '/unauthorized',
    element: (
      <PublicLayout>
        <UnauthorizedPage />
      </PublicLayout>
    )
  },
  {
    path: '*',
    element: (
      <PublicLayout>
        <NotFound />
      </PublicLayout>
    )
  }
])

// =============================================================================
// ROUTE WRAPPERS
// =============================================================================

function BillingRouteWrapper() {
  const BILLING_ENABLED = import.meta.env.VITE_BILLING_ON === 'true'
  
  if (!BILLING_ENABLED) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Billing Not Available</h1>
          <p className="text-gray-600 mb-4">
            Billing features are currently disabled. Check back soon!
          </p>
          <a 
            href="/dashboard" 
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
          >
            Return to Dashboard
          </a>
        </div>
      </div>
    )
  }
  
  return <Billing />
}

function AdminLayout() {
  return (
    <div className="flex h-screen bg-gray-100">
      {/* Admin Sidebar */}
      <div className="w-64 bg-white shadow-sm">
        <div className="p-6">
          <h2 className="text-lg font-semibold text-gray-900">Admin Panel</h2>
        </div>
        <nav className="mt-6">
          <AdminNavLink to="/admin/users">Users</AdminNavLink>
          <AdminNavLink to="/admin/challenges">Challenges</AdminNavLink>
          <AdminNavLink to="/admin/reports">Reports</AdminNavLink>
          <AdminNavLink to="/admin/analytics">Analytics</AdminNavLink>
        </nav>
      </div>
      
      {/* Admin Content */}
      <div className="flex-1 overflow-auto">
        <Suspense fallback={<LoadingSpinner />}>
          <Outlet />
        </Suspense>
      </div>
    </div>
  )
}

function AdminNavLink({ to, children }: { to: string; children: React.ReactNode }) {
  return (
    <a
      href={to}
      className="block px-6 py-3 text-sm font-medium text-gray-700 hover:bg-gray-50 hover:text-gray-900"
    >
      {children}
    </a>
  )
}

// =============================================================================
// ERROR PAGES
// =============================================================================

function UnauthorizedPage() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="text-center">
          <h2 className="mt-6 text-center text-3xl font-bold tracking-tight text-gray-900">
            Access Denied
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            You don't have permission to access this page.
          </p>
          <div className="mt-6">
            <a
              href="/dashboard"
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
            >
              Return to Dashboard
            </a>
          </div>
        </div>
      </div>
    </div>
  )
}

// =============================================================================
// ROUTER COMPONENT
// =============================================================================

export default function AppRouter() {
  return <RouterProvider router={router} />
}

// =============================================================================
// NAVIGATION UTILITIES
// =============================================================================

export const navigationLinks = [
  {
    name: 'Dashboard',
    href: '/dashboard',
    icon: 'Home',
    requiredRole: 'USER'
  },
  {
    name: 'Challenges',
    href: '/challenges',
    icon: 'Target',
    requiredRole: 'USER'
  },
  {
    name: 'Validators',
    href: '/validators',
    icon: 'Shield',
    requiredRole: 'VALIDATOR'
  },
  {
    name: 'Privacy & Moderation',
    href: '/privacy-moderation',
    icon: 'Lock',
    requiredRole: 'USER'
  },
  {
    name: 'Billing',
    href: '/billing',
    icon: 'CreditCard',
    requiredRole: 'USER',
    conditional: true, // Only show if BILLING_ON
    environmentKey: 'VITE_BILLING_ON'
  },
  {
    name: 'Account',
    href: '/account',
    icon: 'User',
    requiredRole: 'USER'
  }
]

export const adminNavigationLinks = [
  {
    name: 'Users',
    href: '/admin/users',
    icon: 'Users',
    requiredRole: 'ADMIN'
  },
  {
    name: 'Challenges',
    href: '/admin/challenges',
    icon: 'Target',
    requiredRole: 'ADMIN'
  },
  {
    name: 'Reports',
    href: '/admin/reports',
    icon: 'AlertTriangle',
    requiredRole: 'MODERATOR'
  },
  {
    name: 'Analytics',
    href: '/admin/analytics',
    icon: 'BarChart3',
    requiredRole: 'ADMIN'
  }
]

// Helper function to check if user can access a route
export function canAccessRoute(
  user: any, 
  requiredRole?: string, 
  environmentKey?: string
): boolean {
  // Check environment flag first
  if (environmentKey && import.meta.env[environmentKey] !== 'true') {
    return false
  }

  if (!user || !requiredRole) {
    return true
  }

  const roleHierarchy = {
    'GUEST': 0,
    'USER': 1,
    'VALIDATOR': 2,
    'MODERATOR': 3,
    'ADMIN': 4,
    'SUPER_ADMIN': 5
  }

  const userLevel = roleHierarchy[user.role] || 0
  const requiredLevel = roleHierarchy[requiredRole] || 0

  return userLevel >= requiredLevel
}

// Helper to generate deep links
export function generateInvitationLink(token: string): string {
  const baseUrl = window.location.origin
  return `${baseUrl}/invite/validator/${token}`
}

export function generatePublicProfileLink(userUuid: string): string {
  const baseUrl = window.location.origin
  return `${baseUrl}/public/profile/${userUuid}`
}

export function generateChallengeLink(challengeUuid: string): string {
  const baseUrl = window.location.origin
  return `${baseUrl}/challenges/${challengeUuid}`
}
