// Stripe Configuration for IMPULSE LEAN v1 - Phase 10
// Complete Stripe Connect setup and payment processing

import React, { useState, useEffect } from 'react'
import { loadStripe, StripeElementsOptions } from '@stripe/stripe-js'
import {
  Elements,
  CardElement,
  useStripe,
  useElements
} from '@stripe/react-stripe-js'
import { CreditCard, Lock, CheckCircle, AlertTriangle } from 'lucide-react'

// Initialize Stripe
const stripePromise = loadStripe(process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || 'pk_test_demo')

interface StripePaymentFormProps {
  amount: number // Amount in cents
  coachId: string
  sessionId: string
  onSuccess: (paymentIntentId: string) => void
  onError: (error: string) => void
}

// =============================================================================
// PAYMENT FORM COMPONENT
// =============================================================================

const StripePaymentForm: React.FC<StripePaymentFormProps> = ({
  amount,
  coachId,
  sessionId,
  onSuccess,
  onError
}) => {
  const elementsOptions: StripeElementsOptions = {
    appearance: {
      theme: 'stripe',
      variables: {
        colorPrimary: '#2563eb',
        colorBackground: '#ffffff',
        colorText: '#1f2937',
        colorDanger: '#dc2626',
        fontFamily: 'Inter, system-ui, sans-serif',
        spacingUnit: '4px',
        borderRadius: '6px'
      }
    }
  }

  return (
    <Elements stripe={stripePromise} options={elementsOptions}>
      <PaymentFormContent
        amount={amount}
        coachId={coachId}
        sessionId={sessionId}
        onSuccess={onSuccess}
        onError={onError}
      />
    </Elements>
  )
}

// =============================================================================
// PAYMENT FORM CONTENT
// =============================================================================

interface PaymentFormContentProps {
  amount: number
  coachId: string
  sessionId: string
  onSuccess: (paymentIntentId: string) => void
  onError: (error: string) => void
}

const PaymentFormContent: React.FC<PaymentFormContentProps> = ({
  amount,
  coachId,
  sessionId,
  onSuccess,
  onError
}) => {
  const stripe = useStripe()
  const elements = useElements()
  
  const [isProcessing, setIsProcessing] = useState(false)
  const [paymentSucceeded, setPaymentSucceeded] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [clientSecret, setClientSecret] = useState<string | null>(null)

  useEffect(() => {
    // Create payment intent when component mounts
    createPaymentIntent()
  }, [])

  const createPaymentIntent = async () => {
    try {
      const response = await fetch('/api/v1/payments/create-intent', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify({
          amount,
          coachId,
          sessionId,
          currency: 'usd'
        })
      })

      const data = await response.json()
      
      if (response.ok) {
        setClientSecret(data.clientSecret)
      } else {
        throw new Error(data.message || 'Failed to create payment intent')
      }
    } catch (err: any) {
      setError(err.message)
      onError(err.message)
    }
  }

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault()

    if (!stripe || !elements || !clientSecret) {
      return
    }

    setIsProcessing(true)
    setError(null)

    const cardElement = elements.getElement(CardElement)
    
    if (!cardElement) {
      setError('Card element not found')
      setIsProcessing(false)
      return
    }

    try {
      const { error: confirmError, paymentIntent } = await stripe.confirmCardPayment(
        clientSecret,
        {
          payment_method: {
            card: cardElement,
            billing_details: {
              name: 'Customer Name' // This should come from form data
            }
          }
        }
      )

      if (confirmError) {
        throw new Error(confirmError.message)
      }

      if (paymentIntent && paymentIntent.status === 'succeeded') {
        setPaymentSucceeded(true)
        onSuccess(paymentIntent.id)
      }
    } catch (err: any) {
      setError(err.message)
      onError(err.message)
    } finally {
      setIsProcessing(false)
    }
  }

  const cardElementOptions = {
    style: {
      base: {
        fontSize: '16px',
        color: '#424770',
        '::placeholder': {
          color: '#aab7c4',
        },
      },
      invalid: {
        color: '#9e2146',
      },
    },
  }

  if (paymentSucceeded) {
    return (
      <div className="text-center py-8">
        <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <CheckCircle className="w-8 h-8 text-green-600" />
        </div>
        <h3 className="text-lg font-medium text-gray-900 mb-2">Payment Successful!</h3>
        <p className="text-gray-600">Your coaching session has been booked and paid for.</p>
      </div>
    )
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Payment Amount */}
      <div className="bg-gray-50 rounded-lg p-4">
        <div className="flex justify-between items-center">
          <span className="text-sm font-medium text-gray-700">Total Amount</span>
          <span className="text-lg font-semibold text-gray-900">
            ${(amount / 100).toFixed(2)}
          </span>
        </div>
      </div>

      {/* Card Element */}
      <div className="space-y-2">
        <label className="block text-sm font-medium text-gray-700">
          Card Information
        </label>
        <div className="border border-gray-300 rounded-md p-3 bg-white">
          <CardElement options={cardElementOptions} />
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <div className="flex">
            <AlertTriangle className="w-5 h-5 text-red-400" />
            <div className="ml-3">
              <p className="text-sm text-red-800">{error}</p>
            </div>
          </div>
        </div>
      )}

      {/* Security Notice */}
      <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
        <div className="flex">
          <Lock className="w-5 h-5 text-blue-400" />
          <div className="ml-3">
            <p className="text-sm text-blue-800">
              Your payment information is securely processed by Stripe. 
              We never store your card details.
            </p>
          </div>
        </div>
      </div>

      {/* Submit Button */}
      <button
        type="submit"
        disabled={!stripe || isProcessing || !clientSecret}
        className="w-full flex items-center justify-center px-4 py-3 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <CreditCard className="w-5 h-5 mr-2" />
        {isProcessing ? 'Processing...' : `Pay $${(amount / 100).toFixed(2)}`}
      </button>
    </form>
  )
}

// =============================================================================
// STRIPE CONNECT ONBOARDING
// =============================================================================

interface StripeConnectOnboardingProps {
  coachId: string
  onComplete: () => void
}

const StripeConnectOnboarding: React.FC<StripeConnectOnboardingProps> = ({
  coachId,
  onComplete
}) => {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [onboardingUrl, setOnboardingUrl] = useState<string | null>(null)

  const startOnboarding = async () => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch('/api/v1/coaches/stripe/onboarding', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        },
        body: JSON.stringify({
          coachId,
          returnUrl: `${window.location.origin}/marketplace/onboarding-complete`,
          refreshUrl: window.location.href
        })
      })

      const data = await response.json()

      if (response.ok) {
        setOnboardingUrl(data.url)
        // Redirect to Stripe onboarding
        window.location.href = data.url
      } else {
        throw new Error(data.message || 'Failed to create onboarding session')
      }
    } catch (err: any) {
      setError(err.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="max-w-md mx-auto bg-white shadow-lg rounded-lg p-6">
      <div className="text-center mb-6">
        <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <CreditCard className="w-8 h-8 text-blue-600" />
        </div>
        <h2 className="text-xl font-semibold text-gray-900 mb-2">
          Set Up Payment Processing
        </h2>
        <p className="text-gray-600">
          Complete your Stripe account setup to start receiving payments from clients.
        </p>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-md p-4 mb-6">
          <div className="flex">
            <AlertTriangle className="w-5 h-5 text-red-400" />
            <div className="ml-3">
              <p className="text-sm text-red-800">{error}</p>
            </div>
          </div>
        </div>
      )}

      <div className="space-y-4 mb-6">
        <div className="flex items-start space-x-3">
          <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-gray-900">Secure & Trusted</p>
            <p className="text-sm text-gray-600">Powered by Stripe Connect</p>
          </div>
        </div>
        <div className="flex items-start space-x-3">
          <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-gray-900">Fast Payouts</p>
            <p className="text-sm text-gray-600">Receive payments in 2 business days</p>
          </div>
        </div>
        <div className="flex items-start space-x-3">
          <CheckCircle className="w-5 h-5 text-green-500 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-gray-900">Global Support</p>
            <p className="text-sm text-gray-600">Accept payments from anywhere</p>
          </div>
        </div>
      </div>

      <button
        onClick={startOnboarding}
        disabled={isLoading}
        className="w-full px-4 py-3 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {isLoading ? 'Setting up...' : 'Start Setup Process'}
      </button>

      <p className="text-xs text-gray-500 text-center mt-4">
        You'll be redirected to Stripe to complete the setup process.
        This is required to comply with financial regulations.
      </p>
    </div>
  )
}

// =============================================================================
// EARNINGS DASHBOARD
// =============================================================================

interface EarningsDashboardProps {
  coachId: string
}

const EarningsDashboard: React.FC<EarningsDashboardProps> = ({ coachId }) => {
  const [earnings, setEarnings] = useState<any>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadEarnings()
  }, [])

  const loadEarnings = async () => {
    try {
      const response = await fetch(`/api/v1/coaches/${coachId}/earnings`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      const data = await response.json()

      if (response.ok) {
        setEarnings(data)
      } else {
        throw new Error(data.message || 'Failed to load earnings')
      }
    } catch (err: any) {
      setError(err.message)
    } finally {
      setIsLoading(false)
    }
  }

  const openStripeDashboard = async () => {
    try {
      const response = await fetch(`/api/v1/coaches/${coachId}/stripe/dashboard`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      })

      const data = await response.json()

      if (response.ok) {
        window.open(data.url, '_blank')
      } else {
        throw new Error(data.message || 'Failed to open Stripe dashboard')
      }
    } catch (err: any) {
      console.error('Failed to open Stripe dashboard:', err)
    }
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-md p-4">
        <div className="flex">
          <AlertTriangle className="w-5 h-5 text-red-400" />
          <div className="ml-3">
            <p className="text-sm text-red-800">{error}</p>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <CreditCard className="h-8 w-8 text-green-600" />
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">
                  Total Earnings
                </dt>
                <dd className="text-2xl font-semibold text-gray-900">
                  ${earnings ? (earnings.totalEarnings / 100).toFixed(2) : '0.00'}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <CheckCircle className="h-8 w-8 text-blue-600" />
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">
                  Sessions Completed
                </dt>
                <dd className="text-2xl font-semibold text-gray-900">
                  {earnings?.sessionsCompleted || 0}
                </dd>
              </dl>
            </div>
          </div>
        </div>

        <div className="bg-white shadow rounded-lg p-6">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <CreditCard className="h-8 w-8 text-purple-600" />
            </div>
            <div className="ml-5 w-0 flex-1">
              <dl>
                <dt className="text-sm font-medium text-gray-500 truncate">
                  Pending Payouts
                </dt>
                <dd className="text-2xl font-semibold text-gray-900">
                  ${earnings ? (earnings.pendingPayouts / 100).toFixed(2) : '0.00'}
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </div>

      {/* Actions */}
      <div className="bg-white shadow rounded-lg p-6">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-medium text-gray-900">Stripe Dashboard</h3>
            <p className="text-sm text-gray-600">
              Manage your payouts, view detailed analytics, and update banking information.
            </p>
          </div>
          <button
            onClick={openStripeDashboard}
            className="px-4 py-2 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700"
          >
            Open Dashboard
          </button>
        </div>
      </div>
    </div>
  )
}

// =============================================================================
// EXPORTS
// =============================================================================

export {
  StripePaymentForm,
  StripeConnectOnboarding,
  EarningsDashboard
}

export default StripePaymentForm
