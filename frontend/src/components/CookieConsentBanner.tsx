// Cookie Consent Banner for IMPULSE LEAN v1 - Phase 9
// GDPR compliant cookie consent management

import React, { useState, useEffect } from 'react'
import { X, Settings, Shield, BarChart3, Target } from 'lucide-react'
import { useGDPRCompliance, ConsentType } from '../services/compliance'

interface CookieConsentProps {
  onConsentChange?: (consents: Record<ConsentType, boolean>) => void
}

interface ConsentSettings {
  essential: boolean
  functional: boolean
  analytics: boolean
  marketing: boolean
}

const CookieConsentBanner: React.FC<CookieConsentProps> = ({ onConsentChange }) => {
  const [showBanner, setShowBanner] = useState(false)
  const [showSettings, setShowSettings] = useState(false)
  const [consents, setConsents] = useState<ConsentSettings>({
    essential: true, // Always required
    functional: false,
    analytics: false,
    marketing: false
  })

  const { recordConsent } = useGDPRCompliance()

  useEffect(() => {
    // Check if user has already provided consent
    const hasConsent = localStorage.getItem('cookie-consent')
    if (!hasConsent) {
      setShowBanner(true)
    }
  }, [])

  const handleAcceptAll = async () => {
    const newConsents = {
      essential: true,
      functional: true,
      analytics: true,
      marketing: true
    }
    
    await saveConsents(newConsents)
    setShowBanner(false)
  }

  const handleRejectAll = async () => {
    const newConsents = {
      essential: true, // Always required
      functional: false,
      analytics: false,
      marketing: false
    }
    
    await saveConsents(newConsents)
    setShowBanner(false)
  }

  const handleSaveSettings = async () => {
    await saveConsents(consents)
    setShowSettings(false)
    setShowBanner(false)
  }

  const saveConsents = async (consentSettings: ConsentSettings) => {
    const userAgent = navigator.userAgent
    const ipAddress = 'auto-detected' // Would be detected by backend
    
    try {
      // Record each consent type
      const consentRecords = [
        {
          consentType: 'COOKIES_FUNCTIONAL' as ConsentType,
          granted: consentSettings.functional,
          purpose: 'Website functionality and user preferences',
          legalBasis: 'CONSENT' as const,
          dataCategories: ['TECHNICAL_DATA' as const],
          retentionPeriod: 365,
          source: 'COOKIE_BANNER' as const,
          ipAddress,
          userAgent,
          version: '1.0'
        },
        {
          consentType: 'COOKIES_ANALYTICS' as ConsentType,
          granted: consentSettings.analytics,
          purpose: 'Website analytics and performance monitoring',
          legalBasis: 'CONSENT' as const,
          dataCategories: ['BEHAVIORAL_DATA' as const, 'TECHNICAL_DATA' as const],
          retentionPeriod: 730,
          source: 'COOKIE_BANNER' as const,
          ipAddress,
          userAgent,
          version: '1.0'
        },
        {
          consentType: 'COOKIES_MARKETING' as ConsentType,
          granted: consentSettings.marketing,
          purpose: 'Personalized advertising and marketing communications',
          legalBasis: 'CONSENT' as const,
          dataCategories: ['BEHAVIORAL_DATA' as const, 'CONTACT_INFORMATION' as const],
          retentionPeriod: 730,
          source: 'COOKIE_BANNER' as const,
          ipAddress,
          userAgent,
          version: '1.0'
        }
      ]

      for (const consent of consentRecords) {
        await recordConsent(consent)
      }

      // Save to localStorage
      localStorage.setItem('cookie-consent', JSON.stringify({
        ...consentSettings,
        timestamp: new Date().toISOString()
      }))

      // Notify parent component
      onConsentChange?.({
        COOKIES_FUNCTIONAL: consentSettings.functional,
        COOKIES_ANALYTICS: consentSettings.analytics,
        COOKIES_MARKETING: consentSettings.marketing,
        COOKIES_ANALYTICS: consentSettings.analytics,
        DATA_PROCESSING: true, // Always true for registered users
        MARKETING_COMMUNICATIONS: consentSettings.marketing,
        PROFILE_VISIBILITY: false, // Separate consent
        DATA_SHARING: false, // Separate consent
        AUTOMATED_DECISION_MAKING: false // Separate consent
      })

    } catch (error) {
      console.error('Failed to record cookie consent:', error)
    }
  }

  const toggleConsent = (type: keyof ConsentSettings) => {
    if (type === 'essential') return // Cannot be disabled
    
    setConsents(prev => ({
      ...prev,
      [type]: !prev[type]
    }))
  }

  if (!showBanner) return null

  return (
    <>
      {/* Cookie Banner */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 shadow-lg z-50">
        <div className="max-w-7xl mx-auto p-4">
          <div className="flex items-start justify-between gap-4">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <Shield className="w-5 h-5 text-blue-600" />
                <h3 className="font-semibold text-gray-900">Cookie Consent</h3>
              </div>
              <p className="text-sm text-gray-600 mb-3">
                We use cookies to enhance your experience, analyze site usage, and provide personalized content. 
                You can manage your preferences or learn more in our{' '}
                <a href="/privacy-policy" className="text-blue-600 hover:text-blue-700 underline">
                  Privacy Policy
                </a>.
              </p>
              
              <div className="flex flex-wrap gap-2">
                <button
                  onClick={handleAcceptAll}
                  className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  Accept All
                </button>
                <button
                  onClick={handleRejectAll}
                  className="px-4 py-2 bg-gray-600 text-white text-sm font-medium rounded-md hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-gray-500"
                >
                  Reject All
                </button>
                <button
                  onClick={() => setShowSettings(true)}
                  className="px-4 py-2 border border-gray-300 text-gray-700 text-sm font-medium rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <Settings className="w-4 h-4 inline mr-1" />
                  Customize
                </button>
              </div>
            </div>
            
            <button
              onClick={() => setShowBanner(false)}
              className="text-gray-400 hover:text-gray-600"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      {/* Cookie Settings Modal */}
      {showSettings && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-semibold text-gray-900">Cookie Settings</h2>
                <button
                  onClick={() => setShowSettings(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              <div className="space-y-6">
                {/* Essential Cookies */}
                <div className="border border-gray-200 rounded-lg p-4">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <Shield className="w-5 h-5 text-green-600" />
                      <h3 className="font-medium text-gray-900">Essential Cookies</h3>
                      <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">Always Active</span>
                    </div>
                  </div>
                  <p className="text-sm text-gray-600">
                    These cookies are necessary for the website to function and cannot be switched off. 
                    They are usually only set in response to actions made by you which amount to a request for services.
                  </p>
                </div>

                {/* Functional Cookies */}
                <div className="border border-gray-200 rounded-lg p-4">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <Settings className="w-5 h-5 text-blue-600" />
                      <h3 className="font-medium text-gray-900">Functional Cookies</h3>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={consents.functional}
                        onChange={() => toggleConsent('functional')}
                        className="sr-only peer"
                      />
                      <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                    </label>
                  </div>
                  <p className="text-sm text-gray-600">
                    These cookies enable enhanced functionality and personalization, such as remembering your preferences and settings.
                  </p>
                </div>

                {/* Analytics Cookies */}
                <div className="border border-gray-200 rounded-lg p-4">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <BarChart3 className="w-5 h-5 text-purple-600" />
                      <h3 className="font-medium text-gray-900">Analytics Cookies</h3>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={consents.analytics}
                        onChange={() => toggleConsent('analytics')}
                        className="sr-only peer"
                      />
                      <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                    </label>
                  </div>
                  <p className="text-sm text-gray-600">
                    These cookies help us understand how visitors interact with our website by collecting and reporting information anonymously.
                  </p>
                </div>

                {/* Marketing Cookies */}
                <div className="border border-gray-200 rounded-lg p-4">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <Target className="w-5 h-5 text-orange-600" />
                      <h3 className="font-medium text-gray-900">Marketing Cookies</h3>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={consents.marketing}
                        onChange={() => toggleConsent('marketing')}
                        className="sr-only peer"
                      />
                      <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                    </label>
                  </div>
                  <p className="text-sm text-gray-600">
                    These cookies are used to deliver advertisements more relevant to you and your interests. 
                    They may also be used to limit the number of times you see an advertisement.
                  </p>
                </div>
              </div>

              <div className="flex justify-end gap-3 mt-6 pt-6 border-t border-gray-200">
                <button
                  onClick={() => setShowSettings(false)}
                  className="px-4 py-2 text-gray-700 border border-gray-300 rounded-md hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  onClick={handleSaveSettings}
                  className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                >
                  Save Preferences
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

export default CookieConsentBanner
