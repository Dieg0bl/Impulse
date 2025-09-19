import React, { useState, useEffect, useCallback } from 'react';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { A11yHelper } from '../../components/A11yHelper';
import { NotificationCard } from '../../components/NotificationCard';
import { IdleState } from '../../components/IdleState';
import { AppButton } from '../../ui/AppButton';
import { AppTextField } from '../../ui/AppTextField';
import type { ApiErrorDto } from '../../types/api';

interface DSARRequest {
	id: string;
	requestType: 'export';
	status: 'pending' | 'processing' | 'completed' | 'failed';
	createdAt: string;
	estimatedCompletion: string;
	completedAt?: string;
	downloadUrl?: string;
	selectedDataTypes: string[];
	reason: string;
}

interface DataType {
	id: string;
	name: string;
	description: string;
	size: string;
	selected: boolean;
}

const AVAILABLE_DATA_TYPES: DataType[] = [
	{
		id: 'profile',
		name: 'Datos de perfil',
		description: 'Nombre, email, fecha de nacimiento, preferencias',
		size: '~2KB',
		selected: true
	},
	{
		id: 'challenges',
		name: 'Historial de desafíos',
		description: 'Desafíos completados, puntuaciones, fechas',
		size: '~15KB',
		selected: true
	},
	{
		id: 'referrals',
		name: 'Sistema de referidos',
		description: 'Códigos de referido, recompensas, estadísticas',
		size: '~5KB',
		selected: true
	},
	{
		id: 'activity',
		name: 'Actividad de la aplicación',
		description: 'Logs de acceso, sesiones, interacciones',
		size: '~50KB',
		selected: false
	},
	{
		id: 'communications',
		name: 'Comunicaciones',
		description: 'Emails enviados, notificaciones push, mensajes',
		size: '~25KB',
		selected: false
	},
	{
		id: 'payments',
		name: 'Información de pagos',
		description: 'Historial de transacciones, métodos de pago (anonimizados)',
		size: '~10KB',
		selected: false
	}
];

const DSARExport: React.FC = () => {
	const [isLoading, setIsLoading] = useState(false);
	const [error, setError] = useState<ApiErrorDto | null>(null);
	const [isIdle, setIsIdle] = useState(false);
	const [currentRequest, setCurrentRequest] = useState<DSARRequest | null>(null);
	const [dataTypes, setDataTypes] = useState<DataType[]>(AVAILABLE_DATA_TYPES);
	const [reason, setReason] = useState('');
	const [isSubmitting, setIsSubmitting] = useState(false);

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const lastActivity = localStorage.getItem('lastActivity');
		if (lastActivity) {
			const timeSinceActivity = Date.now() - parseInt(lastActivity);
			setIsIdle(timeSinceActivity > idleThreshold);
		}
	}, []);

	// Load existing DSAR request
	useEffect(() => {
		const loadExistingRequest = async () => {
			try {
				setIsLoading(true);
				setError(null);

				// Simulate API call to check for existing requests
				await new Promise(resolve => setTimeout(resolve, 800));

				// Check localStorage for existing request
				const storedRequest = localStorage.getItem('dsar_export_request');
				if (storedRequest) {
					const request: DSARRequest = JSON.parse(storedRequest);
					setCurrentRequest(request);
				}

			} catch (err) {
				setError({
					code: 'DSAR_LOAD_ERROR',
					message: 'Error cargando solicitud existente',
					correlationId: `dsar-load-${Date.now()}`
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

	const handleSubmitRequest = useCallback(async () => {
		try {
			setIsSubmitting(true);
			setError(null);

			const selectedTypes = dataTypes.filter(type => type.selected);
			if (selectedTypes.length === 0) {
				setError({
					code: 'VALIDATION_ERROR',
					message: 'Debes seleccionar al menos un tipo de datos',
					correlationId: `dsar-validation-${Date.now()}`
				});
				return;
			}

			if (!reason.trim()) {
				setError({
					code: 'VALIDATION_ERROR',
					message: 'Debes proporcionar una razón para la solicitud',
					correlationId: `dsar-validation-${Date.now()}`
				});
				return;
			}

			// Generate idempotency key
			const idempotencyKey = `dsar-export-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 1500));

			const newRequest: DSARRequest = {
				id: `dsar-${Date.now()}`,
				requestType: 'export',
				status: 'pending',
				createdAt: new Date().toISOString(),
				estimatedCompletion: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(), // 7 days
				selectedDataTypes: selectedTypes.map(type => type.id),
				reason: reason.trim()
			};

			// Store in localStorage
			localStorage.setItem('dsar_export_request', JSON.stringify(newRequest));
			setCurrentRequest(newRequest);

			// Reset form
			setDataTypes(AVAILABLE_DATA_TYPES);
			setReason('');

		} catch (err) {
			setError({
				code: 'DSAR_SUBMIT_ERROR',
				message: 'Error enviando la solicitud de exportación',
				correlationId: `dsar-submit-${Date.now()}`
			});
		} finally {
			setIsSubmitting(false);
		}
	}, [dataTypes, reason]);

	const handleDownloadData = useCallback(async () => {
		if (!currentRequest?.downloadUrl) return;

		try {
			// Simulate download
			const mockData = {
				profile: { name: 'Usuario Ejemplo', email: 'usuario@example.com' },
				challenges: [{ id: 1, name: 'Challenge 1', completed: true }],
				referrals: { code: 'REF123', rewards: [] },
				exportedAt: new Date().toISOString(),
				requestId: currentRequest.id
			};

			const blob = new Blob([JSON.stringify(mockData, null, 2)], { type: 'application/json' });
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = `datos-personales-${currentRequest.id}.json`;
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(url);

		} catch (err) {
			setError({
				code: 'DOWNLOAD_ERROR',
				message: 'Error descargando los datos',
				correlationId: `download-${Date.now()}`
			});
		}
	}, [currentRequest]);

	const handleCancelRequest = useCallback(async () => {
		try {
			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 500));

			localStorage.removeItem('dsar_export_request');
			setCurrentRequest(null);

		} catch (err) {
			setError({
				code: 'CANCEL_ERROR',
				message: 'Error cancelando la solicitud',
				correlationId: `cancel-${Date.now()}`
			});
		}
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

	const selectedDataSize = dataTypes
		.filter(type => type.selected)
		.reduce((total, type) => {
			const size = parseFloat(type.size.replace('~', '').replace('KB', ''));
			return total + size;
		}, 0);

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
							Solicitud de Exportación de Datos (DSAR)
						</h1>
						<p className="text-lg text-gray-600">
							Solicita una copia completa de tus datos personales según el RGPD
						</p>
					</div>

					{/* Privacy Notice */}
					<div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-8">
						<div className="flex items-start">
							<svg className="w-5 h-5 text-blue-400 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
								<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
							</svg>
							<div>
								<h3 className="text-blue-800 font-medium text-sm">
									Derecho de acceso a datos
								</h3>
								<p className="text-blue-700 text-sm mt-1">
									Bajo el RGPD, tienes derecho a obtener una copia de todos tus datos personales.
									Procesaremos tu solicitud en un máximo de 30 días y te notificaremos cuando esté lista.
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

							{currentRequest.status === 'completed' && currentRequest.downloadUrl && (
								<div className="border-t pt-4">
									<AppButton
										onClick={handleDownloadData}
										className="w-full md:w-auto"
									>
										Descargar Datos Exportados
									</AppButton>
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
								Nueva Solicitud de Exportación
							</h2>

							{/* Data Types Selection */}
							<div className="mb-6">
								<h3 className="text-lg font-medium text-gray-900 mb-4">
									Selecciona los tipos de datos a exportar
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
													className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
												/>
											</div>
											<div className="ml-3 flex-1">
												<label htmlFor={`type-${type.id}`} className="text-sm font-medium text-gray-700">
													{type.name} <span className="text-gray-500">({type.size})</span>
												</label>
												<p className="text-sm text-gray-500">{type.description}</p>
											</div>
										</div>
									))}
								</div>

								{selectedDataSize > 0 && (
									<div className="mt-4 p-3 bg-gray-50 rounded-lg">
										<p className="text-sm text-gray-600">
											<strong>Tamaño estimado:</strong> ~{selectedDataSize.toFixed(1)}KB
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
									placeholder="Explica por qué necesitas exportar tus datos..."
									multiline
									rows={4}
									fullWidth
									required
								/>
								<p className="text-sm text-gray-500 mt-1">
									Proporciona una breve explicación de por qué solicitas esta exportación
								</p>
							</div>

							{/* Submit Button */}
							<div className="flex justify-end">
								<AppButton
									onClick={handleSubmitRequest}
									disabled={isSubmitting || dataTypes.filter(t => t.selected).length === 0}
									className="w-full md:w-auto"
								>
									{isSubmitting ? 'Enviando...' : 'Enviar Solicitud'}
								</AppButton>
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
								<strong>¿Qué incluye la exportación?</strong><br />
								Todos tus datos personales almacenados en nuestros sistemas, incluyendo perfil, actividad, comunicaciones y datos de pago anonimizados.
							</p>
							<p>
								<strong>¿Cuánto tiempo tarda?</strong><br />
								Procesamos las solicitudes en un máximo de 30 días según el RGPD. La mayoría se completan en 7-14 días.
							</p>
							<p>
								<strong>¿Es seguro?</strong><br />
								Sí, utilizamos encriptación de extremo a extremo y solo tú puedes acceder a tus datos exportados.
							</p>
							<p>
								<strong>¿Hay algún costo?</strong><br />
								No, este servicio es gratuito según tus derechos bajo el RGPD.
							</p>
						</div>
					</div>
				</div>
			</div>
		</ErrorBoundary>
	);
};

export default DSARExport;
