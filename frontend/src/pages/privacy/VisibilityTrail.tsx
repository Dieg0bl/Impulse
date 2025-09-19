import React, { useState, useEffect, useCallback } from 'react';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { A11yHelper } from '../../components/A11yHelper';
import { NotificationCard } from '../../components/NotificationCard';
import { IdleState } from '../../components/IdleState';
import { AppButton } from '../../ui/AppButton';
import { AppTextField } from '../../ui/AppTextField';
import type { ApiErrorDto } from '../../types/api';

interface VisibilityEvent {
	id: string;
	timestamp: string;
	eventType: 'access' | 'modification' | 'request' | 'consent';
	description: string;
	actor: string;
	ipAddress?: string;
	userAgent?: string;
	location?: string;
	status: 'success' | 'failed' | 'pending';
	details?: Record<string, any>;
}

interface VisibilityTrailData {
	events: VisibilityEvent[];
	totalEvents: number;
	lastUpdated: string;
}

const SAMPLE_EVENTS: VisibilityEvent[] = [
	{
		id: '1',
		timestamp: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
		eventType: 'access',
		description: 'Acceso a datos de perfil desde aplicación móvil',
		actor: 'Usuario (tú)',
		ipAddress: '192.168.1.100',
		userAgent: 'Mobile App v2.1.0',
		location: 'Madrid, España',
		status: 'success'
	},
	{
		id: '2',
		timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
		eventType: 'modification',
		description: 'Cambio en configuración de privacidad - email marketing',
		actor: 'Usuario (tú)',
		ipAddress: '192.168.1.100',
		userAgent: 'Chrome 120.0.0.0',
		location: 'Madrid, España',
		status: 'success',
		details: { previousValue: true, newValue: false }
	},
	{
		id: '3',
		timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
		eventType: 'request',
		description: 'Solicitud de acceso a datos por soporte técnico',
		actor: 'Sistema de soporte',
		ipAddress: '10.0.0.50',
		userAgent: 'Internal API v1.0',
		location: 'Servidor interno',
		status: 'pending'
	},
	{
		id: '4',
		timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24 * 7).toISOString(),
		eventType: 'consent',
		description: 'Consentimiento actualizado para procesamiento de datos',
		actor: 'Usuario (tú)',
		ipAddress: '192.168.1.100',
		userAgent: 'Firefox 119.0',
		location: 'Madrid, España',
		status: 'success',
		details: { consentType: 'data_processing', version: '2.1' }
	},
	{
		id: '5',
		timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24 * 14).toISOString(),
		eventType: 'access',
		description: 'Acceso a historial de desafíos desde dashboard',
		actor: 'Usuario (tú)',
		ipAddress: '192.168.1.100',
		userAgent: 'Chrome 120.0.0.0',
		location: 'Madrid, España',
		status: 'success'
	}
];

const VisibilityTrail: React.FC = () => {
	const [isLoading, setIsLoading] = useState(true);
	const [error, setError] = useState<ApiErrorDto | null>(null);
	const [isIdle, setIsIdle] = useState(false);
	const [searchTerm, setSearchTerm] = useState('');
	const [eventTypeFilter, setEventTypeFilter] = useState<string>('all');
	const [dateRange, setDateRange] = useState({
		start: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
		end: new Date().toISOString().split('T')[0]
	});
	const [visibilityData, setVisibilityData] = useState<VisibilityTrailData | null>(null);

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const lastActivity = localStorage.getItem('lastActivity');
		if (lastActivity) {
			const timeSinceActivity = Date.now() - parseInt(lastActivity);
			setIsIdle(timeSinceActivity > idleThreshold);
		}
	}, []);

	// Load visibility trail data
	useEffect(() => {
		const loadVisibilityTrail = async () => {
			try {
				setIsLoading(true);
				setError(null);

				// Simulate API call
				await new Promise(resolve => setTimeout(resolve, 1200));

				const mockData: VisibilityTrailData = {
					events: SAMPLE_EVENTS,
					totalEvents: SAMPLE_EVENTS.length,
					lastUpdated: new Date().toISOString()
				};

				setVisibilityData(mockData);

			} catch (err) {
				setError({
					code: 'VISIBILITY_LOAD_ERROR',
					message: 'Error cargando el historial de visibilidad',
					correlationId: `visibility-load-${Date.now()}`
				});
			} finally {
				setIsLoading(false);
			}
		};

		loadVisibilityTrail();
	}, [dateRange]);

	const filteredEvents = visibilityData?.events.filter(event => {
		const matchesSearch = searchTerm === '' ||
			event.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
			event.actor.toLowerCase().includes(searchTerm.toLowerCase());

		const matchesType = eventTypeFilter === 'all' || event.eventType === eventTypeFilter;

		const eventDate = new Date(event.timestamp).toISOString().split('T')[0];
		const matchesDate = eventDate >= dateRange.start && eventDate <= dateRange.end;

		return matchesSearch && matchesType && matchesDate;
	}) || [];

	const handleExportTrail = useCallback(async () => {
		try {
			console.log('Exporting visibility trail');

			// Create CSV content
			const csvContent = [
				['Fecha', 'Tipo', 'Descripción', 'Actor', 'IP', 'Ubicación', 'Estado'].join(','),
				...filteredEvents.map(event => [
					new Date(event.timestamp).toLocaleString(),
					event.eventType,
					`"${event.description}"`,
					event.actor,
					event.ipAddress || '',
					event.location || '',
					event.status
				].join(','))
			].join('\n');

			// Create download
			const blob = new Blob([csvContent], { type: 'text/csv' });
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = `visibility-trail-${dateRange.start}-to-${dateRange.end}.csv`;
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(url);

		} catch (err) {
			setError({
				code: 'EXPORT_ERROR',
				message: 'Error exportando el historial',
				correlationId: `export-trail-${Date.now()}`
			});
		}
	}, [filteredEvents, dateRange]);

	const getEventTypeLabel = (type: string) => {
		const labels: Record<string, string> = {
			access: 'Acceso',
			modification: 'Modificación',
			request: 'Solicitud',
			consent: 'Consentimiento'
		};
		return labels[type] || type;
	};

	const getEventTypeColor = (type: string) => {
		const colors: Record<string, string> = {
			access: 'bg-blue-100 text-blue-800',
			modification: 'bg-yellow-100 text-yellow-800',
			request: 'bg-purple-100 text-purple-800',
			consent: 'bg-green-100 text-green-800'
		};
		return colors[type] || 'bg-gray-100 text-gray-800';
	};

	const getStatusColor = (status: string) => {
		const colors: Record<string, string> = {
			success: 'bg-green-100 text-green-800',
			failed: 'bg-red-100 text-red-800',
			pending: 'bg-yellow-100 text-yellow-800'
		};
		return colors[status] || 'bg-gray-100 text-gray-800';
	};

	const formatTimestamp = (timestamp: string) => {
		const date = new Date(timestamp);
		const now = new Date();
		const diffMs = now.getTime() - date.getTime();
		const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

		if (diffDays === 0) {
			return `Hoy ${date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}`;
		} else if (diffDays === 1) {
			return `Ayer ${date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })}`;
		} else if (diffDays < 7) {
			return `${diffDays} días atrás`;
		} else {
			return date.toLocaleDateString('es-ES', {
				year: 'numeric',
				month: 'short',
				day: 'numeric',
				hour: '2-digit',
				minute: '2-digit'
			});
		}
	};

	// Idle state
	if (isIdle) {
		return <IdleState onResume={() => window.location.reload()} />;
	}

	// Loading state
	if (isLoading) {
		return (
			<div className="min-h-screen bg-gray-50">
				<div className="max-w-6xl mx-auto px-4 py-8">
					<div className="animate-pulse space-y-6">
						<div className="h-8 bg-gray-200 rounded w-1/3"></div>
						<div className="bg-white rounded-lg p-6">
							<div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
							<div className="space-y-4">
								{[...Array(5)].map((_, i) => (
									<div key={i} className="flex space-x-4">
										<div className="h-10 w-10 bg-gray-200 rounded-full"></div>
										<div className="flex-1 space-y-2">
											<div className="h-4 bg-gray-200 rounded w-3/4"></div>
											<div className="h-3 bg-gray-200 rounded w-1/2"></div>
										</div>
									</div>
								))}
							</div>
						</div>
					</div>
				</div>
			</div>
		);
	}

	// Error state
	if (error && !visibilityData) {
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

	// Empty state
	if (!visibilityData || filteredEvents.length === 0) {
		return (
			<div className="min-h-screen bg-gray-50 flex items-center justify-center">
				<div className="max-w-md mx-auto px-4">
					<NotificationCard
						type="info"
						title="No hay eventos"
						message={searchTerm || eventTypeFilter !== 'all' ?
							"No se encontraron eventos que coincidan con los filtros aplicados" :
							"No hay eventos de visibilidad registrados aún"
						}
						action={{
							label: searchTerm || eventTypeFilter !== 'all' ? 'Limpiar filtros' : 'Actualizar',
							onClick: () => {
								setSearchTerm('');
								setEventTypeFilter('all');
							}
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
				<div className="max-w-6xl mx-auto px-4 py-8">
					{/* Header */}
					<div className="mb-8">
						<h1 className="text-3xl font-bold text-gray-900 mb-2">
							Traza de Visibilidad
						</h1>
						<p className="text-lg text-gray-600">
							Historial completo de accesos y modificaciones a tus datos
						</p>
						{visibilityData.lastUpdated && (
							<p className="text-sm text-gray-500 mt-2">
								Última actualización: {new Date(visibilityData.lastUpdated).toLocaleString('es-ES')}
							</p>
						)}
					</div>

					{/* Privacy Notice */}
					<div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-8">
						<div className="flex items-start">
							<svg className="w-5 h-5 text-blue-400 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
								<path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
							</svg>
							<div>
								<h3 className="text-blue-800 font-medium text-sm">
									Información de privacidad
								</h3>
								<p className="text-blue-700 text-sm mt-1">
									Esta traza muestra todas las interacciones con tus datos personales.
									Los eventos se almacenan de forma segura y se eliminan automáticamente después de 2 años.
								</p>
							</div>
						</div>
					</div>

					{/* Filters */}
					<div className="bg-white rounded-lg shadow-sm p-6 mb-8">
						<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
							<AppTextField
								label="Buscar eventos"
								value={searchTerm}
								onChange={(e: React.ChangeEvent<HTMLInputElement>) => setSearchTerm(e.target.value)}
								placeholder="Descripción, actor..."
								fullWidth
							/>
							<div>
								<label htmlFor="event-type" className="block text-sm font-medium text-gray-700 mb-1">
									Tipo de evento
								</label>
								<select
									id="event-type"
									value={eventTypeFilter}
									onChange={(e) => setEventTypeFilter(e.target.value)}
									className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
								>
									<option value="all">Todos los tipos</option>
									<option value="access">Acceso</option>
									<option value="modification">Modificación</option>
									<option value="request">Solicitud</option>
									<option value="consent">Consentimiento</option>
								</select>
							</div>
							<AppTextField
								label="Fecha inicio"
								type="date"
								value={dateRange.start}
								onChange={(e: React.ChangeEvent<HTMLInputElement>) => setDateRange(prev => ({ ...prev, start: e.target.value }))}
								fullWidth={false}
							/>
							<AppTextField
								label="Fecha fin"
								type="date"
								value={dateRange.end}
								onChange={(e: React.ChangeEvent<HTMLInputElement>) => setDateRange(prev => ({ ...prev, end: e.target.value }))}
								fullWidth={false}
							/>
						</div>

						<div className="flex justify-between items-center">
							<p className="text-sm text-gray-600">
								{filteredEvents.length} de {visibilityData.totalEvents} eventos
							</p>
							<AppButton
								variant="outline"
								onClick={handleExportTrail}
								disabled={filteredEvents.length === 0}
							>
								Exportar CSV
							</AppButton>
						</div>
					</div>

					{/* Events List */}
					<div className="bg-white rounded-lg shadow-sm overflow-hidden">
						<div className="divide-y divide-gray-200">
							{filteredEvents.map((event) => (
								<div key={event.id} className="p-6 hover:bg-gray-50 transition-colors">
									<div className="flex items-start justify-between">
										<div className="flex items-start space-x-4">
											{/* Event Icon */}
											<div className="flex-shrink-0">
												<div className={`w-10 h-10 rounded-full flex items-center justify-center ${getEventTypeColor(event.eventType)}`}>
													{event.eventType === 'access' && '👁️'}
													{event.eventType === 'modification' && '✏️'}
													{event.eventType === 'request' && '📋'}
													{event.eventType === 'consent' && '✅'}
												</div>
											</div>

											{/* Event Details */}
											<div className="flex-1 min-w-0">
												<div className="flex items-center space-x-2 mb-1">
													<h3 className="text-sm font-medium text-gray-900">
														{event.description}
													</h3>
													<span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getEventTypeColor(event.eventType)}`}>
														{getEventTypeLabel(event.eventType)}
													</span>
													<span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(event.status)}`}>
														{event.status === 'success' ? 'Exitoso' :
														 event.status === 'failed' ? 'Fallido' : 'Pendiente'}
													</span>
												</div>

												<p className="text-sm text-gray-500 mb-2">
													<strong>Actor:</strong> {event.actor}
												</p>

												<div className="text-xs text-gray-400 space-y-1">
													<p><strong>Fecha:</strong> {formatTimestamp(event.timestamp)}</p>
													{event.ipAddress && <p><strong>IP:</strong> {event.ipAddress}</p>}
													{event.location && <p><strong>Ubicación:</strong> {event.location}</p>}
													{event.userAgent && <p><strong>Dispositivo:</strong> {event.userAgent}</p>}
												</div>

												{/* Event Details */}
												{event.details && (
													<div className="mt-3 p-3 bg-gray-50 rounded-lg">
														<h4 className="text-xs font-medium text-gray-700 mb-2">
															Detalles del evento
														</h4>
														<pre className="text-xs text-gray-600 whitespace-pre-wrap">
															{JSON.stringify(event.details, null, 2)}
														</pre>
													</div>
												)}
											</div>
										</div>

										{/* Timestamp */}
										<div className="flex-shrink-0 text-right">
											<p className="text-xs text-gray-400">
												{new Date(event.timestamp).toLocaleDateString('es-ES')}
											</p>
											<p className="text-xs text-gray-400">
												{new Date(event.timestamp).toLocaleTimeString('es-ES', {
													hour: '2-digit',
													minute: '2-digit'
												})}
											</p>
										</div>
									</div>
								</div>
							))}
						</div>
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
				</div>
			</div>
		</ErrorBoundary>
	);
};

export default VisibilityTrail;
