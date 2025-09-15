import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Card } from "../components/ui/Card";
import { Badge } from "../components/ui/Badge";
import AdminConfigPanel from "../components/AdminConfigPanel";
import {
	Activity,
	AlertTriangle,
	CheckCircle,
	Clock,
	Monitor,
	Server,
	TrendingUp,
	Settings,
	BarChart3,
	LineChart,
	Users,
	Download
} from "lucide-react";
import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Card } from "../components/ui/Card";
import { Badge } from "../components/ui/Badge";
import AdminConfigPanel from "../components/AdminConfigPanel";
import {
	Activity,
	AlertTriangle,
	CheckCircle,
	Clock,
	Monitor,
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

	return (
		<div className="min-h-screen bg-gray-900 text-white p-6">
			<div className="max-w-7xl mx-auto space-y-6">
				{/* Header */}
				<motion.div
					initial={{ opacity: 0, y: -20 }}
					animate={{ opacity: 1, y: 0 }}
					className="flex items-center justify-between"
				>
					<div className="flex items-center space-x-4">
						<div className="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-center">
							<Monitor className="w-5 h-5 text-white" />
						</div>
						<div>
							<h1 className="text-2xl font-bold text-white">Panel de Observabilidad</h1>
							<p className="text-gray-400">Monitoreo del sistema en tiempo real</p>
						</div>
					</div>

					<div className="flex items-center space-x-4">
						<div className="flex items-center space-x-2">
							<div className={`w-2 h-2 rounded-full ${isLive ? "bg-green-400" : "bg-gray-400"}`}></div>
							<span className="text-sm text-gray-400">
								{isLive ? "En vivo" : "Pausado"} • {lastUpdate.toLocaleTimeString()}
							</span>
						</div>
						<button
							onClick={() => setIsLive(!isLive)}
							className={`px-3 py-1 rounded text-sm transition-colors ${
								isLive ? "bg-green-600 hover:bg-green-500" : "bg-gray-600 hover:bg-gray-500"
							}`}
						>
							{isLive ? "Pausar" : "Activar"}
						</button>
					</div>
				</motion.div>

				{/* System Health Status */}
				<motion.div
					initial={{ opacity: 0, scale: 0.95 }}
					animate={{ opacity: 1, scale: 1 }}
					transition={{ delay: 0.1 }}
				>
					<Card>
						<div className="p-6">
							<div className="flex items-center justify-between mb-4">
								<h2 className="text-lg font-semibold text-white">Estado del Sistema</h2>
								<Badge
									variant={healthStatus === "healthy" ? "success" : healthStatus === "warning" ? "warning" : "error"}
									size="lg"
								>
									{healthStatus === "healthy" ? "Saludable" : healthStatus === "warning" ? "Advertencia" : "Crítico"}
								</Badge>
							</div>

							<div className="grid grid-cols-2 md:grid-cols-4 gap-4">
								<div className="text-center p-4 bg-gray-800/50 rounded-lg">
									<Activity className="w-6 h-6 mx-auto mb-2 text-green-400" />
									<div className="text-2xl font-bold text-white">{formatUptime(metrics.uptime)}</div>
									<div className="text-sm text-gray-400">Uptime</div>
								</div>
								<div className="text-center p-4 bg-gray-800/50 rounded-lg">
									<Clock className="w-6 h-6 mx-auto mb-2 text-blue-400" />
									<div className={`text-2xl font-bold ${getMetricColor(metrics.responseTime, "response")}`}>
										{Math.round(metrics.responseTime)}ms
									</div>
									<div className="text-sm text-gray-400">Tiempo Respuesta</div>
								</div>
								<div className="text-center p-4 bg-gray-800/50 rounded-lg">
									<AlertTriangle className="w-6 h-6 mx-auto mb-2 text-yellow-400" />
									<div className={`text-2xl font-bold ${getMetricColor(metrics.errorRate, "error")}`}>
										{metrics.errorRate.toFixed(1)}%
									</div>
									<div className="text-sm text-gray-400">Tasa de Error</div>
								</div>
								<div className="text-center p-4 bg-gray-800/50 rounded-lg">
									<Users className="w-6 h-6 mx-auto mb-2 text-purple-400" />
									<div className="text-2xl font-bold text-white">{metrics.activeUsers}</div>
									<div className="text-sm text-gray-400">Usuarios Activos</div>
								</div>
							</div>
						</div>
					</Card>
				</motion.div>

				{/* Tab Navigation */}
				<div className="flex space-x-1 bg-gray-800 rounded-lg p-1">
					{[
						{ id: "overview", label: "Resumen", icon: BarChart3 },
						{ id: "errors", label: "Errores", icon: AlertTriangle },
						{ id: "performance", label: "Rendimiento", icon: TrendingUp },
						{ id: "config", label: "Configuración", icon: Settings }
					].map((tab) => (
						<button
							key={tab.id}
							onClick={() => setActiveTab(tab.id as any)}
							className={`flex items-center space-x-2 px-4 py-2 rounded-md transition-colors ${
								activeTab === tab.id
									? "bg-blue-600 text-white"
									: "text-gray-400 hover:text-white hover:bg-gray-700"
							}`}
						>
							<tab.icon className="w-4 h-4" />
							<span>{tab.label}</span>
						</button>
					))}
				</div>

				{/* Tab Content */}
				<motion.div
					key={activeTab}
					initial={{ opacity: 0, x: 20 }}
					animate={{ opacity: 1, x: 0 }}
					transition={{ duration: 0.3 }}
				>
					{activeTab === "overview" && (
						<div className="space-y-6">
							{/* Resource Usage */}
							<Card>
								<div className="p-6">
									<h3 className="text-lg font-semibold text-white mb-4 flex items-center">
										<Server className="w-5 h-5 mr-2 text-blue-400" />
										Uso de Recursos
									</h3>

									<div className="grid grid-cols-1 md:grid-cols-3 gap-6">
										<div>
											<div className="flex justify-between text-sm mb-2">
												<span className="text-gray-400">CPU</span>
												<span className={getMetricColor(metrics.cpuUsage, "cpu")}>
													{metrics.cpuUsage.toFixed(1)}%
												</span>
											</div>
											<div className="w-full bg-gray-700 rounded-full h-2">
												<div
													className={`h-2 rounded-full transition-all duration-500 ${
														metrics.cpuUsage > 80 ? "bg-red-500" :
														metrics.cpuUsage > 60 ? "bg-yellow-500" : "bg-green-500"
													}`}
													style={{ width: `${Math.min(metrics.cpuUsage, 100)}%` }}
												></div>
											</div>
										</div>
										<div>
											<div className="flex justify-between text-sm mb-2">
												<span className="text-gray-400">Memoria</span>
												<span className={getMetricColor(metrics.memoryUsage, "memory")}>
													{metrics.memoryUsage.toFixed(1)}%
												</span>
											</div>
											<div className="w-full bg-gray-700 rounded-full h-2">
												<div
													className={`h-2 rounded-full transition-all duration-500 ${
														metrics.memoryUsage > 80 ? "bg-red-500" :
														metrics.memoryUsage > 60 ? "bg-yellow-500" : "bg-green-500"
													}`}
													style={{ width: `${Math.min(metrics.memoryUsage, 100)}%` }}
												></div>
											</div>
										</div>
										<div>
											<div className="flex justify-between text-sm mb-2">
												<span className="text-gray-400">Disco</span>
												<span className="text-blue-400">{metrics.diskUsage.toFixed(1)}%</span>
											</div>
											<div className="w-full bg-gray-700 rounded-full h-2">
												<div
													className="h-2 bg-blue-500 rounded-full transition-all duration-500"
													style={{ width: `${Math.min(metrics.diskUsage, 100)}%` }}
												></div>
											</div>
										</div>
									</div>
								</div>
							</Card>

							{/* Performance Chart */}
							<Card>
								<div className="p-6">
									<h3 className="text-lg font-semibold text-white mb-4 flex items-center">
										<LineChart className="w-5 h-5 mr-2 text-green-400" />
										Rendimiento (Últimas 24h)
									</h3>

									<div className="h-64">
										<ResponsiveContainer width="100%" height="100%">
											<RechartsLine data={performanceData}>
												<CartesianGrid strokeDasharray="3 3" stroke="#374151" />
												<XAxis dataKey="time" stroke="#9CA3AF" />
												<YAxis stroke="#9CA3AF" />
												<Tooltip
													contentStyle={{
														backgroundColor: '#1F2937',
														border: '1px solid #374151',
														borderRadius: '8px'
													}}
												/>
												<Line
													type="monotone"
													dataKey="responseTime"
													stroke="#3B82F6"
													strokeWidth={2}
													name="Tiempo Respuesta (ms)"
												/>
												<Line
													type="monotone"
													dataKey="requests"
													stroke="#10B981"
													strokeWidth={2}
													name="Peticiones/min"
												/>
											</RechartsLine>
										</ResponsiveContainer>
									</div>
								</div>
							</Card>
						</div>
					)}

					{activeTab === "errors" && (
						<Card>
							<div className="p-6">
								<div className="flex items-center justify-between mb-4">
									<h3 className="text-lg font-semibold text-white flex items-center">
										<AlertTriangle className="w-5 h-5 mr-2 text-red-400" />
										Logs de Errores Recientes
									</h3>
									<button className="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-500 transition-colors text-sm">
										<Download className="w-4 h-4 inline mr-1" />
										Exportar
									</button>
								</div>

								<div className="space-y-3">
									{errorLogs.map((log) => (
										<div key={log.id} className="p-4 bg-gray-800/50 rounded-lg border-l-4 border-red-500">
											<div className="flex items-start justify-between">
												<div className="flex items-start space-x-3">
													{getLevelIcon(log.level)}
													<div className="flex-1">
														<div className="flex items-center space-x-2 mb-1">
															<span className="font-medium text-white">{log.message}</span>
															<Badge variant="error" size="sm">
																{log.statusCode}
															</Badge>
														</div>
														<div className="text-sm text-gray-400">
															{log.method} {log.path} • {formatDuration(log.duration)}
														</div>
														{log.userAgent && (
															<div className="text-xs text-gray-500 mt-1">{log.userAgent}</div>
														)}
													</div>
												</div>
												<div className="text-xs text-gray-400">
													{log.timestamp.toLocaleTimeString()}
												</div>
											</div>
										</div>
									))}
								</div>
							</div>
						</Card>
					)}

					{activeTab === "performance" && (
						<div className="space-y-6">
							<Card>
								<div className="p-6">
									<h3 className="text-lg font-semibold text-white mb-4 flex items-center">
										<BarChart3 className="w-5 h-5 mr-2 text-purple-400" />
										Métricas de Rendimiento
									</h3>

									<div className="h-64">
										<ResponsiveContainer width="100%" height="100%">
											<BarChart data={performanceData}>
												<CartesianGrid strokeDasharray="3 3" stroke="#374151" />
												<XAxis dataKey="time" stroke="#9CA3AF" />
												<YAxis stroke="#9CA3AF" />
												<Tooltip
													contentStyle={{
														backgroundColor: '#1F2937',
														border: '1px solid #374151',
														borderRadius: '8px'
													}}
												/>
												<Bar dataKey="requests" fill="#8B5CF6" name="Peticiones" />
												<Bar dataKey="errors" fill="#EF4444" name="Errores" />
											</BarChart>
										</ResponsiveContainer>
									</div>
								</div>
							</Card>
						</div>
					)}

					{activeTab === "config" && (
						<AdminConfigPanel variant="full" />
					)}
				</motion.div>
			</div>
		</div>
	);
};

export default ObservabilityDashboard;
interface SystemMetrics {
