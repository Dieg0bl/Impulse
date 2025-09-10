// Privacy Dashboard for IMPULSE LEAN v1 - Phase 9
// GDPR Data Subject Rights management interface

import React, { useState, useEffect } from 'react'
import { 
  Download, 
  Trash2, 
  Edit3, 
  Shield, 
  Eye, 
  FileText, 
  AlertTriangle, 
  CheckCircle,
  Clock,
  X
} from 'lucide-react'
import { useAuth } from '../services/store'
import { 
  useGDPRCompliance, 
  DataSubjectRequest, 
  DataSubjectRightType, 
  ConsentRecord 
} from '../services/compliance'

const PrivacyDashboard: React.FC = () => {
  const { user } = useAuth()
  const { submitDataRequest, exportUserData, complianceManager } = useGDPRCompliance()
  
  const [activeTab, setActiveTab] = useState<'consents' | 'requests' | 'data'>('consents')
  const [consents, setConsents] = useState<ConsentRecord[]>([])
  const [requests, setRequests] = useState<DataSubjectRequest[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [showRequestModal, setShowRequestModal] = useState(false)
  const [newRequestType, setNewRequestType] = useState<DataSubjectRightType>('ACCESS')

  useEffect(() => {
    if (user) {
      loadUserData()
    }
  }, [user])

  const loadUserData = async () => {
    if (!user) return
    
    setIsLoading(true)
    try {
      const [userConsents, userRequests] = await Promise.all([
        complianceManager.gdpr.getUserConsents(user.id),
        complianceManager.gdpr.getDataSubjectRequests(user.id)
      ])
      
      setConsents(userConsents)
      setRequests(userRequests)
    } catch (error) {
      console.error('Failed to load privacy data:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleRequestSubmission = async (type: DataSubjectRightType, description: string) => {
    if (!user) return

    try {
      await submitDataRequest({
        userId: user.id,
        requestType: type,
        description,
        verificationRequired: ['ERASURE', 'PORTABILITY'].includes(type),
        requestedBy: user.email
      })
      
      setShowRequestModal(false)
      loadUserData() // Refresh the data
    } catch (error) {
      console.error('Failed to submit request:', error)
    }
  }

  const handleDataExport = async () => {
    if (!user) return

    try {
      setIsLoading(true)
      const { downloadUrl } = await exportUserData(user.id)
      
      // Create download link
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `impulse-data-export-${user.id}.zip`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    } catch (error) {
      console.error('Failed to export data:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return <CheckCircle className="w-4 h-4 text-green-500" />
      case 'PROCESSING':
        return <Clock className="w-4 h-4 text-blue-500" />
      case 'REJECTED':
        return <X className="w-4 h-4 text-red-500" />
      default:
        return <Clock className="w-4 h-4 text-yellow-500" />
    }
  }

  const getRequestTypeDisplay = (type: DataSubjectRightType) => {
    switch (type) {
      case 'ACCESS':
        return 'Access My Data'
      case 'RECTIFICATION':
        return 'Correct My Data'
      case 'ERASURE':
        return 'Delete My Data'
      case 'RESTRICTION':
        return 'Restrict Processing'
      case 'PORTABILITY':
        return 'Export My Data'
      case 'OBJECTION':
        return 'Object to Processing'
      case 'AUTOMATED_DECISION':
        return 'Review Automated Decisions'
      default:
        return type
    }
  }

  if (!user) {
    return (
      <div className="max-w-4xl mx-auto py-8 px-4">
        <div className="text-center">
          <Shield className="mx-auto h-12 w-12 text-gray-400" />
          <h2 className="mt-2 text-lg font-medium text-gray-900">Access Required</h2>
          <p className="mt-1 text-sm text-gray-500">
            Please log in to access your privacy settings.
          </p>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-6xl mx-auto py-8 px-4">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Privacy Dashboard</h1>
        <p className="text-gray-600">
          Manage your data, privacy settings, and exercise your rights under GDPR.
        </p>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8">
          {[
            { id: 'consents', label: 'Consent Management', icon: Shield },
            { id: 'requests', label: 'Data Requests', icon: FileText },
            { id: 'data', label: 'My Data', icon: Eye }
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
      {activeTab === 'consents' && (
        <div className="space-y-6">
          <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200">
              <h2 className="text-lg font-medium text-gray-900">Consent Management</h2>
              <p className="text-sm text-gray-500">
                Review and manage your data processing consents.
              </p>
            </div>
            
            <div className="divide-y divide-gray-200">
              {consents.map((consent) => (
                <div key={consent.id} className="px-6 py-4">
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <h3 className="text-sm font-medium text-gray-900">
                        {consent.consentType.replace(/_/g, ' ').toLowerCase()}
                      </h3>
                      <p className="text-sm text-gray-500 mt-1">
                        {consent.purpose}
                      </p>
                      <div className="flex items-center mt-2 text-xs text-gray-400">
                        <span>Legal Basis: {consent.legalBasis}</span>
                        <span className="mx-2">•</span>
                        <span>
                          {consent.granted ? 'Granted' : 'Revoked'} on{' '}
                          {new Date(consent.grantedAt || consent.revokedAt || '').toLocaleDateString()}
                        </span>
                      </div>
                    </div>
                    
                    <div className="flex items-center">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        consent.granted
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {consent.granted ? 'Active' : 'Revoked'}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'requests' && (
        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-lg font-medium text-gray-900">Data Subject Requests</h2>
              <p className="text-sm text-gray-500">
                Exercise your rights under GDPR and track request status.
              </p>
            </div>
            <button
              onClick={() => setShowRequestModal(true)}
              className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700"
            >
              New Request
            </button>
          </div>

          <div className="bg-white shadow rounded-lg">
            <div className="divide-y divide-gray-200">
              {requests.length === 0 ? (
                <div className="px-6 py-8 text-center">
                  <FileText className="mx-auto h-8 w-8 text-gray-400" />
                  <p className="mt-2 text-sm text-gray-500">No requests submitted yet.</p>
                </div>
              ) : (
                requests.map((request) => (
                  <div key={request.id} className="px-6 py-4">
                    <div className="flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center">
                          {getStatusIcon(request.status)}
                          <h3 className="ml-2 text-sm font-medium text-gray-900">
                            {getRequestTypeDisplay(request.requestType)}
                          </h3>
                        </div>
                        <p className="text-sm text-gray-500 mt-1">
                          {request.description}
                        </p>
                        <div className="flex items-center mt-2 text-xs text-gray-400">
                          <span>Submitted: {new Date(request.submittedAt).toLocaleDateString()}</span>
                          {request.completedAt && (
                            <>
                              <span className="mx-2">•</span>
                              <span>Completed: {new Date(request.completedAt).toLocaleDateString()}</span>
                            </>
                          )}
                        </div>
                      </div>
                      
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        request.status === 'COMPLETED'
                          ? 'bg-green-100 text-green-800'
                          : request.status === 'REJECTED'
                          ? 'bg-red-100 text-red-800'
                          : 'bg-yellow-100 text-yellow-800'
                      }`}>
                        {request.status.replace(/_/g, ' ').toLowerCase()}
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'data' && (
        <div className="space-y-6">
          <div>
            <h2 className="text-lg font-medium text-gray-900">My Data</h2>
            <p className="text-sm text-gray-500">
              Access and export your personal data stored in our system.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Data Export */}
            <div className="bg-white shadow rounded-lg p-6">
              <div className="flex items-center mb-4">
                <Download className="w-6 h-6 text-blue-600" />
                <h3 className="ml-2 text-lg font-medium text-gray-900">Export My Data</h3>
              </div>
              <p className="text-sm text-gray-500 mb-4">
                Download a complete copy of your personal data in a portable format.
              </p>
              <button
                onClick={handleDataExport}
                disabled={isLoading}
                className="w-full px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {isLoading ? 'Preparing Export...' : 'Download My Data'}
              </button>
            </div>

            {/* Data Categories */}
            <div className="bg-white shadow rounded-lg p-6">
              <div className="flex items-center mb-4">
                <Eye className="w-6 h-6 text-green-600" />
                <h3 className="ml-2 text-lg font-medium text-gray-900">Data Categories</h3>
              </div>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Profile Information</span>
                  <span className="text-green-600">✓ Stored</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Activity Data</span>
                  <span className="text-green-600">✓ Stored</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Communication Preferences</span>
                  <span className="text-green-600">✓ Stored</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Analytics Data</span>
                  <span className="text-yellow-600">⚬ Anonymized</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Request Modal */}
      {showRequestModal && (
        <RequestModal
          onClose={() => setShowRequestModal(false)}
          onSubmit={handleRequestSubmission}
          requestType={newRequestType}
          onRequestTypeChange={setNewRequestType}
        />
      )}
    </div>
  )
}

// Request Modal Component
interface RequestModalProps {
  onClose: () => void
  onSubmit: (type: DataSubjectRightType, description: string) => Promise<void>
  requestType: DataSubjectRightType
  onRequestTypeChange: (type: DataSubjectRightType) => void
}

const RequestModal: React.FC<RequestModalProps> = ({
  onClose,
  onSubmit,
  requestType,
  onRequestTypeChange
}) => {
  const [description, setDescription] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!description.trim()) return

    setIsSubmitting(true)
    try {
      await onSubmit(requestType, description)
    } finally {
      setIsSubmitting(false)
    }
  }

  const requestTypes: { value: DataSubjectRightType; label: string; description: string }[] = [
    {
      value: 'ACCESS',
      label: 'Access My Data',
      description: 'Request access to your personal data we process'
    },
    {
      value: 'RECTIFICATION',
      label: 'Correct My Data',
      description: 'Request correction of inaccurate personal data'
    },
    {
      value: 'ERASURE',
      label: 'Delete My Data',
      description: 'Request deletion of your personal data'
    },
    {
      value: 'RESTRICTION',
      label: 'Restrict Processing',
      description: 'Request to restrict processing of your data'
    },
    {
      value: 'PORTABILITY',
      label: 'Export My Data',
      description: 'Request your data in a portable format'
    },
    {
      value: 'OBJECTION',
      label: 'Object to Processing',
      description: 'Object to processing based on legitimate interests'
    }
  ]

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-md w-full">
        <form onSubmit={handleSubmit}>
          <div className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Submit Data Request</h2>
              <button
                type="button"
                onClick={onClose}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Request Type
                </label>
                <select
                  value={requestType}
                  onChange={(e) => onRequestTypeChange(e.target.value as DataSubjectRightType)}
                  className="w-full border border-gray-300 rounded-md px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                >
                  {requestTypes.map((type) => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
                <p className="mt-1 text-xs text-gray-500">
                  {requestTypes.find(t => t.value === requestType)?.description}
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description
                </label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  rows={4}
                  className="w-full border border-gray-300 rounded-md px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Please provide details about your request..."
                  required
                />
              </div>

              {['ERASURE', 'PORTABILITY'].includes(requestType) && (
                <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3">
                  <div className="flex">
                    <AlertTriangle className="w-5 h-5 text-yellow-400" />
                    <div className="ml-3">
                      <p className="text-sm text-yellow-800">
                        This request requires identity verification. You may be contacted to confirm your identity.
                      </p>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className="px-6 py-4 bg-gray-50 border-t border-gray-200 flex justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-gray-700 border border-gray-300 rounded-md hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting || !description.trim()}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              {isSubmitting ? 'Submitting...' : 'Submit Request'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default PrivacyDashboard
