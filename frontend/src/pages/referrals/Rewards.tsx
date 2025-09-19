import React, { useState, useEffect } from 'react';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';

interface Reward {
	id: string;
	amount: number;
	status: 'PENDING' | 'EARNED' | 'PAID' | 'CANCELED';
	description: string;
	earnedAt: string;
	paidAt?: string;
	referralId?: string;
	taxAmount?: number;
}

interface RewardStats {
	totalEarned: number;
	totalPaid: number;
	pendingAmount: number;
	nextPayoutDate?: string;
}

const Rewards: React.FC = () => {
	const [rewards, setRewards] = useState<Reward[]>([]);
	const [stats, setStats] = useState<RewardStats | null>(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const [filter, setFilter] = useState<'ALL' | 'PENDING' | 'EARNED' | 'PAID' | 'CANCELED'>('ALL');

	// Simular carga de datos
	useEffect(() => {
		const loadData = async () => {
			try {
				await new Promise(resolve => setTimeout(resolve, 1000));

				const mockRewards: Reward[] = [
					{
						id: '1',
						amount: 20,
						status: 'PAID',
						description: 'Referido #123 completó 30 días',
						earnedAt: '2025-08-15',
						paidAt: '2025-09-01',
						referralId: '123',
						taxAmount: 4
					},
					{
						id: '2',
						amount: 20,
						status: 'EARNED',
						description: 'Referido #124 completó 30 días',
						earnedAt: '2025-09-01',
						referralId: '124'
					},
					{
						id: '3',
						amount: 20,
						status: 'PENDING',
						description: 'Referido #125 en proceso de activación',
						earnedAt: '2025-09-10',
						referralId: '125'
					},
					{
						id: '4',
						amount: 15,
						status: 'CANCELED',
						description: 'Referido #126 canceló cuenta antes de 30 días',
						earnedAt: '2025-08-20',
						referralId: '126'
					},
					{
						id: '5',
						amount: 20,
						status: 'PAID',
						description: 'Referido #127 completó 30 días',
						earnedAt: '2025-07-15',
						paidAt: '2025-08-01',
						referralId: '127',
						taxAmount: 4
					}
				];

				setRewards(mockRewards);

				const totalEarned = mockRewards
					.filter(r => r.status !== 'CANCELED')
					.reduce((sum, r) => sum + r.amount, 0);

				const totalPaid = mockRewards
					.filter(r => r.status === 'PAID')
					.reduce((sum, r) => sum + r.amount, 0);

				const pendingAmount = mockRewards
					.filter(r => r.status === 'EARNED')
					.reduce((sum, r) => sum + r.amount, 0);

				setStats({
					totalEarned,
					totalPaid,
					pendingAmount,
					nextPayoutDate: '2025-10-01'
				});

			} catch (err: any) {
				setError('Error cargando recompensas');
			} finally {
				setLoading(false);
			}
		};

		loadData();
	}, []);

	const getStatusColor = (status: string) => {
		switch (status) {
			case 'PAID': return 'success';
			case 'EARNED': return 'info';
			case 'PENDING': return 'warning';
			case 'CANCELED': return 'error';
			default: return 'secondary';
		}
	};

	const getStatusText = (status: string) => {
		switch (status) {
			case 'PAID': return 'Pagada';
			case 'EARNED': return 'Ganada';
			case 'PENDING': return 'Pendiente';
			case 'CANCELED': return 'Cancelada';
			default: return status;
		}
	};

	const getStatusIcon = (status: string) => {
		switch (status) {
			case 'PAID':
				return (
					<svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
						<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
					</svg>
				);
			case 'EARNED':
				return (
					<svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
						<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
					</svg>
				);
			case 'PENDING':
				return (
					<svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
						<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
					</svg>
				);
			case 'CANCELED':
				return (
					<svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
						<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
					</svg>
				);
			default:
				return null;
		}
	};

	const filteredRewards = rewards.filter(reward => {
		if (filter === 'ALL') return true;
		return reward.status === filter;
	});

	if (loading) {
		return (
			<div className="container-app">
				<div className="max-w-4xl mx-auto space-y-8">
					<div className="animate-pulse">
						<div className="h-8 bg-gray-200 rounded w-1/4 mb-4"></div>
						<div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
							<div className="h-24 bg-gray-200 rounded"></div>
							<div className="h-24 bg-gray-200 rounded"></div>
							<div className="h-24 bg-gray-200 rounded"></div>
						</div>
						<div className="space-y-3">
							<div className="h-16 bg-gray-200 rounded"></div>
							<div className="h-16 bg-gray-200 rounded"></div>
							<div className="h-16 bg-gray-200 rounded"></div>
						</div>
					</div>
				</div>
			</div>
		);
	}

	return (
		<div className="container-app">
			<div className="max-w-4xl mx-auto space-y-8">
				{/* Header */}
				<div className="text-center">
					<h1 className="text-3xl font-bold text-gray-900 mb-2">Mis Recompensas</h1>
					<p className="text-gray-600">
						Historial completo de tus ganancias por referidos
					</p>
				</div>

				{/* Estadísticas */}
				{stats && (
					<div className="grid grid-cols-1 md:grid-cols-3 gap-4">
						<div className="bg-white border border-gray-200 rounded-lg p-6 text-center">
							<div className="text-3xl font-bold text-green-600 mb-1">€{stats.totalEarned}</div>
							<div className="text-sm text-gray-600">Total ganado</div>
						</div>
						<div className="bg-white border border-gray-200 rounded-lg p-6 text-center">
							<div className="text-3xl font-bold text-blue-600 mb-1">€{stats.totalPaid}</div>
							<div className="text-sm text-gray-600">Total pagado</div>
						</div>
						<div className="bg-white border border-gray-200 rounded-lg p-6 text-center">
							<div className="text-3xl font-bold text-yellow-600 mb-1">€{stats.pendingAmount}</div>
							<div className="text-sm text-gray-600">Pendiente de pago</div>
							{stats.nextPayoutDate && (
								<div className="text-xs text-gray-500 mt-1">
									Próximo: {new Date(stats.nextPayoutDate).toLocaleDateString('es-ES')}
								</div>
							)}
						</div>
					</div>
				)}

				{/* Filtros */}
				<div className="bg-white border border-gray-200 rounded-lg p-4">
					<div className="flex flex-wrap gap-2">
						<Button
							variant={filter === 'ALL' ? 'default' : 'outline'}
							size="sm"
							onClick={() => setFilter('ALL')}
						>
							Todas ({rewards.length})
						</Button>
						<Button
							variant={filter === 'PENDING' ? 'default' : 'outline'}
							size="sm"
							onClick={() => setFilter('PENDING')}
						>
							Pendientes ({rewards.filter(r => r.status === 'PENDING').length})
						</Button>
						<Button
							variant={filter === 'EARNED' ? 'default' : 'outline'}
							size="sm"
							onClick={() => setFilter('EARNED')}
						>
							Ganadas ({rewards.filter(r => r.status === 'EARNED').length})
						</Button>
						<Button
							variant={filter === 'PAID' ? 'default' : 'outline'}
							size="sm"
							onClick={() => setFilter('PAID')}
						>
							Pagadas ({rewards.filter(r => r.status === 'PAID').length})
						</Button>
						<Button
							variant={filter === 'CANCELED' ? 'default' : 'outline'}
							size="sm"
							onClick={() => setFilter('CANCELED')}
						>
							Canceladas ({rewards.filter(r => r.status === 'CANCELED').length})
						</Button>
					</div>
				</div>

				{/* Lista de recompensas */}
				<div className="bg-white border border-gray-200 rounded-lg p-6">
					<h2 className="text-xl font-semibold text-gray-900 mb-4">Historial de Recompensas</h2>

					{filteredRewards.length === 0 ? (
						<div className="text-center py-12 text-gray-500">
							<svg className="mx-auto h-12 w-12 text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
								<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1" />
							</svg>
							<p className="text-lg font-medium mb-1">No hay recompensas en esta categoría</p>
							<p className="text-sm">Cambia el filtro para ver otras recompensas</p>
						</div>
					) : (
						<div className="space-y-4">
							{filteredRewards.map((reward) => (
								<div key={reward.id} className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors">
									<div className="flex items-center justify-between">
										<div className="flex items-center gap-4">
											<div className="flex-shrink-0">
												<div className={`p-2 rounded-full ${
													reward.status === 'PAID' ? 'bg-green-100' :
													reward.status === 'EARNED' ? 'bg-blue-100' :
													reward.status === 'PENDING' ? 'bg-yellow-100' :
													'bg-red-100'
												}`}>
													<div className={`${
														reward.status === 'PAID' ? 'text-green-600' :
														reward.status === 'EARNED' ? 'text-blue-600' :
														reward.status === 'PENDING' ? 'text-yellow-600' :
														'text-red-600'
													}`}>
														{getStatusIcon(reward.status)}
													</div>
												</div>
											</div>
											<div className="flex-1">
												<div className="flex items-center gap-2 mb-1">
													<h3 className="font-medium text-gray-900">{reward.description}</h3>
													<Badge variant={getStatusColor(reward.status)}>
														{getStatusText(reward.status)}
													</Badge>
												</div>
												<div className="text-sm text-gray-600 space-y-1">
													<p>Ganada el {new Date(reward.earnedAt).toLocaleDateString('es-ES')}</p>
													{reward.paidAt && (
														<p>Pagada el {new Date(reward.paidAt).toLocaleDateString('es-ES')}</p>
													)}
													{reward.referralId && (
														<p className="text-xs text-gray-500">ID de referido: {reward.referralId}</p>
													)}
												</div>
											</div>
										</div>
										<div className="text-right">
											<div className="text-xl font-bold text-gray-900 mb-1">
												€{reward.amount}
											</div>
											{reward.taxAmount && reward.taxAmount > 0 && (
												<div className="text-xs text-gray-500">
													Retención: €{reward.taxAmount}
												</div>
											)}
										</div>
									</div>
								</div>
							))}
						</div>
					)}
				</div>

				{/* Información fiscal */}
				<div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
					<div className="flex items-start gap-3">
						<svg className="h-5 w-5 text-blue-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
							<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
						</svg>
						<div className="text-blue-800">
							<h3 className="font-medium mb-1">Información fiscal</h3>
							<ul className="text-sm space-y-1">
								<li>• Las recompensas superiores a €600 anuales están sujetas a retención fiscal</li>
								<li>• Recibirás un certificado fiscal anual por email</li>
								<li>• Los pagos se realizan mensualmente el día 1</li>
								<li>• Puedes actualizar tus datos fiscales en Configuración</li>
							</ul>
						</div>
					</div>
				</div>

				{/* Mensaje de error */}
				{error && (
					<div className="bg-red-50 border border-red-200 rounded-lg p-4" role="alert">
						<div className="flex items-start gap-3">
							<svg className="h-5 w-5 text-red-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
								<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
							</svg>
							<div>
								<p className="text-red-800 font-medium">{error}</p>
							</div>
						</div>
					</div>
				)}
			</div>
		</div>
	);
};

export default Rewards;
