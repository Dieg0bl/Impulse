import React, { useState, useEffect, useCallback } from 'react';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { A11yHelper } from '../../components/A11yHelper';
import { NotificationCard } from '../../components/NotificationCard';
import { IdleState } from '../../components/IdleState';
import { AppButton } from '../../ui/AppButton';
import { AppTextField } from '../../ui/AppTextField';
import type { ApiErrorDto } from '../../types/api';

interface DSARDeleteRequest {
	id: string;
	requestType: 'delete';
	status: 'pending' | 'processing' | 'completed' | 'failed' | 'cancelled';
	createdAt: string;
	estimatedCompletion: string;
	completedAt?: string;
	selectedDataTypes: string[];
	reason: string;
	confirmations: {
		understandIrreversible: boolean;
		backupData: boolean;
		contactSupport: boolean;
	};
}

interface DataType {
	id: string;
	name: string;
	description: string;
	critical: boolean;
	selected: boolean;
}

const AVAILABLE_DATA_TYPES: DataType[] = [
	{
		id: 'profile',
		name: 'Datos de perfil',
		description: 'Nombre, email, fecha de nacimiento, preferencias',
		critical: true,
		selected: false
	},
	{
		id: 'challenges',
		name: 'Historial de desafíos',
		description: 'Desafíos completados, puntuaciones, fechas',
		critical: false,
		selected: false
	},
	{
		id: 'referrals',
		name: 'Sistema de referidos',
		description: 'Códigos de referido, recompensas, estadísticas',
		critical: false,
		selected: false
	},
	{
		id: 'activity',
		name: 'Actividad de la aplicación',
		description: 'Logs de acceso, sesiones, interacciones',
		critical: false,
		selected: false
	},
	{
		id: 'communications',
		name: 'Comunicaciones',
		description: 'Emails enviados, notificaciones push, mensajes',
		critical: false,
		selected: false
	},
	{
		id: 'payments',
		name: 'Información de pagos',
		description: 'Historial de transacciones, métodos de pago',
		critical: true,
		selected: false
	}
];

const DSARDelete: React.FC = () => {
	const [isLoading, setIsLoading] = useState(false);
	const [error, setError] = useState<ApiErrorDto | null>(null);
	const [isIdle, setIsIdle] = useState(false);
	const [currentRequest, setCurrentRequest] = useState<DSARDeleteRequest | null>(null);
	const [dataTypes, setDataTypes] = useState<DataType[]>(AVAILABLE_DATA_TYPES);
	const [reason, setReason] = useState('');
	const [confirmations, setConfirmations] = useState({
		understandIrreversible: false,
		backupData: false,
		contactSupport: false
	});
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [showConfirmationModal, setShowConfirmationModal] = useState(false);

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const lastActivity = localStorage.getItem('lastActivity');
		if (lastActivity) {
			const timeSinceActivity = Date.now() - parseInt(lastActivity);
			setIsIdle(timeSinceActivity > idleThreshold);
		}
	}, []);

	// Load existing DSAR delete request
	useEffect(() => {
		const loadExistingRequest = async () => {
			try {
				setIsLoading(true);
				setError(null);

				// Simulate API call to check for existing requests
				await new Promise(resolve => setTimeout(resolve, 800));

				// Check localStorage for existing request
				const storedRequest = localStorage.getItem('dsar_delete_request');
				if (storedRequest) {
					const request: DSARDeleteRequest = JSON.parse(storedRequest);
					setCurrentRequest(request);
				}

			} catch (err) {
				setError({
					code: 'DSAR_LOAD_ERROR',
					message: 'Error cargando solicitud existente',
					correlationId: `dsar-delete-load-${Date.now()}`
				});
			} finally {
				setIsLoading(false);
			}
		};

		loadExistingRequest();
	}, []);

	const handleDataTypeToggle = useCallback((typeId: string) => {
		setDataTypes(prev =>
			prev.map(type =>
				type.id === typeId
					? { ...type, selected: !type.selected }
					: type
			)
		);
	}, []);

	const handleConfirmationToggle = useCallback((confirmation: keyof typeof confirmations) => {
		setConfirmations(prev => ({
			...prev,
			[confirmation]: !prev[confirmation]
		}));
	}, []);

	const validateForm = useCallback(() => {
		const selectedTypes = dataTypes.filter(type => type.selected);
		if (selectedTypes.length === 0) {
			setError({
				code: 'VALIDATION_ERROR',
				message: 'Debes seleccionar al menos un tipo de datos',
				correlationId: `dsar-delete-validation-${Date.now()}`
			});
			return false;
		}

		if (!reason.trim()) {
			setError({
				code: 'VALIDATION_ERROR',
				message: 'Debes proporcionar una razón para la solicitud',
				correlationId: `dsar-delete-validation-${Date.now()}`
			});
			return false;
		}

		const allConfirmed = Object.values(confirmations).every(confirmed => confirmed);
		if (!allConfirmed) {
			setError({
				code: 'VALIDATION_ERROR',
				message: 'Debes confirmar todas las advertencias',
				correlationId: `dsar-delete-validation-${Date.now()}`
			});
			return false;
		}

		return true;
	}, [dataTypes, reason, confirmations]);

	const handleSubmitRequest = useCallback(async () => {
		if (!validateForm()) return;

		setShowConfirmationModal(true);
	}, [validateForm]);

	const handleConfirmSubmit = useCallback(async () => {
		try {
			setIsSubmitting(true);
			setShowConfirmationModal(false);
			setError(null);

			const selectedTypes = dataTypes.filter(type => type.selected);

			// Generate idempotency key
			const idempotencyKey = `dsar-delete-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 2000));

			const newRequest: DSARDeleteRequest = {
				id: `dsar-delete-${Date.now()}`,
				requestType: 'delete',
				status: 'pending',
				createdAt: new Date().toISOString(),
				estimatedCompletion: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(), // 30 days
				selectedDataTypes: selectedTypes.map(type => type.id),
				reason: reason.trim(),
				confirmations
			};

			// Store in localStorage
			localStorage.setItem('dsar_delete_request', JSON.stringify(newRequest));
			setCurrentRequest(newRequest);

			// Reset form
			setDataTypes(AVAILABLE_DATA_TYPES);
			setReason('');
			setConfirmations({
				understandIrreversible: false,
				backupData: false,
				contactSupport: false
			});

		} catch (err) {
			setError({
				code: 'DSAR_SUBMIT_ERROR',
				message: 'Error enviando la solicitud de eliminación',
				correlationId: `dsar-delete-submit-${Date.now()}`
			});
		} finally {
			setIsSubmitting(false);
		}
	}, [dataTypes, reason, confirmations]);

	const handleCancelRequest = useCallback(async () => {
		try {
			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 500));

			localStorage.removeItem('dsar_delete_request');
			setCurrentRequest(null);

		} catch (err) {
			setError({
				code: 'CANCEL_ERROR',
				message: 'Error cancelando la solicitud',
				correlationId: `cancel-delete-${Date.now()}`
			});
		}
	}, []);

	const getStatusColor = (status: string) => {
		const colors: Record<string, string> = {
			pending: 'bg-yellow-100 text-yellow-800',
			processing: 'bg-blue-100 text-blue-800',
			completed: 'bg-green-100 text-green-800',
			failed: 'bg-red-100 text-red-800',
			cancelled: 'bg-gray-100 text-gray-800'
		};
		return colors[status] || 'bg-gray-100 text-gray-800';
	};

	const getStatusLabel = (status: string) => {
		const labels: Record<string, string> = {
			pending: 'Pendiente',
			processing: 'Procesando',
			completed: 'Completada',
			failed: 'Fallida',
			cancelled: 'Cancelada'
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

	const selectedCriticalTypes = dataTypes.filter(type => type.selected && type.critical);
	const hasCriticalSelections = selectedCriticalTypes.length > 0;

	// Idle state
	if (isIdle) {
		return <IdleState onResume={() => window.location.reload()} />;
	}

	// Loading state
	if (isLoading && !currentRequest) {
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
							Solicitud de Eliminación de Datos (Derecho al Olvido)
						</h1>
						<p className="text-lg text-gray-600">
							Solicita la eliminación permanente de tus datos personales según el RGPD
						</p>
					</div>

					{/* Critical Warning */}
					<div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-8">
						<div className="flex items-start">
							<svg className="w-5 h-5 text-red-400 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
								<path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
							</svg>
							<div>
								<h3 className="text-red-800 font-medium text-sm">
									Advertencia importante
								</h3>
								<p className="text-red-700 text-sm mt-1">
									Esta acción es <strong>irreversible</strong>. Una vez eliminados, tus datos no podrán ser recuperados.
									Si seleccionas datos críticos (perfil o pagos), perderás el acceso a tu cuenta.
								</p>
							</div>
						</div>
					</div>

					{/* Existing Request Status */}
					{currentRequest && (
						<div className="bg-white rounded-lg shadow-sm p-6 mb-8">
							<div className="flex items-center justify-between mb-4">
								<h2 className="text-xl font-semibold text-gray-900">
									Solicitud Actual
								</h2>
								<span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(currentRequest.status)}`}>
									{getStatusLabel(currentRequest.status)}
								</span>
							</div>

							<div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
								<div>
									<p className="text-sm text-gray-500">ID de solicitud</p>
									<p className="font-mono text-sm">{currentRequest.id}</p>
								</div>
								<div>
									<p className="text-sm text-gray-500">Fecha de solicitud</p>
									<p className="text-sm">{formatDate(currentRequest.createdAt)}</p>
								</div>
								<div>
									<p className="text-sm text-gray-500">Completación estimada</p>
									<p className="text-sm">{formatDate(currentRequest.estimatedCompletion)}</p>
								</div>
								<div>
									<p className="text-sm text-gray-500">Tipos de datos</p>
									<p className="text-sm">{currentRequest.selectedDataTypes.length} seleccionados</p>
								</div>
							</div>

							{currentRequest.status === 'completed' && (
								<div className="border-t pt-4">
									<div className="bg-green-50 border border-green-200 rounded-lg p-4">
										<p className="text-green-800 text-sm">
											<strong>Solicitud completada:</strong> Tus datos han sido eliminados permanentemente según tu solicitud.
										</p>
									</div>
								</div>
							)}

							{(currentRequest.status === 'pending' || currentRequest.status === 'processing') && (
								<div className="border-t pt-4">
									<AppButton
										variant="outline"
										onClick={handleCancelRequest}
										className="w-full md:w-auto"
									>
										Cancelar Solicitud
									</AppButton>
								</div>
							)}
						</div>
					)}

					{/* New Request Form */}
					{!currentRequest && (
						<div className="bg-white rounded-lg shadow-sm p-6">
							<h2 className="text-xl font-semibold text-gray-900 mb-6">
								Nueva Solicitud de Eliminación
							</h2>

							{/* Data Types Selection */}
							<div className="mb-6">
								<h3 className="text-lg font-medium text-gray-900 mb-4">
									Selecciona los tipos de datos a eliminar
								</h3>
								<div className="space-y-3">
									{dataTypes.map((type) => (
										<div key={type.id} className="flex items-start">
											<div className="flex items-center h-5">
												<input
													id={`type-${type.id}`}
													type="checkbox"
													checked={type.selected}
													onChange={() => handleDataTypeToggle(type.id)}
													className="h-4 w-4 text-red-600 focus:ring-red-500 border-gray-300 rounded"
												/>
											</div>
											<div className="ml-3 flex-1">
												<label htmlFor={`type-${type.id}`} className="text-sm font-medium text-gray-700 flex items-center">
													{type.name}
													{type.critical && (
														<span className="ml-2 inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-red-100 text-red-800">
															Crítico
														</span>
													)}
												</label>
												<p className="text-sm text-gray-500">{type.description}</p>
											</div>
										</div>
									))}
								</div>

								{hasCriticalSelections && (
									<div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
										<p className="text-red-800 text-sm">
											<strong>⚠️ Has seleccionado datos críticos:</strong> La eliminación de estos datos resultará en la pérdida permanente de tu cuenta y acceso a la aplicación.
										</p>
									</div>
								)}
							</div>

							{/* Reason */}
							<div className="mb-6">
								<AppTextField
									label="Razón de la solicitud"
									value={reason}
									onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setReason(e.target.value)}
									placeholder="Explica por qué deseas eliminar tus datos..."
									multiline
									rows={4}
									fullWidth
									required
								/>
								<p className="text-sm text-gray-500 mt-1">
									Proporciona una explicación detallada de tu solicitud
								</p>
							</div>

							{/* Confirmations */}
							<div className="mb-6">
								<h3 className="text-lg font-medium text-gray-900 mb-4">
									Confirmaciones requeridas
								</h3>
								<div className="space-y-3">
									<div className="flex items-start">
										<div className="flex items-center h-5">
											<input
												id="confirm-irreversible"
												type="checkbox"
												checked={confirmations.understandIrreversible}
												onChange={() => handleConfirmationToggle('understandIrreversible')}
												className="h-4 w-4 text-red-600 focus:ring-red-500 border-gray-300 rounded"
											/>
										</div>
										<div className="ml-3">
											<label htmlFor="confirm-irreversible" className="text-sm font-medium text-gray-700">
												Entiendo que esta acción es irreversible
											</label>
											<p className="text-sm text-gray-500">
												Los datos eliminados no podrán ser recuperados bajo ninguna circunstancia
											</p>
										</div>
									</div>

									<div className="flex items-start">
										<div className="flex items-center h-5">
											<input
												id="confirm-backup"
												type="checkbox"
												checked={confirmations.backupData}
												onChange={() => handleConfirmationToggle('backupData')}
												className="h-4 w-4 text-red-600 focus:ring-red-500 border-gray-300 rounded"
											/>
										</div>
										<div className="ml-3">
											<label htmlFor="confirm-backup" className="text-sm font-medium text-gray-700">
												He hecho una copia de seguridad de mis datos importantes
											</label>
											<p className="text-sm text-gray-500">
												Si tienes datos que deseas conservar, asegúrate de exportarlos antes
											</p>
										</div>
									</div>

									<div className="flex items-start">
										<div className="flex items-center h-5">
											<input
												id="confirm-support"
												type="checkbox"
												checked={confirmations.contactSupport}
												onChange={() => handleConfirmationToggle('contactSupport')}
												className="h-4 w-4 text-red-600 focus:ring-red-500 border-gray-300 rounded"
											/>
										</div>
										<div className="ml-3">
											<label htmlFor="confirm-support" className="text-sm font-medium text-gray-700">
												He contactado al soporte si tengo dudas
											</label>
											<p className="text-sm text-gray-500">
												Nuestro equipo de soporte puede ayudarte con cualquier pregunta sobre este proceso
											</p>
										</div>
									</div>
								</div>
							</div>

							{/* Submit Button */}
							<div className="flex justify-end">
								<AppButton
									variant="danger"
									onClick={handleSubmitRequest}
									disabled={isSubmitting || dataTypes.filter(t => t.selected).length === 0}
									className="w-full md:w-auto"
								>
									{isSubmitting ? 'Enviando...' : 'Enviar Solicitud de Eliminación'}
								</AppButton>
							</div>
						</div>
					)}

					{/* Confirmation Modal */}
					{showConfirmationModal && (
						<div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
							<div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
								<div className="mt-3">
									<h3 className="text-lg font-medium text-gray-900 mb-4">
										Confirmar solicitud de eliminación
									</h3>
									<div className="mb-4">
										<p className="text-sm text-gray-500 mb-3">
											Estás a punto de solicitar la eliminación permanente de:
										</p>
										<ul className="text-sm text-gray-700 space-y-1">
											{dataTypes.filter(t => t.selected).map(type => (
												<li key={type.id} className="flex items-center">
													• {type.name}
													{type.critical && (
														<span className="ml-2 text-red-600 font-medium">(Crítico)</span>
													)}
												</li>
											))}
										</ul>
									</div>
									<div className="bg-red-50 border border-red-200 rounded-lg p-3 mb-4">
										<p className="text-red-800 text-sm">
											<strong>Esta acción no se puede deshacer.</strong> Una vez procesada la solicitud,
											tus datos serán eliminados permanentemente de nuestros sistemas.
										</p>
									</div>
									<div className="flex justify-end space-x-3">
										<AppButton
											variant="outline"
											onClick={() => setShowConfirmationModal(false)}
										>
											Cancelar
										</AppButton>
										<AppButton
											variant="danger"
											onClick={handleConfirmSubmit}
											disabled={isSubmitting}
										>
											{isSubmitting ? 'Procesando...' : 'Confirmar Eliminación'}
										</AppButton>
									</div>
								</div>
							</div>
						</div>
					)}

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
								<strong>¿Qué datos puedo eliminar?</strong><br />
								Puedes seleccionar qué tipos de datos eliminar. Los datos críticos (perfil, pagos) resultarán en la pérdida de tu cuenta.
							</p>
							<p>
								<strong>¿Cuánto tiempo tarda?</strong><br />
								Procesamos las solicitudes de eliminación en un máximo de 30 días según el RGPD.
							</p>
							<p>
								<strong>¿Puedo cancelar?</strong><br />
								Sí, puedes cancelar tu solicitud mientras esté pendiente o procesándose.
							</p>
							<p>
								<strong>¿Qué pasa con mi cuenta?</strong><br />
								Si eliminas datos críticos, tu cuenta será desactivada permanentemente.
							</p>
						</div>
					</div>
				</div>
			</div>
		</ErrorBoundary>
	);
};

export default DSARDelete;
