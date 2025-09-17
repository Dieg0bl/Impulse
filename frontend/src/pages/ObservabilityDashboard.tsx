import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import AdminConfigPanel from "../components/AdminConfigPanel";
import PageHeader from "../components/PageHeader";
import {
	Activity,
	AlertTriangle,
	CheckCircle,
	Clock,
	Server,
	TrendingUp,
	Settings,
	BarChart3,
	LineChart,
	Users,
	Download
} from "lucide-react";
import { LineChart as RechartsLine, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';

interface SystemMetrics {
	uptime: number;
	responseTime: number;
	errorRate: number;
	requestsPerSecond: number;
	activeUsers: number;
	cpuUsage: number;
	memoryUsage: number;
	diskUsage: number;
}

interface ErrorLog {
	id: string;
	timestamp: Date;
	level: "error" | "warning" | "info";
	message: string;
	path: string;
	method: string;
	statusCode: number;
	duration: number;
	userAgent?: string;
}

const ObservabilityDashboard: React.FC = () => {
	const [metrics, setMetrics] = useState<SystemMetrics>({
		uptime: 99.9,
		responseTime: 145,
		errorRate: 0.3,
		requestsPerSecond: 23.4,
		activeUsers: 1247,
		cpuUsage: 34.5,
		memoryUsage: 67.2,
		diskUsage: 45.8
	});

	const [isLive, setIsLive] = useState(true);
	const [lastUpdate, setLastUpdate] = useState(new Date());
	const [activeTab, setActiveTab] = useState<"overview" | "errors" | "performance" | "config">("overview");

	// Mock performance data
	const performanceData = [
		{ time: "00:00", responseTime: 120, requests: 15, errors: 0 },
		{ time: "04:00", responseTime: 135, requests: 8, errors: 1 },
		{ time: "08:00", responseTime: 180, requests: 45, errors: 2 },
		{ time: "12:00", responseTime: 165, requests: 67, errors: 1 },
		{ time: "16:00", responseTime: 145, requests: 34, errors: 0 },
		{ time: "20:00", responseTime: 125, requests: 28, errors: 1 },
	];

	const errorLogs: ErrorLog[] = [
		{
			id: "1",
			timestamp: new Date(Date.now() - 300000),
			level: "error",
			message: "Database connection timeout",
			path: "/api/challenges",
			method: "GET",
			statusCode: 500,
			duration: 5000,
			userAgent: "Mozilla/5.0 Chrome"
		},
		{
			id: "2",
			timestamp: new Date(Date.now() - 600000),
			level: "warning",
			message: "High memory usage detected",
			path: "/api/evidences/upload",
			method: "POST",
			statusCode: 413,
			duration: 2340,
		},
		{
			id: "3",
			timestamp: new Date(Date.now() - 900000),
			level: "error",
			message: "JWT token validation failed",
			path: "/api/auth/validate",
			method: "POST",
			statusCode: 401,
			duration: 150,
		}
	];

	useEffect(() => {
		if (isLive) {
			const interval = setInterval(() => {
				// Simulate metric updates
				setMetrics(prev => ({
					...prev,
					responseTime: prev.responseTime + (Math.random() - 0.5) * 20,
					errorRate: Math.max(0, prev.errorRate + (Math.random() - 0.5) * 0.2),
					requestsPerSecond: Math.max(0, prev.requestsPerSecond + (Math.random() - 0.5) * 5),
					activeUsers: Math.max(0, prev.activeUsers + Math.floor((Math.random() - 0.5) * 20)),
					cpuUsage: Math.max(0, Math.min(100, prev.cpuUsage + (Math.random() - 0.5) * 10)),
					memoryUsage: Math.max(0, Math.min(100, prev.memoryUsage + (Math.random() - 0.5) * 5)),
				}));
				setLastUpdate(new Date());
			}, 5000);

			return () => clearInterval(interval);
		}
	}, [isLive]);

	const getHealthStatus = () => {
		if (metrics.uptime >= 99.5 && metrics.errorRate < 1.0) return "healthy";
		if (metrics.uptime >= 95.0 && metrics.errorRate < 5.0) return "warning";
		return "critical";
	};

	const getMetricColor = (value: number, type: "cpu" | "memory" | "response" | "error") => {
		switch (type) {
			case "cpu":
			case "memory":
				if (value > 80) return "text-red-400";
				if (value > 60) return "text-yellow-400";
				return "text-green-400";
			case "response":
				if (value > 500) return "text-red-400";
				if (value > 200) return "text-yellow-400";
				return "text-green-400";
			case "error":
				if (value > 2) return "text-red-400";
				if (value > 0.5) return "text-yellow-400";
				return "text-green-400";
			default:
				return "text-white";
		}
	};

	const formatUptime = (uptime: number) => {
		return `${uptime.toFixed(2)}%`;
	};

	const formatDuration = (duration: number) => {
		if (duration > 1000) return `${(duration / 1000).toFixed(1)}s`;
		return `${duration}ms`;
	};

	const getLevelIcon = (level: "error" | "warning" | "info") => {
		switch (level) {
			case "error":
				return <AlertTriangle className="w-4 h-4 text-red-400" />;
			case "warning":
				return <AlertTriangle className="w-4 h-4 text-yellow-400" />;
			case "info":
				return <CheckCircle className="w-4 h-4 text-blue-400" />;
		}
	};

	const healthStatus = getHealthStatus();

	// Derived UI constants (avoid nested ternaries in JSX for readability & lint compliance)
	let healthBadgeLabel = 'Crítico';
	let healthBadgeClasses = 'bg-red-100 text-red-800 border border-red-200';

	if (healthStatus === 'healthy') {
		healthBadgeLabel = 'Saludable';
		healthBadgeClasses = 'bg-green-100 text-green-800 border border-green-200';
	} else if (healthStatus === 'warning') {
		healthBadgeLabel = 'Advertencia';
		healthBadgeClasses = 'bg-yellow-100 text-yellow-800 border border-yellow-200';
	}

	const getResponseTimeColor = () => {
		const color = getMetricColor(metrics.responseTime, 'response');
		if (color.includes('red')) return 'text-red-600';
		if (color.includes('yellow')) return 'text-yellow-600';
		return 'text-blue-900';
	};

	const getErrorRateColor = () => {
		const color = getMetricColor(metrics.errorRate, 'error');
		if (color.includes('red')) return 'text-red-600';
		if (color.includes('yellow')) return 'text-yellow-600';
		return 'text-yellow-900';
	};

	const tabs = [
		{ id: 'overview', label: 'Resumen', icon: BarChart3 },
		{ id: 'errors', label: 'Errores', icon: AlertTriangle },
		{ id: 'performance', label: 'Rendimiento', icon: TrendingUp },
		{ id: 'config', label: 'Configuración', icon: Settings }
	] as const;

	return (
		<div className="bg-gradient-to-br from-gray-50 via-white to-blue-50 min-h-screen">
			<div className="container-app pt-10 pb-24 space-y-8 max-w-7xl mx-auto">
				<PageHeader
					title={
						<span className="font-extrabold text-primary-700 tracking-tight">
							Observabilidad del Sistema
						</span>
					}
					subtitle="Monitoreo en tiempo real, métricas de rendimiento y configuración avanzada de la plataforma"
					actions={
						<div className="flex items-center gap-4">
							<div className="flex items-center gap-2 px-4 py-2 bg-white/80 backdrop-blur-sm rounded-xl border border-gray-200 shadow-sm text-sm font-medium text-gray-700" aria-live="polite">
								<span className={`inline-flex w-2 h-2 rounded-full ${isLive ? 'bg-green-500 animate-pulse' : 'bg-gray-400'}`} />
								{isLive ? 'En vivo' : 'Pausado'} • {lastUpdate.toLocaleTimeString()}
							</div>
							<button
								onClick={() => setIsLive(!isLive)}
								className={`px-6 py-2 rounded-xl font-semibold transition-all duration-200 ${
									isLive
										? 'bg-white/80 border border-primary-200 text-primary-700 hover:bg-white shadow-sm backdrop-blur-sm'
										: 'bg-primary-600 text-white hover:bg-primary-700 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5'
								}`}
							>
								{isLive ? 'Pausar' : 'Activar'}
							</button>
						</div>
					}
				/>

				<section aria-labelledby="health-heading" className="space-y-6">
					<h2 id="health-heading" className="sr-only">Estado del sistema</h2>
					<motion.div
						initial={{ opacity: 0, y: 20 }}
						animate={{ opacity: 1, y: 0 }}
						transition={{ duration: 0.5 }}
					>
						<div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
							<div className="p-8">
								<div className="flex items-center justify-between mb-6 flex-wrap gap-4">
									<h3 className="text-2xl font-bold text-gray-900">Estado del Sistema</h3>
									<div className={`px-4 py-2 rounded-xl font-semibold text-sm ${healthBadgeClasses}`}>
										{healthBadgeLabel}
									</div>
								</div>
								<div className="grid grid-cols-2 md:grid-cols-4 gap-6">
									<div className="text-center p-6 bg-gradient-to-br from-green-50 to-green-100 rounded-xl border border-green-200">
										<Activity className="w-8 h-8 mx-auto mb-3 text-green-600" />
										<div className="text-2xl font-bold text-green-900 mb-1">{formatUptime(metrics.uptime)}</div>
										<p className="text-sm font-medium text-green-700">Disponibilidad</p>
									</div>
									<div className="text-center p-6 bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl border border-blue-200">
										<Clock className="w-8 h-8 mx-auto mb-3 text-blue-600" />
										<div className={`text-2xl font-bold mb-1 ${getResponseTimeColor()}`}>
											{Math.round(metrics.responseTime)}ms
										</div>
										<p className="text-sm font-medium text-blue-700">Tiempo Respuesta</p>
									</div>
									<div className="text-center p-6 bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-xl border border-yellow-200">
										<AlertTriangle className="w-8 h-8 mx-auto mb-3 text-yellow-600" />
										<div className={`text-2xl font-bold mb-1 ${getErrorRateColor()}`}>
											{metrics.errorRate.toFixed(1)}%
										</div>
										<p className="text-sm font-medium text-yellow-700">Tasa de Error</p>
									</div>
									<div className="text-center p-6 bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl border border-purple-200">
										<Users className="w-8 h-8 mx-auto mb-3 text-purple-600" />
										<div className="text-2xl font-bold text-purple-900 mb-1">{metrics.activeUsers}</div>
										<p className="text-sm font-medium text-purple-700">Usuarios Activos</p>
									</div>
								</div>
							</div>
						</div>
					</motion.div>
				</section>

				<nav aria-label="Secciones de observabilidad" className="pt-2">
					<div role="tablist" aria-orientation="horizontal" className="flex gap-2 bg-white/80 backdrop-blur-sm rounded-2xl p-2 shadow-lg border border-gray-200 overflow-x-auto">
						{tabs.map(tab => {
							const selected = activeTab === tab.id;
							return (
								<button
									key={tab.id}
									role="tab"
									aria-selected={selected}
									aria-controls={`panel-${tab.id}`}
									id={`tab-${tab.id}`}
									onClick={() => setActiveTab(tab.id)}
									className={`flex items-center gap-3 px-6 py-3 rounded-xl text-sm font-semibold focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-primary-500 transition-all duration-200 whitespace-nowrap ${
										selected
											? 'bg-primary-600 text-white shadow-lg transform scale-105'
											: 'text-gray-600 hover:text-gray-900 hover:bg-white/60 hover:shadow-md'
									}`}
								>
									<tab.icon className="w-5 h-5" />
									<span>{tab.label}</span>
								</button>
							);
						})}
					</div>
				</nav>

				<div role="tabpanel" id={`panel-${activeTab}`} aria-labelledby={`tab-${activeTab}`} className="mt-8">
					{activeTab === 'overview' && (
						<div className="space-y-8">
							<motion.div
								initial={{ opacity: 0, y: 20 }}
								animate={{ opacity: 1, y: 0 }}
								transition={{ duration: 0.5, delay: 0.1 }}
							>
								<div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
									<div className="p-8 space-y-6">
										<h3 className="text-2xl font-bold text-gray-900 flex items-center gap-3">
											<Server className="w-6 h-6 text-blue-500" />
											Uso de Recursos del Servidor
										</h3>
										<div className="grid gap-8 md:grid-cols-3">
											{[
												{ label: 'CPU', value: metrics.cpuUsage, type: 'cpu' as const, color: 'blue', icon: '🔥' },
												{ label: 'Memoria', value: metrics.memoryUsage, type: 'memory' as const, color: 'purple', icon: '💾' },
												{ label: 'Disco', value: metrics.diskUsage, type: 'disk' as const, color: 'indigo', icon: '💿' }
											].map(m => {
												let barColor = 'bg-green-500';
												let textColor = 'text-green-600';
												let bgGradient = 'from-green-50 to-green-100';
												let borderColor = 'border-green-200';

												if (m.type === 'disk') {
													barColor = 'bg-indigo-500';
													textColor = 'text-indigo-600';
													bgGradient = 'from-indigo-50 to-indigo-100';
													borderColor = 'border-indigo-200';
												} else if (m.value > 80) {
													barColor = 'bg-red-500';
													textColor = 'text-red-600';
													bgGradient = 'from-red-50 to-red-100';
													borderColor = 'border-red-200';
												} else if (m.value > 60) {
													barColor = 'bg-yellow-500';
													textColor = 'text-yellow-600';
													bgGradient = 'from-yellow-50 to-yellow-100';
													borderColor = 'border-yellow-200';
												}

												return (
													<div key={m.label} className={`p-6 bg-gradient-to-br ${bgGradient} rounded-xl border ${borderColor}`}>
														<div className="space-y-4">
															<div className="flex items-center justify-between">
																<span className="text-lg font-semibold text-gray-900">{m.icon} {m.label}</span>
																<span className={`text-2xl font-bold ${textColor}`}>{m.value.toFixed(1)}%</span>
															</div>
															<div className="space-y-2">
																<div className="h-3 rounded-full bg-white/80 border border-gray-200 overflow-hidden shadow-inner">
																	<div
																		className={`h-3 rounded-full transition-all duration-1000 ease-out ${barColor}`}
																		style={{ width: `${Math.min(m.value, 100)}%` }}
																	/>
																</div>
																<p className="text-sm text-gray-600 font-medium">
																	{(() => {
																		if (m.value > 80) return 'Uso Alto';
																		if (m.value > 60) return 'Uso Moderado';
																		return 'Uso Normal';
																	})()}
																</p>
															</div>
														</div>
													</div>
												);
											})}
										</div>
									</div>
								</div>
							</motion.div>

							<motion.div
								initial={{ opacity: 0, y: 20 }}
								animate={{ opacity: 1, y: 0 }}
								transition={{ duration: 0.5, delay: 0.2 }}
							>
								<div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
									<div className="p-8 space-y-6">
										<h3 className="text-2xl font-bold text-gray-900 flex items-center gap-3">
											<LineChart className="w-6 h-6 text-green-500" />
											Rendimiento en Tiempo Real
											<span className="text-sm font-normal text-gray-500">(Últimas 24 horas)</span>
										</h3>
										<div className="h-80 bg-gradient-to-br from-gray-50 to-white rounded-xl p-4 border border-gray-100">
											<ResponsiveContainer width="100%" height="100%">
												<RechartsLine data={performanceData}>
													<CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
													<XAxis dataKey="time" stroke="#6B7280" fontSize={12} />
													<YAxis stroke="#6B7280" fontSize={12} />
													<Tooltip
														contentStyle={{
															backgroundColor: 'white',
															border: '1px solid #D1D5DB',
															borderRadius: '12px',
															boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)'
														}}
													/>
													<Line
														type="monotone"
														dataKey="responseTime"
														stroke="#3B82F6"
														strokeWidth={3}
														name="Tiempo Respuesta (ms)"
														dot={{ fill: '#3B82F6', strokeWidth: 2, r: 4 }}
													/>
													<Line
														type="monotone"
														dataKey="requests"
														stroke="#10B981"
														strokeWidth={3}
														name="Peticiones/min"
														dot={{ fill: '#10B981', strokeWidth: 2, r: 4 }}
													/>
												</RechartsLine>
											</ResponsiveContainer>
										</div>
									</div>
								</div>
							</motion.div>
						</div>
					)}

					{activeTab === 'errors' && (
						<motion.div
							initial={{ opacity: 0, y: 20 }}
							animate={{ opacity: 1, y: 0 }}
							transition={{ duration: 0.5 }}
						>
							<div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
								<div className="p-8 space-y-6">
									<div className="flex items-center justify-between flex-wrap gap-4">
										<h3 className="text-2xl font-bold text-gray-900 flex items-center gap-3">
											<AlertTriangle className="w-6 h-6 text-red-500" />
											Registro de Errores Recientes
										</h3>
										<button className="px-6 py-3 bg-primary-600 text-white rounded-xl font-semibold hover:bg-primary-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 inline-flex items-center gap-2">
											<Download className="w-5 h-5" />
											Exportar Logs
										</button>
									</div>
									<ul className="space-y-4" aria-label="Lista de errores recientes">
										{errorLogs.map(log => {
											const getStatusCodeClasses = () => {
												if (log.statusCode >= 500) return 'bg-red-100 text-red-800';
												if (log.statusCode >= 400) return 'bg-yellow-100 text-yellow-800';
												return 'bg-green-100 text-green-800';
											};

											return (
												<li key={log.id} className="group">
													<div className="p-6 bg-gradient-to-r from-red-50 via-white to-red-50 rounded-xl border-l-4 border-red-500 hover:shadow-lg transition-all duration-200 hover:border-red-600">
														<div className="flex items-start justify-between gap-4">
															<div className="flex items-start gap-4">
																<div className="p-2 bg-red-100 rounded-lg">
																	{getLevelIcon(log.level)}
																</div>
																<div className="space-y-2">
																	<div className="flex items-center gap-3 flex-wrap">
																		<p className="text-lg font-semibold text-gray-900">{log.message}</p>
																		<div className={`px-3 py-1 rounded-lg text-xs font-bold ${getStatusCodeClasses()}`}>
																			{log.statusCode}
																		</div>
																	</div>
																	<div className="space-y-1">
																		<p className="text-sm text-gray-600 font-medium">
																			<span className="font-semibold text-gray-900">{log.method}</span> {log.path}
																		</p>
																		<p className="text-sm text-gray-500">
																			Duración: <span className="font-semibold">{formatDuration(log.duration)}</span>
																		</p>
																		{log.userAgent && (
																			<p className="text-xs text-gray-400 max-w-md truncate" title={log.userAgent}>
																				User Agent: {log.userAgent}
																			</p>
																		)}
																	</div>
																</div>
															</div>
															<div className="text-right">
																<time className="text-sm font-medium text-gray-500" dateTime={log.timestamp.toISOString()}>
																	{log.timestamp.toLocaleTimeString()}
																</time>
																<p className="text-xs text-gray-400 mt-1">
																	{log.timestamp.toLocaleDateString()}
																</p>
															</div>
														</div>
													</div>
												</li>
											);
										})}
									</ul>
								</div>
							</div>
						</motion.div>
					)}

					{activeTab === 'performance' && (
						<motion.div
							initial={{ opacity: 0, y: 20 }}
							animate={{ opacity: 1, y: 0 }}
							transition={{ duration: 0.5 }}
						>
							<div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
								<div className="p-8 space-y-6">
									<h3 className="text-2xl font-bold text-gray-900 flex items-center gap-3">
										<BarChart3 className="w-6 h-6 text-purple-500" />
										Análisis de Rendimiento Detallado
									</h3>
									<div className="h-80 bg-gradient-to-br from-purple-50 to-white rounded-xl p-4 border border-purple-100">
										<ResponsiveContainer width="100%" height="100%">
											<BarChart data={performanceData}>
												<CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
												<XAxis dataKey="time" stroke="#6B7280" fontSize={12} />
												<YAxis stroke="#6B7280" fontSize={12} />
												<Tooltip
													contentStyle={{
														backgroundColor: 'white',
														border: '1px solid #D1D5DB',
														borderRadius: '12px',
														boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)'
													}}
												/>
												<Bar dataKey="requests" fill="#8B5CF6" name="Peticiones" radius={[4, 4, 0, 0]} />
												<Bar dataKey="errors" fill="#EF4444" name="Errores" radius={[4, 4, 0, 0]} />
											</BarChart>
										</ResponsiveContainer>
									</div>
								</div>
							</div>
						</motion.div>
					)}

					{activeTab === 'config' && (
						<motion.div
							initial={{ opacity: 0, y: 20 }}
							animate={{ opacity: 1, y: 0 }}
							transition={{ duration: 0.5 }}
						>
							<div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg border border-gray-200">
								<div className="p-8">
									<h3 className="text-2xl font-bold text-gray-900 flex items-center gap-3 mb-6">
										<Settings className="w-6 h-6 text-indigo-500" />
										Configuración del Sistema
									</h3>
									<AdminConfigPanel variant="full" />
								</div>
							</div>
						</motion.div>
					)}
				</div>
			</div>
		</div>
	);
};

export default ObservabilityDashboard;
