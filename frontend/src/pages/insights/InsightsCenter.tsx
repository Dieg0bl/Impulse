import React, { useState, useEffect, useCallback } from 'react';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorBoundary } from '../../components/ErrorBoundary';
import { A11yHelper } from '../../components/A11yHelper';
import { NotificationCard } from '../../components/NotificationCard';
import { IdleState } from '../../components/IdleState';
import { AppButton } from '../../ui/AppButton';
import { AppTextField } from '../../ui/AppTextField';
import type { ApiErrorDto } from '../../types/api';

interface MetricCard {
	id: string;
	title: string;
	value: string | number;
	change: number;
	changeType: 'positive' | 'negative' | 'neutral';
	icon: string;
	description: string;
}

interface ChartData {
	labels: string[];
	datasets: {
		label: string;
		data: number[];
		backgroundColor?: string;
		borderColor?: string;
	}[];
}

interface InsightsData {
	metrics: MetricCard[];
	userActivity: ChartData;
	challengeProgress: ChartData;
	revenue: ChartData;
	engagement: ChartData;
}

interface DateRange {
	start: string;
	end: string;
}

const METRICS: MetricCard[] = [
	{
		id: 'total-users',
		title: 'Usuarios Totales',
		value: '12,543',
		change: 12.5,
		changeType: 'positive',
		icon: 'users',
		description: 'Usuarios registrados en la plataforma'
	},
	{
		id: 'active-challenges',
		title: 'Desafíos Activos',
		value: '1,234',
		change: 8.2,
		changeType: 'positive',
		icon: 'target',
		description: 'Desafíos actualmente en progreso'
	},
	{
		id: 'completed-challenges',
		title: 'Desafíos Completados',
		value: '8,765',
		change: -2.1,
		changeType: 'negative',
		icon: 'check-circle',
		description: 'Desafíos finalizados exitosamente'
	},
	{
		id: 'total-revenue',
		title: 'Ingresos Totales',
		value: '€45,678',
		change: 15.3,
		changeType: 'positive',
		icon: 'dollar-sign',
		description: 'Ingresos generados este mes'
	},
	{
		id: 'avg-session-time',
		title: 'Tiempo Promedio',
		value: '24m 32s',
		change: 5.7,
		changeType: 'positive',
		icon: 'clock',
		description: 'Tiempo promedio de sesión'
	},
	{
		id: 'conversion-rate',
		title: 'Tasa de Conversión',
		value: '3.24%',
		change: -0.8,
		changeType: 'negative',
		icon: 'trending-up',
		description: 'Conversión de visitantes a usuarios'
	}
];

const InsightsCenter: React.FC = () => {
	const [isLoading, setIsLoading] = useState(true);
	const [error, setError] = useState<ApiErrorDto | null>(null);
	const [isIdle, setIsIdle] = useState(false);
	const [dateRange, setDateRange] = useState<DateRange>({
		start: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
		end: new Date().toISOString().split('T')[0]
	});
	const [selectedMetric, setSelectedMetric] = useState<string>('all');
	const [insightsData, setInsightsData] = useState<InsightsData | null>(null);

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const lastActivity = localStorage.getItem('lastActivity');
		if (lastActivity) {
			const timeSinceActivity = Date.now() - parseInt(lastActivity);
			setIsIdle(timeSinceActivity > idleThreshold);
		}
	}, []);

	// Load insights data
	useEffect(() => {
		const loadInsights = async () => {
			try {
				setIsLoading(true);
				setError(null);

				// Simulate API call
				await new Promise(resolve => setTimeout(resolve, 1500));

				const mockData: InsightsData = {
					metrics: METRICS,
					userActivity: {
						labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
						datasets: [{
							label: 'Usuarios Activos',
							data: [120, 150, 180, 200, 170, 140, 160],
							backgroundColor: 'rgba(59, 130, 246, 0.5)',
							borderColor: 'rgb(59, 130, 246)'
						}]
					},
					challengeProgress: {
						labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio'],
						datasets: [{
							label: 'Desafíos Completados',
							data: [65, 78, 90, 81, 95, 110],
							backgroundColor: 'rgba(16, 185, 129, 0.5)',
							borderColor: 'rgb(16, 185, 129)'
						}]
					},
					revenue: {
						labels: ['Sem 1', 'Sem 2', 'Sem 3', 'Sem 4'],
						datasets: [{
							label: 'Ingresos (€)',
							data: [8500, 9200, 10100, 11200],
							backgroundColor: 'rgba(245, 158, 11, 0.5)',
							borderColor: 'rgb(245, 158, 11)'
						}]
					},
					engagement: {
						labels: ['0-5min', '5-15min', '15-30min', '30-60min', '60min+'],
						datasets: [{
							label: 'Sesiones',
							data: [120, 340, 280, 150, 90],
							backgroundColor: 'rgba(139, 92, 246, 0.5)',
							borderColor: 'rgb(139, 92, 246)'
						}]
					}
				};

				setInsightsData(mockData);

			} catch (err) {
				setError({
					code: 'INSIGHTS_LOAD_ERROR',
					message: 'Error cargando datos de insights',
					correlationId: `insights-load-${Date.now()}`
				});
			} finally {
				setIsLoading(false);
			}
		};

		loadInsights();
	}, [dateRange, selectedMetric]);

	const handleDateRangeChange = useCallback((field: 'start' | 'end', value: string) => {
		setDateRange(prev => ({ ...prev, [field]: value }));
	}, []);

	const handleExportData = useCallback(async (format: 'csv' | 'pdf') => {
		try {
			// Simulate export
			console.log(`Exporting insights data as ${format.toUpperCase()}`);

			// Create download link
			const blob = new Blob(['Mock export data'], { type: 'text/plain' });
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = `insights-${dateRange.start}-to-${dateRange.end}.${format}`;
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(url);

		} catch (err) {
			setError({
				code: 'EXPORT_ERROR',
				message: 'Error exportando datos',
				correlationId: `export-${Date.now()}`
			});
		}
	}, [dateRange]);

	const getIcon = (iconName: string) => {
		const icons: Record<string, string> = {
			users: '👥',
			target: '🎯',
			'check-circle': '✅',
			'dollar-sign': '💰',
			clock: '🕐',
			'trending-up': '📈'
		};
		return icons[iconName] || '📊';
	};

	const renderChart = (data: ChartData, title: string) => (
		<div className="bg-white rounded-lg shadow-sm p-6">
			<h3 className="text-lg font-semibold text-gray-900 mb-4">{title}</h3>
			<div className="h-64 flex items-center justify-center bg-gray-50 rounded-lg">
				<div className="text-center">
					<div className="text-4xl mb-2">📊</div>
					<p className="text-gray-500">Gráfico de {title.toLowerCase()}</p>
					<p className="text-xs text-gray-400 mt-2">
						Datos: {data.labels.join(', ')}
					</p>
				</div>
			</div>
		</div>
	);

	// Idle state
	if (isIdle) {
		return <IdleState onResume={() => window.location.reload()} />;
	}

	// Loading state
	if (isLoading) {
		return (
			<div className="min-h-screen bg-gray-50">
				<div className="max-w-7xl mx-auto px-4 py-8">
					<div className="animate-pulse space-y-8">
						<div className="h-8 bg-gray-200 rounded w-1/3"></div>
						<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
							{[...Array(6)].map((_, i) => (
								<div key={i} className="bg-white rounded-lg p-6">
									<div className="h-4 bg-gray-200 rounded w-1/2 mb-2"></div>
									<div className="h-8 bg-gray-200 rounded w-1/3 mb-4"></div>
									<div className="h-3 bg-gray-200 rounded w-1/4"></div>
								</div>
							))}
						</div>
						<div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
							<div className="bg-white rounded-lg p-6 h-64"></div>
							<div className="bg-white rounded-lg p-6 h-64"></div>
						</div>
					</div>
				</div>
			</div>
		);
	}

	// Error state
	if (error && !insightsData) {
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
	if (!insightsData) {
		return (
			<div className="min-h-screen bg-gray-50 flex items-center justify-center">
				<div className="max-w-md mx-auto px-4">
					<NotificationCard
						type="info"
						title="No hay datos"
						message="No se encontraron datos de insights para el período seleccionado"
						action={{
							label: 'Cambiar fechas',
							onClick: () => setDateRange({
								start: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
								end: new Date().toISOString().split('T')[0]
							})
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
				<div className="max-w-7xl mx-auto px-4 py-8">
					{/* Header */}
					<div className="mb-8">
						<h1 className="text-3xl font-bold text-gray-900 mb-2">
							Centro de Insights
						</h1>
						<p className="text-lg text-gray-600">
							Analiza el rendimiento y las métricas de tu plataforma
						</p>
					</div>

					{/* Filters and Actions */}
					<div className="bg-white rounded-lg shadow-sm p-6 mb-8">
						<div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
							<div className="flex flex-col sm:flex-row gap-4">
								<AppTextField
									label="Fecha inicio"
									type="date"
									value={dateRange.start}
									onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleDateRangeChange('start', e.target.value)}
									fullWidth={false}
								/>
								<AppTextField
									label="Fecha fin"
									type="date"
									value={dateRange.end}
									onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleDateRangeChange('end', e.target.value)}
									fullWidth={false}
								/>
								<div>
									<label htmlFor="metric-filter" className="block text-sm font-medium text-gray-700 mb-1">
										Métrica
									</label>
									<select
										id="metric-filter"
										value={selectedMetric}
										onChange={(e) => setSelectedMetric(e.target.value)}
										className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
									>
										<option value="all">Todas las métricas</option>
										<option value="users">Usuarios</option>
										<option value="challenges">Desafíos</option>
										<option value="revenue">Ingresos</option>
										<option value="engagement">Engagement</option>
									</select>
								</div>
							</div>

							<div className="flex gap-3">
								<AppButton
									variant="outline"
									onClick={() => handleExportData('csv')}
								>
									Exportar CSV
								</AppButton>
								<AppButton
									variant="outline"
									onClick={() => handleExportData('pdf')}
								>
									Exportar PDF
								</AppButton>
							</div>
						</div>
					</div>

					{/* Metrics Cards */}
					<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
						{insightsData.metrics.map((metric) => (
							<div key={metric.id} className="bg-white rounded-lg shadow-sm p-6">
								<div className="flex items-center justify-between mb-4">
									<div className="flex items-center">
										<span className="text-2xl mr-3" role="img" aria-label={metric.title}>
											{getIcon(metric.icon)}
										</span>
										<div>
											<h3 className="text-sm font-medium text-gray-500">
												{metric.title}
											</h3>
											<p className="text-xs text-gray-400">
												{metric.description}
											</p>
										</div>
									</div>
								</div>

								<div className="flex items-baseline justify-between">
									<div>
										<p className="text-2xl font-bold text-gray-900">
											{metric.value}
										</p>
										<div className="flex items-center mt-1">
											<span
												className={`text-sm font-medium ${
													metric.changeType === 'positive'
														? 'text-green-600'
														: metric.changeType === 'negative'
														? 'text-red-600'
														: 'text-gray-600'
												}`}
											>
												{metric.change > 0 ? '+' : ''}{metric.change}%
											</span>
											<span className="text-sm text-gray-500 ml-1">
												vs mes anterior
											</span>
										</div>
									</div>
								</div>
							</div>
						))}
					</div>

					{/* Charts Grid */}
					<div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
						{renderChart(insightsData.userActivity, 'Actividad de Usuarios')}
						{renderChart(insightsData.challengeProgress, 'Progreso de Desafíos')}
					</div>

					<div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
						{renderChart(insightsData.revenue, 'Ingresos')}
						{renderChart(insightsData.engagement, 'Engagement por Duración')}
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

export default InsightsCenter;
