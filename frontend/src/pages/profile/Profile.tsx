
import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { useTelemetry } from '../../hooks/useTelemetry';
import { useRBAC } from '../../hooks/useRBAC';
import { Badge } from '../../ui/Badge';
import { Button } from '../../ui/Button';
import { Input } from '../../ui/Input';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { A11yHelper } from '../../components/A11yHelper';
import { DataState } from '../../components/DataState';
import { IdleState } from '../../components/IdleState';
import { NotificationCard } from '../../components/NotificationCard';
import { useApi } from '../../hooks/useApi';
import { useLocalStorage } from '../../hooks/useLocalStorage';
import type { UserProfile, ApiError } from '../../types/api';

interface ProfileForm {
	username: string;
	email: string;
	firstName: string;
	lastName: string;
	bio: string;
	phone?: string;
	location?: string;
	website?: string;
}

const Profile: React.FC = () => {
	const { user, updateProfile } = useAuth();
	const { trackEvent } = useTelemetry();
	const { hasPermission } = useRBAC();
	const { apiCall } = useApi();
	const [lastActivity] = useLocalStorage('lastActivity', Date.now());

	const [form, setForm] = useState<ProfileForm>({
		username: '',
		email: '',
		firstName: '',
		lastName: '',
		bio: '',
		phone: '',
		location: '',
		website: ''
	});

	const [loading, setLoading] = useState(true);
	const [saving, setSaving] = useState(false);
	const [error, setError] = useState<ApiError | null>(null);
	const [success, setSuccess] = useState<string | null>(null);
	const [isIdle, setIsIdle] = useState(false);

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const timeSinceActivity = Date.now() - lastActivity;
		setIsIdle(timeSinceActivity > idleThreshold);
	}, [lastActivity]);

	// Load user profile data
	useEffect(() => {
		const loadProfile = async () => {
			try {
				setLoading(true);
				setError(null);

				// Track page view
				trackEvent('profile_page_view', {
					userId: user?.id,
					timestamp: new Date().toISOString()
				});

				if (user) {
					setForm({
						username: user.username || '',
						email: user.email || '',
						firstName: user.firstName || '',
						lastName: user.lastName || '',
						bio: user.bio || '',
						phone: user.phone || '',
						location: user.location || '',
						website: user.website || ''
					});
				}
			} catch (err) {
				const apiError = err as ApiError;
				setError({
					code: apiError.code || 'PROFILE_LOAD_ERROR',
					message: apiError.message || 'Error cargando perfil',
					correlationId: apiError.correlationId || `profile-load-${Date.now()}`
				});

				trackEvent('profile_load_error', {
					error: apiError.message,
					correlationId: apiError.correlationId
				});
			} finally {
				setLoading(false);
			}
		};

		loadProfile();
	}, [user, trackEvent]);

	const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
		const { name, value } = e.target;
		setForm(prev => ({ ...prev, [name]: value }));

		// Clear success/error states on input change
		if (success) setSuccess(null);
		if (error) setError(null);
	}, [success, error]);

	const validateForm = useCallback((): string | null => {
		if (!form.username.trim()) return 'El nombre de usuario es requerido';
		if (!form.firstName.trim()) return 'El nombre es requerido';
		if (!form.lastName.trim()) return 'Los apellidos son requerido';
		if (form.username.length < 3) return 'El nombre de usuario debe tener al menos 3 caracteres';
		if (form.bio && form.bio.length > 500) return 'La bio no puede exceder 500 caracteres';

		// Email validation
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		if (form.email && !emailRegex.test(form.email)) return 'Email inválido';

		// Website validation
		if (form.website && form.website.trim()) {
			try {
				new URL(form.website);
			} catch {
				return 'URL del sitio web inválida';
			}
		}

		return null;
	}, [form]);

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();

		const validationError = validateForm();
		if (validationError) {
			setError({
				code: 'VALIDATION_ERROR',
				message: validationError,
				correlationId: `profile-validation-${Date.now()}`
			});
			return;
		}

		try {
			setSaving(true);
			setError(null);
			setSuccess(null);

			// Track form submission
			trackEvent('profile_update_attempt', {
				userId: user?.id,
				fields: Object.keys(form).filter(key => form[key as keyof ProfileForm])
			});

			const result = await apiCall<UserProfile>('/api/profile', {
				method: 'PUT',
				body: JSON.stringify(form)
			});

			// apiCall returns a wrapper { success, data, status }
			// extract payload explicitly before updating local user state
			const payload = (result && typeof result === 'object' && 'data' in result) ? result.data : result as unknown as UserProfile;
			await updateProfile(payload);

			setSuccess('Perfil actualizado exitosamente');

			trackEvent('profile_update_success', {
				userId: user?.id,
				timestamp: new Date().toISOString()
			});

		} catch (err) {
			const apiError = err as ApiError;
			setError({
				code: apiError.code || 'PROFILE_UPDATE_ERROR',
				message: apiError.message || 'Error actualizando perfil',
				correlationId: apiError.correlationId || `profile-update-${Date.now()}`
			});

			trackEvent('profile_update_error', {
				error: apiError.message,
				correlationId: apiError.correlationId
			});
		} finally {
			setSaving(false);
		}
	};

	const handleResendVerification = async () => {
		try {
			await apiCall('/api/auth/resend-verification', { method: 'POST' });
			setSuccess('Email de verificación enviado');

			trackEvent('verification_email_resent', {
				userId: user?.id
			});
		} catch (err) {
			const apiError = err as ApiError;
			setError({
				code: apiError.code || 'VERIFICATION_RESEND_ERROR',
				message: apiError.message || 'Error enviando email de verificación',
				correlationId: apiError.correlationId || `verification-resend-${Date.now()}`
			});
		}
	};

	// Loading state
	if (loading) {
		return (
			<div className="min-h-screen bg-gray-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					<div className="bg-white rounded-lg shadow-sm p-8">
						<div className="animate-pulse space-y-6">
							<div className="h-8 bg-gray-200 rounded w-1/3"></div>
							<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
								<div className="space-y-4">
									<div className="h-4 bg-gray-200 rounded w-1/4"></div>
									<div className="h-10 bg-gray-200 rounded"></div>
									<div className="h-4 bg-gray-200 rounded w-1/4"></div>
									<div className="h-10 bg-gray-200 rounded"></div>
								</div>
								<div className="space-y-4">
									<div className="h-4 bg-gray-200 rounded w-1/4"></div>
									<div className="h-10 bg-gray-200 rounded"></div>
									<div className="h-4 bg-gray-200 rounded w-1/4"></div>
									<div className="h-10 bg-gray-200 rounded"></div>
								</div>
							</div>
							<div className="h-32 bg-gray-200 rounded"></div>
							<div className="h-12 bg-gray-200 rounded w-1/3"></div>
						</div>
					</div>
				</div>
			</div>
		);
	}

	// Idle state
	if (isIdle) {
		return <IdleState onResume={() => window.location.reload()} />;
	}

	// Error state
	if (error && !user) {
		return (
			<div className="min-h-screen bg-gray-50 flex items-center justify-center">
				<div className="max-w-md mx-auto px-4">
					<NotificationCard
						type="error"
						title="Error de carga"
						message={error.message}
						action={{
							label: 'Reintentar',
							onClick: () => window.location.reload()
						}}
					/>
				</div>
			</div>
		);
	}

	// Empty state (no user data)
	if (!user) {
		return (
			<div className="min-h-screen bg-gray-50 flex items-center justify-center">
				<div className="max-w-md mx-auto px-4">
					<NotificationCard
						type="info"
						title="Perfil no disponible"
						message="No se pudo cargar la información del perfil"
						action={{
							label: 'Ir al inicio',
							onClick: () => window.location.href = '/'
						}}
					/>
				</div>
			</div>
		);
	}

	return (
		<ErrorBoundary>
			<A11yHelper />
			<div className="min-h-screen bg-gray-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					<div className="bg-white rounded-lg shadow-sm p-8">
						<div className="mb-8">
							<h1 className="text-3xl font-bold text-gray-900 mb-2">
								Mi perfil
							</h1>
							<p className="text-gray-600">
								Administra tu información personal y preferencias
							</p>
						</div>

						{/* Verification Status */}
						<div className="mb-8 p-4 bg-gray-50 rounded-lg">
							<div className="flex items-center justify-between">
								<div className="flex items-center gap-3">
									<span className="font-medium text-gray-900">Estado de verificación:</span>
									{user.isVerified ? (
										<Badge variant="success" className="flex items-center gap-2">
											<svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
												<path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
											</svg>
											Verificado
										</Badge>
									) : (
										<Badge variant="warning" className="flex items-center gap-2">
											<svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
												<path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
											</svg>
											Pendiente de verificación
										</Badge>
									)}
								</div>
								{!user.isVerified && (
									<Button
										variant="outline"
										size="sm"
										onClick={handleResendVerification}
										disabled={saving}
									>
										Reenviar email
									</Button>
								)}
							</div>
						</div>

						<form onSubmit={handleSubmit} className="space-y-8" aria-label="Editar perfil de usuario">
							{/* Basic Information */}
							<div>
								<h2 className="text-xl font-semibold text-gray-900 mb-4">
									Información básica
								</h2>
								<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
									<Input
										label="Nombre de usuario"
										name="username"
										value={form.username}
										onChange={handleChange}
										required
										aria-required="true"
										aria-describedby="username-help"
										placeholder="Tu nombre de usuario"
									/>
									<div id="username-help" className="sr-only">
										El nombre de usuario debe tener al menos 3 caracteres
									</div>

									<Input
										label="Email"
										name="email"
										value={form.email}
										disabled
										aria-disabled="true"
										aria-describedby="email-help"
									/>
									<div id="email-help" className="sr-only">
										El email no se puede modificar desde aquí
									</div>

									<Input
										label="Nombre"
										name="firstName"
										value={form.firstName}
										onChange={handleChange}
										required
										aria-required="true"
										placeholder="Tu nombre"
									/>

									<Input
										label="Apellidos"
										name="lastName"
										value={form.lastName}
										onChange={handleChange}
										required
										aria-required="true"
										placeholder="Tus apellidos"
									/>
								</div>
							</div>

							{/* Additional Information */}
							<div>
								<h2 className="text-xl font-semibold text-gray-900 mb-4">
									Información adicional
								</h2>
								<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
									<Input
										label="Teléfono"
										name="phone"
										value={form.phone || ''}
										onChange={handleChange}
										type="tel"
										placeholder="+34 600 000 000"
									/>

									<Input
										label="Ubicación"
										name="location"
										value={form.location || ''}
										onChange={handleChange}
										placeholder="Ciudad, País"
									/>

									<div className="md:col-span-2">
										<Input
											label="Sitio web"
											name="website"
											value={form.website || ''}
											onChange={handleChange}
											type="url"
											placeholder="https://tu-sitio-web.com"
										/>
									</div>
								</div>
							</div>

							{/* Bio */}
							<div>
								<label htmlFor="bio" className="block text-sm font-medium text-gray-700 mb-2">
									Biografía
									<span className="text-gray-500 text-xs ml-2">
										({form.bio?.length || 0}/500 caracteres)
									</span>
								</label>
								<textarea
									id="bio"
									name="bio"
									value={form.bio}
									onChange={handleChange}
									rows={4}
									maxLength={500}
									className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-vertical"
									placeholder="Cuéntanos un poco sobre ti..."
									aria-label="Biografía del usuario"
									aria-describedby="bio-help"
								/>
								<div id="bio-help" className="sr-only">
									Describe brevemente quién eres y qué te interesa
								</div>
							</div>

							{/* Actions */}
							<div className="flex flex-col sm:flex-row gap-4 pt-6 border-t border-gray-200">
								<Button
									type="submit"
									disabled={saving || !hasPermission('profile:update')}
									aria-busy={saving}
									className="flex-1 sm:flex-none"
								>
									{saving ? (
										<>
											<LoadingSpinner size="sm" className="mr-2" />
											Guardando...
										</>
									) : (
										'Guardar cambios'
									)}
								</Button>

								<Button
									type="button"
									variant="outline"
									onClick={() => window.history.back()}
									className="flex-1 sm:flex-none"
								>
									Cancelar
								</Button>
							</div>

							{/* Status Messages */}
							{success && (
								<div className="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg">
									<div className="flex items-center">
										<svg className="w-5 h-5 text-green-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
											<path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
										</svg>
										<span className="text-green-800 font-medium">{success}</span>
									</div>
								</div>
							)}

							{error && (
								<div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-lg" role="alert">
									<div className="flex items-center">
										<svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
											<path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
										</svg>
										<div>
											<h3 className="text-red-800 font-medium">Error</h3>
											<p className="text-red-700 text-sm mt-1">{error.message}</p>
											{error.correlationId && (
												<p className="text-red-600 text-xs mt-2">
													ID de correlación: {error.correlationId}
												</p>
											)}
										</div>
									</div>
								</div>
							)}
						</form>
					</div>
				</div>
			</div>
		</ErrorBoundary>
	);
};

export default Profile;
