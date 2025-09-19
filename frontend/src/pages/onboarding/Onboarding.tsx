import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useTelemetry } from '../../hooks/useTelemetry';
import { useRBAC } from '../../hooks/useRBAC';
import { useApi } from '../../hooks/useApi';
import { useLocalStorage } from '../../hooks/useLocalStorage';
import { Button } from '../../ui/Button';
import { Input } from '../../ui/Input';
// Badge unused
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { A11yHelper } from '../../components/A11yHelper';
import { NotificationCard } from '../../components/NotificationCard';
import { IdleState } from '../../components/IdleState';
import type { ApiError } from '../../types/api';

interface OnboardingStep {
	id: string;
	title: string;
	description: string;
	isCompleted: boolean;
	isRequired: boolean;
}

interface OnboardingData {
	basicInfo: {
		firstName: string;
		lastName: string;
		bio: string;
	};
	preferences: {
		emailNotifications: boolean;
		marketingEmails: boolean;
		language: string;
		timezone: string;
	};
	verification: {
		phone?: string;
		location?: string;
	};
}

const STEPS: OnboardingStep[] = [
	{
		id: 'welcome',
		title: '¡Bienvenido!',
		description: 'Comencemos con tu configuración inicial',
		isCompleted: false,
		isRequired: true
	},
	{
		id: 'basic-info',
		title: 'Información básica',
		description: 'Cuéntanos un poco sobre ti',
		isCompleted: false,
		isRequired: true
	},
	{
		id: 'preferences',
		title: 'Preferencias',
		description: 'Configura tus notificaciones y preferencias',
		isCompleted: false,
		isRequired: true
	},
	{
		id: 'verification',
		title: 'Verificación',
		description: 'Verifica tu cuenta para acceder a todas las funciones',
		isCompleted: false,
		isRequired: true
	},
	{
		id: 'complete',
		title: '¡Listo!',
		description: 'Tu cuenta está configurada y lista para usar',
		isCompleted: false,
		isRequired: false
	}
];

const Onboarding: React.FC = () => {
	const navigate = useNavigate();
	const { user, updateProfile } = useAuth();
	const { trackEvent } = useTelemetry();
	const { hasPermission } = useRBAC();
	const { apiCall } = useApi();
	const [lastActivity] = useLocalStorage('lastActivity', Date.now());

	const [currentStep, setCurrentStep] = useState(0);
	const [isLoading, setIsLoading] = useState(false);
	const [error, setError] = useState<ApiError | null>(null);
	const [isIdle, setIsIdle] = useState(false);
	const [onboardingData, setOnboardingData] = useState<OnboardingData>({
		basicInfo: {
			firstName: '',
			lastName: '',
			bio: ''
		},
		preferences: {
			emailNotifications: true,
			marketingEmails: false,
			language: 'es',
			timezone: 'Europe/Madrid'
		},
		verification: {
			phone: '',
			location: ''
		}
	});

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const timeSinceActivity = Date.now() - lastActivity;
		setIsIdle(timeSinceActivity > idleThreshold);
	}, [lastActivity]);

	// Load existing user data
	useEffect(() => {
		if (user) {
			setOnboardingData(prev => ({
				...prev,
				basicInfo: {
					firstName: user.firstName || '',
					lastName: user.lastName || '',
					bio: user.bio || ''
				}
			}));
		}

		// Track onboarding start
		trackEvent('onboarding_started', {
			userId: user?.id,
			timestamp: new Date().toISOString()
		});
	}, [user, trackEvent]);

	const handleInputChange = useCallback((section: keyof OnboardingData, field: string, value: any) => {
		setOnboardingData(prev => ({
			...prev,
			[section]: {
				...prev[section],
				[field]: value
			}
		}));
		setError(null);
	}, []);

	const validateStep = useCallback((stepIndex: number): string | null => {
		switch (stepIndex) {
					case 1: { // Basic Info
						const { firstName, lastName } = onboardingData.basicInfo;
						if (!firstName.trim()) return 'El nombre es requerido';
						if (!lastName.trim()) return 'Los apellidos son requeridos';
						if (firstName.length < 2) return 'El nombre debe tener al menos 2 caracteres';
						if (lastName.length < 2) return 'Los apellidos deben tener al menos 2 caracteres';
						break;
					}
					case 3: { // Verification
						const { phone } = onboardingData.verification;
						if (phone && phone.trim()) {
							const phoneRegex = /^\+?[0-9\s\-()]+$/;
							if (!phoneRegex.test(phone)) return 'Formato de teléfono inválido';
						}
						break;
					}
		}
		return null;
	}, [onboardingData]);

	const handleNext = async () => {
		const validationError = validateStep(currentStep);
		if (validationError) {
			setError({
				code: 'VALIDATION_ERROR',
				message: validationError,
				correlationId: `onboarding-step-${currentStep}-${Date.now()}`
			});
			return;
		}

		if (currentStep < STEPS.length - 1) {
			setCurrentStep(prev => prev + 1);

			trackEvent('onboarding_step_completed', {
				userId: user?.id,
				step: STEPS[currentStep].id,
				stepIndex: currentStep
			});
		} else {
			await handleComplete();
		}
	};

	const handlePrevious = () => {
		if (currentStep > 0) {
			setCurrentStep(prev => prev - 1);
		}
	};

	const handleComplete = async () => {
		try {
			setIsLoading(true);
			setError(null);

			// Save onboarding data
			await apiCall('/api/onboarding/complete', {
				method: 'POST',
				body: JSON.stringify({
					...onboardingData,
					completedAt: new Date().toISOString()
				})
			});

			// Update user profile with onboarding data
			await updateProfile({
				...user,
				...onboardingData.basicInfo,
				preferences: onboardingData.preferences,
				isOnboardingCompleted: true
			});

			trackEvent('onboarding_completed', {
				userId: user?.id,
				timestamp: new Date().toISOString()
			});

			// Redirect to dashboard
			navigate('/dashboard');

		} catch (err) {
			const apiError = err as ApiError;
			setError({
				code: apiError.code || 'ONBOARDING_COMPLETE_ERROR',
				message: apiError.message || 'Error completando el onboarding',
				correlationId: apiError.correlationId || `onboarding-complete-${Date.now()}`
			});

			trackEvent('onboarding_complete_error', {
				error: apiError.message,
				correlationId: apiError.correlationId
			});
		} finally {
			setIsLoading(false);
		}
	};

	const handleSkip = () => {
		trackEvent('onboarding_skipped', {
			userId: user?.id,
			step: STEPS[currentStep].id,
			stepIndex: currentStep
		});

		navigate('/dashboard');
	};

	// Idle state
	if (isIdle) {
		return <IdleState onResume={() => window.location.reload()} />;
	}

	const currentStepData = STEPS[currentStep];
	const progress = ((currentStep + 1) / STEPS.length) * 100;

	const buttonLabel = currentStep === STEPS.length - 1 ? 'Comenzar' : 'Siguiente';

	return (
		<ErrorBoundary>
			<A11yHelper />
			<div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
				<div className="max-w-4xl mx-auto px-4 py-8">
					{/* Header */}
					<div className="text-center mb-8">
						<h1 className="text-4xl font-bold text-gray-900 mb-2">
							Configuración inicial
						</h1>
						<p className="text-lg text-gray-600">
							Completa estos pasos para personalizar tu experiencia
						</p>
					</div>

					{/* Progress Bar */}
					<div className="bg-white rounded-lg shadow-sm p-6 mb-8">
						<div className="flex items-center justify-between mb-4">
							<span className="text-sm font-medium text-gray-700">
								Paso {currentStep + 1} de {STEPS.length}
							</span>
							<span className="text-sm text-gray-500">
								{progress.toFixed(0)}% completado
							</span>
						</div>
						<div className="w-full bg-gray-200 rounded-full h-2">
							<div
								className="bg-blue-600 h-2 rounded-full transition-all duration-300"
								style={{ width: `${progress}%` }}
							></div>
						</div>
					</div>

					{/* Step Content */}
					<div className="bg-white rounded-lg shadow-sm p-8 mb-8">
						{/* Step Header */}
						<div className="text-center mb-8">
							<h2 className="text-2xl font-bold text-gray-900 mb-2">
								{currentStepData.title}
							</h2>
							<p className="text-gray-600">
								{currentStepData.description}
							</p>
						</div>

						{/* Step Content */}
						{currentStep === 0 && (
							// Welcome Step
							<div className="text-center space-y-6">
								<div className="w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center mx-auto">
									<svg className="w-12 h-12 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
									</svg>
								</div>
								<div>
									<h3 className="text-xl font-semibold text-gray-900 mb-2">
										¡Hola, {user?.firstName || 'Usuario'}!
									</h3>
									<p className="text-gray-600">
										Estamos emocionados de tenerte aquí. Vamos a configurar tu cuenta
										para que puedas aprovechar al máximo todas las funciones disponibles.
									</p>
								</div>
							</div>
						)}

						{currentStep === 1 && (
							// Basic Info Step
							<div className="max-w-md mx-auto space-y-6">
								<Input
									label="Nombre"
									value={onboardingData.basicInfo.firstName}
									onChange={(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => handleInputChange('basicInfo', 'firstName', e.target.value)}
									required
									aria-required="true"
									placeholder="Tu nombre"
								/>
								<Input
									label="Apellidos"
									value={onboardingData.basicInfo.lastName}
									onChange={(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => handleInputChange('basicInfo', 'lastName', e.target.value)}
									required
									aria-required="true"
									placeholder="Tus apellidos"
								/>
								<div>
									<label htmlFor="bio" className="block text-sm font-medium text-gray-700 mb-2">
										Biografía (opcional)
									</label>
									<textarea
										id="bio"
										value={onboardingData.basicInfo.bio}
										onChange={(e) => handleInputChange('basicInfo', 'bio', e.target.value)}
										rows={3}
										maxLength={200}
										className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-vertical"
										placeholder="Cuéntanos un poco sobre ti..."
									/>
									<p className="text-xs text-gray-500 mt-1">
										{onboardingData.basicInfo.bio.length}/200 caracteres
									</p>
								</div>
							</div>
						)}

						{currentStep === 2 && (
							// Preferences Step
							<div className="max-w-md mx-auto space-y-6">
								<div>
									<h3 className="text-lg font-medium text-gray-900 mb-4">
										Notificaciones
									</h3>
									<div className="space-y-3">
										<label className="flex items-center">
											<input
												type="checkbox"
												checked={onboardingData.preferences.emailNotifications}
												onChange={(e) => handleInputChange('preferences', 'emailNotifications', e.target.checked)}
												className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
											/>
											<span className="ml-2 text-sm text-gray-700">
												Notificaciones por email
											</span>
										</label>
										<label className="flex items-center">
											<input
												type="checkbox"
												checked={onboardingData.preferences.marketingEmails}
												onChange={(e) => handleInputChange('preferences', 'marketingEmails', e.target.checked)}
												className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
											/>
											<span className="ml-2 text-sm text-gray-700">
												Emails de marketing y promociones
											</span>
										</label>
									</div>
								</div>

								<div>
									<label htmlFor="language" className="block text-sm font-medium text-gray-700 mb-2">
										Idioma
									</label>
									<select
										id="language"
										value={onboardingData.preferences.language}
										onChange={(e) => handleInputChange('preferences', 'language', e.target.value)}
										className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
									>
										<option value="es">Español</option>
										<option value="en">English</option>
										<option value="fr">Français</option>
										<option value="de">Deutsch</option>
									</select>
								</div>

								<div>
									<label htmlFor="timezone" className="block text-sm font-medium text-gray-700 mb-2">
										Zona horaria
									</label>
									<select
										id="timezone"
										value={onboardingData.preferences.timezone}
										onChange={(e) => handleInputChange('preferences', 'timezone', e.target.value)}
										className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
									>
										<option value="Europe/Madrid">Europe/Madrid (CET)</option>
										<option value="Europe/London">Europe/London (GMT)</option>
										<option value="America/New_York">America/New_York (EST)</option>
										<option value="America/Los_Angeles">America/Los_Angeles (PST)</option>
									</select>
								</div>
							</div>
						)}

						{currentStep === 3 && (
							// Verification Step
							<div className="max-w-md mx-auto space-y-6">
								<div className="text-center mb-6">
									<div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
										<svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
										</svg>
									</div>
									<h3 className="text-lg font-medium text-gray-900 mb-2">
										Verificación de cuenta
									</h3>
									<p className="text-gray-600 text-sm">
										Para acceder a todas las funciones, necesitamos verificar tu cuenta.
										Te hemos enviado un email de verificación.
									</p>
								</div>

								<div className="space-y-4">
									<Input
										label="Teléfono (opcional)"
										value={onboardingData.verification.phone || ''}
										onChange={(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => handleInputChange('verification', 'phone', e.target.value)}
										type="tel"
										placeholder="+34 600 000 000"
									/>
									<Input
										label="Ubicación (opcional)"
										value={onboardingData.verification.location || ''}
										onChange={(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => handleInputChange('verification', 'location', e.target.value)}
										placeholder="Ciudad, País"
									/>
								</div>

								<div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
									<div className="flex items-start">
										<svg className="w-5 h-5 text-blue-400 mt-0.5 mr-2" fill="currentColor" viewBox="0 0 20 20">
											<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
										</svg>
										<div>
											<h4 className="text-blue-800 font-medium text-sm">
												¿No has recibido el email?
											</h4>
											<p className="text-blue-700 text-sm mt-1">
												Revisa tu carpeta de spam o solicita un nuevo email de verificación
												en la configuración de tu perfil.
											</p>
										</div>
									</div>
								</div>
							</div>
						)}

						{currentStep === 4 && (
							// Complete Step
							<div className="text-center space-y-6">
								<div className="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto">
									<svg className="w-12 h-12 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
										<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
									</svg>
								</div>
								<div>
									<h3 className="text-xl font-semibold text-gray-900 mb-2">
										¡Todo listo!
									</h3>
									<p className="text-gray-600">
										Tu cuenta está completamente configurada. Ya puedes comenzar a explorar
										todas las funciones disponibles en la plataforma.
									</p>
								</div>
								<div className="bg-green-50 border border-green-200 rounded-lg p-4">
									<h4 className="text-green-800 font-medium mb-2">
										¿Qué puedes hacer ahora?
									</h4>
									<ul className="text-green-700 text-sm space-y-1 text-left">
										<li>• Crear y participar en desafíos</li>
										<li>• Gestionar tus evidencias y progreso</li>
										<li>• Conectar con otros usuarios</li>
										<li>• Acceder a funciones premium</li>
									</ul>
								</div>
							</div>
						)}
					</div>

					{/* Error Message */}
					{error && (
						<div className="mb-8">
							<NotificationCard
								type="error"
								title="Error"
								message={error.message}
								action={error.code === 'VALIDATION_ERROR' ? undefined : {
									label: 'Reintentar',
									onClick: () => setError(null)
								}}
							/>
						</div>
					)}

					{/* Navigation */}
					<div className="flex justify-between items-center">
						<Button
							variant="outline"
							onClick={handlePrevious}
							disabled={currentStep === 0 || isLoading}
						>
							Anterior
						</Button>

						<div className="flex space-x-4">
							{currentStep < STEPS.length - 1 && (
								<Button
									variant="ghost"
									onClick={handleSkip}
									disabled={isLoading}
								>
									Omitir
								</Button>
							)}

							<Button
								onClick={handleNext}
								disabled={isLoading || !hasPermission('onboarding:complete')}
							>
								{isLoading ? (
									<>
										<LoadingSpinner size="sm" className="mr-2" />
										{currentStep === STEPS.length - 1 ? 'Completando...' : buttonLabel}
									</>
								) : buttonLabel}
							</Button>
						</div>
					</div>

					{/* Step Indicators */}
					<div className="mt-8 flex justify-center">
						<div className="flex space-x-2">
							{STEPS.map((step, index) => (
								<div
									key={step.id}
									className={`w-3 h-3 rounded-full ${
										index <= currentStep ? 'bg-blue-600' : 'bg-gray-300'
									}`}
									aria-label={`Paso ${index + 1}: ${step.title}`}
								/>
							))}
						</div>
					</div>
				</div>
			</div>
		</ErrorBoundary>
	);
};

export default Onboarding;
