import React, { useState, useEffect, useCallback } from 'react';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { A11yHelper } from '../../components/A11yHelper';
import { NotificationCard } from '../../components/NotificationCard';
import { IdleState } from '../../components/IdleState';
import { AppButton } from '../../ui/AppButton';
import { AppTextField } from '../../ui/AppTextField';
import type { ApiErrorDto } from '../../types/api';

interface ValidationRequest {
	id: string;
	status: 'pending' | 'processing' | 'completed' | 'failed';
	createdAt: string;
	completedAt?: string;
	validationMethod: 'email' | 'sms' | 'document' | 'biometric';
	personalData: {
		name: string;
		email: string;
		phone?: string;
		documentType?: string;
		documentNumber?: string;
	};
}

interface ValidationMethod {
	id: string;
	name: string;
	description: string;
	available: boolean;
	estimatedTime: string;
}

const VALIDATION_METHODS: ValidationMethod[] = [
	{
		id: 'email',
		name: 'Validación por Email',
		description: 'Recibe un código de verificación en tu email',
		available: true,
		estimatedTime: '2-5 minutos'
	},
	{
		id: 'sms',
		name: 'Validación por SMS',
		description: 'Recibe un código de verificación por mensaje de texto',
		available: true,
		estimatedTime: '2-5 minutos'
	},
	{
		id: 'document',
		name: 'Validación por Documento',
		description: 'Sube una copia de tu documento de identidad',
		available: true,
		estimatedTime: '1-2 días'
	},
	{
		id: 'biometric',
		name: 'Validación Biométrica',
		description: 'Verificación facial o huella dactilar',
		available: false,
		estimatedTime: 'Próximamente'
	}
];

const VDP: React.FC = () => {
	const [isLoading, setIsLoading] = useState(false);
	const [error, setError] = useState<ApiErrorDto | null>(null);
	const [isIdle, setIsIdle] = useState(false);
	const [currentStep, setCurrentStep] = useState<'form' | 'method' | 'verification' | 'completed'>('form');
	const [selectedMethod, setSelectedMethod] = useState<string>('');
	const [validationRequest, setValidationRequest] = useState<ValidationRequest | null>(null);
	const [verificationCode, setVerificationCode] = useState('');
	const [isSubmitting, setIsSubmitting] = useState(false);

	// Form data
	const [formData, setFormData] = useState({
		name: '',
		email: '',
		phone: '',
		documentType: '',
		documentNumber: ''
	});

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const lastActivity = localStorage.getItem('lastActivity');
		if (lastActivity) {
			const timeSinceActivity = Date.now() - parseInt(lastActivity);
			setIsIdle(timeSinceActivity > idleThreshold);
		}
	}, []);

	const handleFormChange = useCallback((field: string, value: string) => {
		setFormData(prev => ({
			...prev,
			[field]: value
		}));
	}, []);

	const validateForm = useCallback(() => {
		if (!formData.name.trim()) {
			setError({
				code: 'VALIDATION_ERROR',
				message: 'El nombre es obligatorio',
				correlationId: `vdp-form-validation-${Date.now()}`
			});
			return false;
		}

		if (!formData.email.trim() || !formData.email.includes('@')) {
			setError({
				code: 'VALIDATION_ERROR',
				message: 'Email válido es obligatorio',
				correlationId: `vdp-form-validation-${Date.now()}`
			});
			return false;
		}

		if (selectedMethod === 'sms' && !formData.phone.trim()) {
			setError({
				code: 'VALIDATION_ERROR',
				message: 'El teléfono es obligatorio para validación por SMS',
				correlationId: `vdp-form-validation-${Date.now()}`
			});
			return false;
		}

		if (selectedMethod === 'document') {
			if (!formData.documentType.trim()) {
				setError({
					code: 'VALIDATION_ERROR',
					message: 'El tipo de documento es obligatorio',
					correlationId: `vdp-form-validation-${Date.now()}`
				});
				return false;
			}
			if (!formData.documentNumber.trim()) {
				setError({
					code: 'VALIDATION_ERROR',
					message: 'El número de documento es obligatorio',
					correlationId: `vdp-form-validation-${Date.now()}`
				});
				return false;
			}
		}

		return true;
	}, [formData, selectedMethod]);

	const handleStartValidation = useCallback(async () => {
		if (!validateForm()) return;

		try {
			setIsSubmitting(true);
			setError(null);

			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 1500));

			const newRequest: ValidationRequest = {
				id: `vdp-${Date.now()}`,
				status: 'pending',
				createdAt: new Date().toISOString(),
				validationMethod: selectedMethod as any,
				personalData: {
					name: formData.name,
					email: formData.email,
					phone: formData.phone || undefined,
					documentType: formData.documentType || undefined,
					documentNumber: formData.documentNumber || undefined
				}
			};

			setValidationRequest(newRequest);
			setCurrentStep('verification');

		} catch (err) {
			setError({
				code: 'VALIDATION_START_ERROR',
				message: 'Error iniciando la validación',
				correlationId: `vdp-start-${Date.now()}`
			});
		} finally {
			setIsSubmitting(false);
		}
	}, [formData, selectedMethod, validateForm]);

	const handleVerifyCode = useCallback(async () => {
		if (!verificationCode.trim()) {
			setError({
				code: 'VALIDATION_ERROR',
				message: 'El código de verificación es obligatorio',
				correlationId: `vdp-verify-${Date.now()}`
			});
			return;
		}

		try {
			setIsSubmitting(true);
			setError(null);

			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 2000));

			// Mock verification - in real app this would validate against backend
			const isValid = verificationCode === '123456';

			if (isValid && validationRequest) {
				const completedRequest: ValidationRequest = {
					...validationRequest,
					status: 'completed',
					completedAt: new Date().toISOString()
				};
				setValidationRequest(completedRequest);
				setCurrentStep('completed');
			} else {
				setError({
					code: 'INVALID_CODE',
					message: 'Código de verificación incorrecto',
					correlationId: `vdp-verify-${Date.now()}`
				});
			}

		} catch (err) {
			setError({
				code: 'VERIFICATION_ERROR',
				message: 'Error verificando el código',
				correlationId: `vdp-verify-${Date.now()}`
			});
		} finally {
			setIsSubmitting(false);
		}
	}, [verificationCode, validationRequest]);

	const handleResendCode = useCallback(async () => {
		try {
			setError(null);

			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 1000));

			// In real app, this would trigger sending a new code
			console.log('Resending verification code');

		} catch (err) {
			setError({
				code: 'RESEND_ERROR',
				message: 'Error reenviando el código',
				correlationId: `vdp-resend-${Date.now()}`
			});
		}
	}, []);

	const handleReset = useCallback(() => {
		setCurrentStep('form');
		setSelectedMethod('');
		setValidationRequest(null);
		setVerificationCode('');
		setFormData({
			name: '',
			email: '',
			phone: '',
			documentType: '',
			documentNumber: ''
		});
		setError(null);
	}, []);

	const getStatusColor = (status: string) => {
		const colors: Record<string, string> = {
			pending: 'bg-yellow-100 text-yellow-800',
			processing: 'bg-blue-100 text-blue-800',
			completed: 'bg-green-100 text-green-800',
			failed: 'bg-red-100 text-red-800'
		};
		return colors[status] || 'bg-gray-100 text-gray-800';
	};

	const getStatusLabel = (status: string) => {
		const labels: Record<string, string> = {
			pending: 'Pendiente',
			processing: 'Procesando',
			completed: 'Completada',
			failed: 'Fallida'
		};
		return labels[status] || status;
	};

	const formatDate = (dateString: string) => {
		return new Date(dateString).toLocaleDateString('es-ES', {
			year: 'numeric',
			month: 'long',
			day: 'numeric',
			hour: '2-digit',
			minute: '2-digit'
		});
	};

	// Idle state
	if (isIdle) {
		return <IdleState onResume={() => window.location.reload()} />;
	}

	// Loading state
	if (isLoading) {
		return (
			<div className="min-h-screen bg-gray-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					<div className="animate-pulse space-y-6">
						<div className="h-8 bg-gray-200 rounded w-1/3"></div>
						<div className="bg-white rounded-lg p-6">
							<div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
							<div className="space-y-4">
								{[...Array(3)].map((_, i) => (
									<div key={i} className="h-16 bg-gray-200 rounded"></div>
								))}
							</div>
						</div>
					</div>
				</div>
			</div>
		);
	}

	return (
		<ErrorBoundary>
			<A11yHelper />
			<div className="min-h-screen bg-gray-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					{/* Header */}
					<div className="mb-8">
						<h1 className="text-3xl font-bold text-gray-900 mb-2">
							Validación de Datos Personales (VDP)
						</h1>
						<p className="text-lg text-gray-600">
							Verifica tu identidad para acceder a servicios adicionales
						</p>
					</div>

					{/* Progress Indicator */}
					<div className="mb-8">
						<div className="flex items-center justify-between">
							{['form', 'method', 'verification', 'completed'].map((step, index) => (
								<div key={step} className="flex items-center">
									<div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
										currentStep === step ? 'bg-blue-600 text-white' :
										['form', 'method', 'verification', 'completed'].indexOf(currentStep) > index ? 'bg-green-600 text-white' :
										'bg-gray-200 text-gray-600'
									}`}>
										{index + 1}
									</div>
									{index < 3 && (
										<div className={`w-12 h-0.5 mx-2 ${
											['form', 'method', 'verification', 'completed'].indexOf(currentStep) > index ? 'bg-green-600' : 'bg-gray-200'
										}`} />
									)}
								</div>
							))}
						</div>
						<div className="flex justify-between mt-2 text-sm text-gray-600">
							<span>Datos personales</span>
							<span>Método</span>
							<span>Verificación</span>
							<span>Completado</span>
						</div>
					</div>

					{/* Step Content */}
					<div className="bg-white rounded-lg shadow-sm p-6">
						{/* Step 1: Personal Data Form */}
						{currentStep === 'form' && (
							<div>
								<h2 className="text-xl font-semibold text-gray-900 mb-6">
									Información Personal
								</h2>
								<div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
									<AppTextField
										label="Nombre completo"
										value={formData.name}
										onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFormChange('name', e.target.value)}
										placeholder="Tu nombre completo"
										fullWidth
										required
									/>
									<AppTextField
										label="Email"
										type="email"
										value={formData.email}
										onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFormChange('email', e.target.value)}
										placeholder="tu@email.com"
										fullWidth
										required
									/>
									<AppTextField
										label="Teléfono"
										type="tel"
										value={formData.phone}
										onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFormChange('phone', e.target.value)}
										placeholder="+34 600 000 000"
										fullWidth
									/>
									<div className="grid grid-cols-2 gap-2">
										<AppTextField
											label="Tipo documento"
											value={formData.documentType}
											onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFormChange('documentType', e.target.value)}
											placeholder="DNI/NIE"
											fullWidth
										/>
										<AppTextField
											label="Número"
											value={formData.documentNumber}
											onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleFormChange('documentNumber', e.target.value)}
											placeholder="12345678A"
											fullWidth
										/>
									</div>
								</div>
								<div className="flex justify-end">
									<AppButton
										onClick={() => setCurrentStep('method')}
										disabled={!formData.name || !formData.email}
									>
										Siguiente
									</AppButton>
								</div>
							</div>
						)}

						{/* Step 2: Validation Method Selection */}
						{currentStep === 'method' && (
							<div>
								<h2 className="text-xl font-semibold text-gray-900 mb-6">
									Selecciona Método de Validación
								</h2>
								<div className="space-y-4 mb-6">
									{VALIDATION_METHODS.map((method) => (
										<div
											key={method.id}
											className={`border rounded-lg p-4 cursor-pointer transition-colors ${
												selectedMethod === method.id ? 'border-blue-500 bg-blue-50' :
												method.available ? 'border-gray-200 hover:border-gray-300' : 'border-gray-200 opacity-50'
											}`}
											onClick={() => method.available && setSelectedMethod(method.id)}
										>
											<div className="flex items-center justify-between">
												<div>
													<h3 className="font-medium text-gray-900">{method.name}</h3>
													<p className="text-sm text-gray-600 mt-1">{method.description}</p>
													<p className="text-xs text-gray-500 mt-1">
														Tiempo estimado: {method.estimatedTime}
													</p>
												</div>
												<div className="flex items-center">
													{!method.available && (
														<span className="text-xs text-gray-400 mr-2">No disponible</span>
													)}
													<input
														type="radio"
														checked={selectedMethod === method.id}
														onChange={() => method.available && setSelectedMethod(method.id)}
														disabled={!method.available}
														className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
													/>
												</div>
											</div>
										</div>
									))}
								</div>
								<div className="flex justify-between">
									<AppButton variant="outline" onClick={() => setCurrentStep('form')}>
										Atrás
									</AppButton>
									<AppButton
										onClick={handleStartValidation}
										disabled={!selectedMethod || isSubmitting}
									>
										{isSubmitting ? 'Iniciando...' : 'Iniciar Validación'}
									</AppButton>
								</div>
							</div>
						)}

						{/* Step 3: Verification */}
						{currentStep === 'verification' && validationRequest && (
							<div>
								<h2 className="text-xl font-semibold text-gray-900 mb-6">
									Verificación de Identidad
								</h2>

								<div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
									<div className="flex items-start">
										<svg className="w-5 h-5 text-blue-400 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
											<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
										</svg>
										<div>
											<h3 className="text-blue-800 font-medium text-sm">
												Código enviado
											</h3>
											<p className="text-blue-700 text-sm mt-1">
												{selectedMethod === 'email' && `Hemos enviado un código de verificación a ${formData.email}`}
												{selectedMethod === 'sms' && `Hemos enviado un código de verificación por SMS a ${formData.phone}`}
												{selectedMethod === 'document' && 'Tu documento ha sido recibido y está siendo procesado'}
											</p>
										</div>
									</div>
								</div>

								{(selectedMethod === 'email' || selectedMethod === 'sms') && (
									<div className="mb-6">
										<AppTextField
											label="Código de verificación"
											value={verificationCode}
											onChange={(e: React.ChangeEvent<HTMLInputElement>) => setVerificationCode(e.target.value)}
											placeholder="Ingresa el código de 6 dígitos"
											fullWidth
											maxLength={6}
											required
										/>
										<p className="text-sm text-gray-500 mt-1">
											Ingresa el código que recibiste
										</p>
									</div>
								)}

								{selectedMethod === 'document' && (
									<div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6">
										<p className="text-yellow-800 text-sm">
											Tu documento está siendo revisado por nuestro equipo. Te notificaremos cuando se complete la validación.
										</p>
									</div>
								)}

								<div className="flex justify-between">
									<AppButton variant="outline" onClick={() => setCurrentStep('method')}>
										Atrás
									</AppButton>
									<div className="flex space-x-3">
										{(selectedMethod === 'email' || selectedMethod === 'sms') && (
											<AppButton variant="outline" onClick={handleResendCode}>
												Reenviar código
											</AppButton>
										)}
										<AppButton
											onClick={handleVerifyCode}
											disabled={isSubmitting || ((selectedMethod === 'email' || selectedMethod === 'sms') && !verificationCode)}
										>
											{isSubmitting ? 'Verificando...' : 'Verificar'}
										</AppButton>
									</div>
								</div>
							</div>
						)}

						{/* Step 4: Completed */}
						{currentStep === 'completed' && validationRequest && (
							<div>
								<div className="text-center mb-6">
									<div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
										<svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
										</svg>
									</div>
									<h2 className="text-xl font-semibold text-gray-900 mb-2">
										¡Validación Completada!
									</h2>
									<p className="text-gray-600">
										Tu identidad ha sido verificada exitosamente
									</p>
								</div>

								<div className="bg-gray-50 rounded-lg p-4 mb-6">
									<h3 className="font-medium text-gray-900 mb-2">Detalles de la validación</h3>
									<div className="text-sm text-gray-600 space-y-1">
										<p><strong>ID:</strong> {validationRequest.id}</p>
										<p><strong>Método:</strong> {VALIDATION_METHODS.find(m => m.id === validationRequest.validationMethod)?.name}</p>
										<p><strong>Completada:</strong> {formatDate(validationRequest.completedAt!)}</p>
									</div>
								</div>

								<div className="flex justify-center">
									<AppButton onClick={handleReset}>
										Validar otra identidad
									</AppButton>
								</div>
							</div>
						)}
					</div>

					{/* Error Message */}
					{error && (
						<div className="mt-8">
							<NotificationCard
								type="error"
								title="Error"
								message={error.message}
								action={{
									label: 'Reintentar',
									onClick: () => setError(null)
								}}
							/>
						</div>
					)}

					{/* Help Section */}
					<div className="mt-8 bg-gray-50 rounded-lg p-6">
						<h3 className="text-lg font-medium text-gray-900 mb-4">
							¿Necesitas ayuda?
						</h3>
						<div className="space-y-3 text-sm text-gray-600">
							<p>
								<strong>¿Por qué validar mi identidad?</strong><br />
								La validación nos ayuda a proteger tu cuenta y cumplir con regulaciones de seguridad.
							</p>
							<p>
								<strong>¿Qué información es necesaria?</strong><br />
								Solo recopilamos la información mínima necesaria para verificar tu identidad.
							</p>
							<p>
								<strong>¿Mis datos están seguros?</strong><br />
								Todos los datos se encriptan y se eliminan automáticamente después de la validación.
							</p>
							<p>
								<strong>¿Cuánto tiempo tarda?</strong><br />
								Los métodos por email/SMS son instantáneos, la validación por documento puede tomar 1-2 días.
							</p>
						</div>
					</div>
				</div>
			</div>
		</ErrorBoundary>
	);
};

export default VDP;
