// Booking Form Component for IMPULSE LEAN v1 - Phase 10
// Complete booking interface with Stripe payment integration

import React, { useState, useEffect } from 'react'
import { 
  Calendar, 
  Clock, 
  DollarSign, 
  User, 
  CreditCard,
  CheckCircle,
  AlertCircle,
  Loader
} from 'lucide-react'
import { useCoachMarketplace, Coach, SessionType } from '../services/coachMarketplace'
import { useAuth } from '../services/store'

interface BookingFormProps {
  coach: Coach
  sessionType?: SessionType
  onSuccess?: (bookingId: string) => void
  onCancel?: () => void
}

const BookingForm: React.FC<BookingFormProps> = ({ 
  coach, 
  sessionType, 
  onSuccess, 
  onCancel 
}) => {
  const { user } = useAuth()
  const { createBooking, getAvailableSlots } = useCoachMarketplace()
  
  const [step, setStep] = useState<'details' | 'payment' | 'confirmation'>('details')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  
  const [selectedSessionType, setSelectedSessionType] = useState<SessionType | null>(
    sessionType || null
  )
  const [selectedDate, setSelectedDate] = useState<string>('')
  const [selectedTime, setSelectedTime] = useState<string>('')
  const [availableSlots, setAvailableSlots] = useState<string[]>([])
  const [bookingData, setBookingData] = useState({
    notes: '',
    goals: '',
    contactPreference: 'email' as 'email' | 'phone'
  })
  
  const [paymentData, setPaymentData] = useState({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    name: '',
    email: user?.email || '',
    phone: ''
  })

  useEffect(() => {
    if (selectedDate && selectedSessionType) {
      loadAvailableSlots()
    }
  }, [selectedDate, selectedSessionType])

  const loadAvailableSlots = async () => {
    try {
      const slots = await getAvailableSlots(
        coach.id, 
        selectedDate, 
        selectedSessionType!.duration
      )
      setAvailableSlots(slots)
    } catch (err) {
      console.error('Failed to load available slots:', err)
    }
  }

  const handleBookingSubmit = async () => {
    if (!selectedSessionType || !selectedDate || !selectedTime) {
      setError('Please select a session type, date, and time')
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      const scheduledAt = new Date(`${selectedDate}T${selectedTime}`)
      
      const booking = await createBooking({
        coachId: coach.id,
        sessionTypeId: selectedSessionType.id,
        scheduledAt: scheduledAt.toISOString(),
        notes: bookingData.notes,
        goals: bookingData.goals,
        clientContactInfo: {
          email: paymentData.email,
          phone: paymentData.phone,
          preference: bookingData.contactPreference
        }
      })

      if (onSuccess) {
        onSuccess(booking.id)
      } else {
        setStep('confirmation')
      }
    } catch (err: any) {
      setError(err.message || 'Failed to create booking')
    } finally {
      setIsLoading(false)
    }
  }

  const formatPrice = (cents: number) => {
    return `$${(cents / 100).toFixed(2)}`
  }

  const generateTimeSlots = () => {
    const slots = []
    for (let hour = 9; hour <= 17; hour++) {
      for (let minute of [0, 30]) {
        const time = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`
        slots.push(time)
      }
    }
    return slots
  }

  const getMinDate = () => {
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    return tomorrow.toISOString().split('T')[0]
  }

  return (
    <div className="max-w-2xl mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
      {/* Header */}
      <div className="bg-blue-600 text-white px-6 py-4">
        <h2 className="text-xl font-semibold">Book a Session</h2>
        <p className="text-blue-100 mt-1">with {coach.businessName}</p>
      </div>

      {/* Progress Indicator */}
      <div className="px-6 py-4 border-b border-gray-200">
        <div className="flex items-center space-x-4">
          {[
            { id: 'details', label: 'Session Details', icon: Calendar },
            { id: 'payment', label: 'Payment', icon: CreditCard },
            { id: 'confirmation', label: 'Confirmation', icon: CheckCircle }
          ].map(({ id, label, icon: Icon }, index) => (
            <div key={id} className="flex items-center">
              <div className={`flex items-center justify-center w-8 h-8 rounded-full border-2 ${
                step === id 
                  ? 'border-blue-500 bg-blue-500 text-white' 
                  : index < ['details', 'payment', 'confirmation'].indexOf(step)
                  ? 'border-green-500 bg-green-500 text-white'
                  : 'border-gray-300 text-gray-400'
              }`}>
                <Icon className="w-4 h-4" />
              </div>
              <span className={`ml-2 text-sm font-medium ${
                step === id ? 'text-blue-600' : 'text-gray-500'
              }`}>
                {label}
              </span>
              {index < 2 && (
                <div className={`w-8 h-0.5 mx-4 ${
                  index < ['details', 'payment', 'confirmation'].indexOf(step)
                    ? 'bg-green-500'
                    : 'bg-gray-300'
                }`} />
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Content */}
      <div className="px-6 py-6">
        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
            <div className="flex">
              <AlertCircle className="w-5 h-5 text-red-400" />
              <div className="ml-3">
                <p className="text-sm text-red-800">{error}</p>
              </div>
            </div>
          </div>
        )}

        {step === 'details' && (
          <div className="space-y-6">
            {/* Session Type Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Choose Session Type
              </label>
              <div className="grid gap-3">
                {coach.pricing.sessionTypes.map((sessionTypeOption) => (
                  <div
                    key={sessionTypeOption.id}
                    className={`border rounded-lg p-4 cursor-pointer transition-colors ${
                      selectedSessionType?.id === sessionTypeOption.id
                        ? 'border-blue-500 bg-blue-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                    onClick={() => setSelectedSessionType(sessionTypeOption)}
                  >
                    <div className="flex justify-between items-start">
                      <div>
                        <h4 className="font-medium text-gray-900">{sessionTypeOption.name}</h4>
                        <p className="text-sm text-gray-600 mt-1">{sessionTypeOption.description}</p>
                        <div className="flex items-center mt-2 text-sm text-gray-500">
                          <Clock className="w-4 h-4 mr-1" />
                          {sessionTypeOption.duration} minutes
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="text-lg font-semibold text-gray-900">
                          {formatPrice(sessionTypeOption.price)}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Date Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Select Date
              </label>
              <input
                type="date"
                min={getMinDate()}
                value={selectedDate}
                onChange={(e) => setSelectedDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            {/* Time Selection */}
            {selectedDate && selectedSessionType && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Select Time
                </label>
                <div className="grid grid-cols-4 gap-2">
                  {generateTimeSlots().map((time) => (
                    <button
                      key={time}
                      onClick={() => setSelectedTime(time)}
                      disabled={!availableSlots.includes(time)}
                      className={`px-3 py-2 text-sm rounded-md border ${
                        selectedTime === time
                          ? 'border-blue-500 bg-blue-500 text-white'
                          : availableSlots.includes(time)
                          ? 'border-gray-300 hover:border-blue-300 hover:bg-blue-50'
                          : 'border-gray-200 bg-gray-100 text-gray-400 cursor-not-allowed'
                      }`}
                    >
                      {time}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Session Notes */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Session Goals & Notes (Optional)
              </label>
              <textarea
                value={bookingData.notes}
                onChange={(e) => setBookingData({ ...bookingData, notes: e.target.value })}
                rows={3}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="What would you like to focus on in this session?"
              />
            </div>

            {/* Contact Information */}
            <div className="space-y-4">
              <h3 className="text-sm font-medium text-gray-700">Contact Information</h3>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email
                </label>
                <input
                  type="email"
                  value={paymentData.email}
                  onChange={(e) => setPaymentData({ ...paymentData, email: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Phone (Optional)
                </label>
                <input
                  type="tel"
                  value={paymentData.phone}
                  onChange={(e) => setPaymentData({ ...paymentData, phone: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
            </div>
          </div>
        )}

        {step === 'payment' && (
          <div className="space-y-6">
            {/* Booking Summary */}
            <div className="bg-gray-50 rounded-lg p-4">
              <h3 className="font-medium text-gray-900 mb-3">Booking Summary</h3>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Coach:</span>
                  <span className="font-medium">{coach.businessName}</span>
                </div>
                <div className="flex justify-between">
                  <span>Session:</span>
                  <span className="font-medium">{selectedSessionType?.name}</span>
                </div>
                <div className="flex justify-between">
                  <span>Date & Time:</span>
                  <span className="font-medium">
                    {selectedDate && selectedTime 
                      ? `${new Date(selectedDate).toLocaleDateString()} at ${selectedTime}`
                      : 'Not selected'
                    }
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>Duration:</span>
                  <span className="font-medium">{selectedSessionType?.duration} minutes</span>
                </div>
                <div className="border-t pt-2 mt-2">
                  <div className="flex justify-between font-semibold">
                    <span>Total:</span>
                    <span>{selectedSessionType ? formatPrice(selectedSessionType.price) : '$0.00'}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Payment Form */}
            <div className="space-y-4">
              <h3 className="font-medium text-gray-900">Payment Information</h3>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Cardholder Name
                </label>
                <input
                  type="text"
                  value={paymentData.name}
                  onChange={(e) => setPaymentData({ ...paymentData, name: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Card Number
                </label>
                <input
                  type="text"
                  value={paymentData.cardNumber}
                  onChange={(e) => setPaymentData({ ...paymentData, cardNumber: e.target.value })}
                  placeholder="1234 5678 9012 3456"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Expiry Date
                  </label>
                  <input
                    type="text"
                    value={paymentData.expiryDate}
                    onChange={(e) => setPaymentData({ ...paymentData, expiryDate: e.target.value })}
                    placeholder="MM/YY"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    CVV
                  </label>
                  <input
                    type="text"
                    value={paymentData.cvv}
                    onChange={(e) => setPaymentData({ ...paymentData, cvv: e.target.value })}
                    placeholder="123"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                    required
                  />
                </div>
              </div>
            </div>

            {/* Security Notice */}
            <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
              <div className="flex">
                <div className="flex-shrink-0">
                  <div className="w-6 h-6 bg-blue-600 rounded-full flex items-center justify-center">
                    <div className="w-3 h-3 bg-white rounded-full" />
                  </div>
                </div>
                <div className="ml-3">
                  <p className="text-sm text-blue-800">
                    Your payment information is securely processed by Stripe. 
                    We do not store your card details.
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}

        {step === 'confirmation' && (
          <div className="text-center space-y-6">
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto">
              <CheckCircle className="w-8 h-8 text-green-600" />
            </div>
            
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                Booking Confirmed!
              </h3>
              <p className="text-gray-600">
                Your coaching session has been successfully booked. 
                You will receive a confirmation email shortly.
              </p>
            </div>

            <div className="bg-gray-50 rounded-lg p-4 text-left">
              <h4 className="font-medium text-gray-900 mb-2">Session Details</h4>
              <div className="space-y-1 text-sm text-gray-600">
                <p><strong>Coach:</strong> {coach.businessName}</p>
                <p><strong>Session:</strong> {selectedSessionType?.name}</p>
                <p><strong>Date & Time:</strong> {selectedDate && selectedTime 
                  ? `${new Date(selectedDate).toLocaleDateString()} at ${selectedTime}`
                  : 'Not available'
                }</p>
                <p><strong>Duration:</strong> {selectedSessionType?.duration} minutes</p>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Footer Actions */}
      <div className="px-6 py-4 bg-gray-50 border-t border-gray-200">
        <div className="flex justify-between items-center">
          {step !== 'confirmation' && (
            <>
              <button
                onClick={onCancel}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
              >
                Cancel
              </button>
              
              <div className="flex space-x-3">
                {step === 'payment' && (
                  <button
                    onClick={() => setStep('details')}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50"
                  >
                    Back
                  </button>
                )}
                
                <button
                  onClick={() => {
                    if (step === 'details') {
                      setStep('payment')
                    } else if (step === 'payment') {
                      handleBookingSubmit()
                    }
                  }}
                  disabled={isLoading || (
                    step === 'details' && (!selectedSessionType || !selectedDate || !selectedTime)
                  )}
                  className="flex items-center px-6 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {isLoading && <Loader className="w-4 h-4 mr-2 animate-spin" />}
                  {step === 'details' ? 'Continue to Payment' : 'Complete Booking'}
                </button>
              </div>
            </>
          )}
          
          {step === 'confirmation' && (
            <button
              onClick={onCancel}
              className="w-full px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700"
            >
              Close
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

export default BookingForm
