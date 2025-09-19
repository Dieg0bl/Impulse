import React, { useState, useEffect } from 'react';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';

interface Referral {
	id: string;
	code: string;
	createdAt: string;
	status: 'PENDING' | 'COMPLETED' | 'EXPIRED';
	reward?: number;
}

interface ReferralStats {
	totalReferrals: number;
	activeReferrals: number;
	totalRewards: number;
	pendingRewards: number;
}

const MyCode: React.FC = () => {
	const [referralCode, setReferralCode] = useState<string>('');
	const [stats, setStats] = useState<ReferralStats | null>(null);
	const [referrals, setReferrals] = useState<Referral[]>([]);
	const [loading, setLoading] = useState(true);
	const [copySuccess, setCopySuccess] = useState(false);
	const [shareSuccess, setShareSuccess] = useState(false);
	const [error, setError] = useState<string | null>(null);

	// Simular carga de datos
	useEffect(() => {
		const loadData = async () => {
			try {
				// Simular API call
				await new Promise(resolve => setTimeout(resolve, 1000));

				setReferralCode('IMPULSE2025');
				setStats({
					totalReferrals: 12,
					activeReferrals: 8,
					totalRewards: 240,
					pendingRewards: 60
				});
				setReferrals([
					{ id: '1', code: 'IMPULSE2025', createdAt: '2025-09-15', status: 'COMPLETED', reward: 20 },
					{ id: '2', code: 'IMPULSE2025', createdAt: '2025-09-14', status: 'PENDING' },
					{ id: '3', code: 'IMPULSE2025', createdAt: '2025-09-13', status: 'COMPLETED', reward: 20 },
					{ id: '4', code: 'IMPULSE2025', createdAt: '2025-09-12', status: 'EXPIRED' },
				]);
			} catch (err: any) {
				setError('Error cargando datos de referidos');
			} finally {
				setLoading(false);
			}
		};

		loadData();
	}, []);

	const handleCopyCode = async () => {
		try {
			await navigator.clipboard.writeText(referralCode);
			setCopySuccess(true);
			setTimeout(() => setCopySuccess(false), 2000);
		} catch (err: any) {
			setError('Error copiando código');
		}
	};

	const handleShare = async (platform: string) => {
		const shareUrl = `https://impulse.app/referral/${referralCode}`;
		const shareText = `Únete a Impulse y obtén recompensas. Usa mi código: ${referralCode}`;

		try {
			if (navigator.share) {
				await navigator.share({
					title: 'Únete a Impulse',
					text: shareText,
					url: shareUrl
				});
			} else {
				// Fallback para compartir
				const urls = {
					whatsapp: `https://wa.me/?text=${encodeURIComponent(shareText)}`,
					twitter: `https://twitter.com/intent/tweet?text=${encodeURIComponent(shareText)}&url=${encodeURIComponent(shareUrl)}`,
					facebook: `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(shareUrl)}`
				};

				if (platform in urls) {
					window.open(urls[platform as keyof typeof urls], '_blank');
				}
			}
			setShareSuccess(true);
			setTimeout(() => setShareSuccess(false), 2000);
		} catch (err: any) {
			setError('Error compartiendo código');
		}
	};

	const getStatusColor = (status: string) => {
		switch (status) {
			case 'COMPLETED': return 'success';
			case 'PENDING': return 'warning';
			case 'EXPIRED': return 'error';
			default: return 'secondary';
		}
	};

	const getStatusText = (status: string) => {
		switch (status) {
			case 'COMPLETED': return 'Completado';
			case 'PENDING': return 'Pendiente';
			case 'EXPIRED': return 'Expirado';
			default: return status;
		}
	};

	if (loading) {
		return (
			<div className="container-app">
				<div className="max-w-4xl mx-auto space-y-8">
					<div className="animate-pulse">
						<div className="h-8 bg-gray-200 rounded w-1/4 mb-4"></div>
						<div className="h-32 bg-gray-200 rounded mb-8"></div>
						<div className="h-24 bg-gray-200 rounded mb-4"></div>
						<div className="space-y-3">
							<div className="h-4 bg-gray-200 rounded"></div>
							<div className="h-4 bg-gray-200 rounded w-3/4"></div>
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
					<h1 className="text-3xl font-bold text-gray-900 mb-2">Mi Código de Referidos</h1>
					<p className="text-gray-600">
						Comparte tu código y gana recompensas por cada amigo que se una
					</p>
				</div>

				{/* Código y compartir */}
				<div className="bg-white border border-gray-200 rounded-lg p-6">
					<div className="text-center space-y-6">
						<div>
							<h2 className="text-xl font-semibold text-gray-900 mb-2">Tu Código</h2>
							<div className="inline-flex items-center gap-4 bg-gray-50 border border-gray-200 rounded-lg px-6 py-4">
								<code className="text-2xl font-mono font-bold text-primary-600">
									{referralCode}
								</code>
								<Button
									onClick={handleCopyCode}
									variant="outline"
									size="sm"
									aria-label="Copiar código de referido"
								>
									{copySuccess ? '¡Copiado!' : 'Copiar'}
								</Button>
							</div>
						</div>

						<div>
							<h3 className="text-lg font-medium text-gray-900 mb-4">Compartir</h3>
							<div className="flex flex-wrap justify-center gap-3">
								<Button
									onClick={() => handleShare('whatsapp')}
									variant="outline"
									className="flex items-center gap-2"
									aria-label="Compartir en WhatsApp"
								>
									<svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
										<path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893A11.821 11.821 0 0020.885 3.488"/>
									</svg>
									WhatsApp
								</Button>
								<Button
									onClick={() => handleShare('twitter')}
									variant="outline"
									className="flex items-center gap-2"
									aria-label="Compartir en Twitter"
								>
									<svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
										<path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"/>
									</svg>
									Twitter
								</Button>
								<Button
									onClick={() => handleShare('facebook')}
									variant="outline"
									className="flex items-center gap-2"
									aria-label="Compartir en Facebook"
								>
									<svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
										<path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
									</svg>
									Facebook
								</Button>
							</div>
							{shareSuccess && (
								<div className="mt-4 text-green-600 font-medium" role="alert">
									¡Enlace compartido exitosamente!
								</div>
							)}
						</div>
					</div>
				</div>

				{/* Estadísticas */}
				{stats && (
					<div className="grid grid-cols-1 md:grid-cols-4 gap-4">
						<div className="bg-white border border-gray-200 rounded-lg p-4 text-center">
							<div className="text-2xl font-bold text-primary-600">{stats.totalReferrals}</div>
							<div className="text-sm text-gray-600">Total referidos</div>
						</div>
						<div className="bg-white border border-gray-200 rounded-lg p-4 text-center">
							<div className="text-2xl font-bold text-green-600">{stats.activeReferrals}</div>
							<div className="text-sm text-gray-600">Referidos activos</div>
						</div>
						<div className="bg-white border border-gray-200 rounded-lg p-4 text-center">
							<div className="text-2xl font-bold text-blue-600">€{stats.totalRewards}</div>
							<div className="text-sm text-gray-600">Recompensas totales</div>
						</div>
						<div className="bg-white border border-gray-200 rounded-lg p-4 text-center">
							<div className="text-2xl font-bold text-yellow-600">€{stats.pendingRewards}</div>
							<div className="text-sm text-gray-600">Recompensas pendientes</div>
						</div>
					</div>
				)}

				{/* Historial de referidos */}
				<div className="bg-white border border-gray-200 rounded-lg p-6">
					<h2 className="text-xl font-semibold text-gray-900 mb-4">Historial de Referidos</h2>
					{referrals.length === 0 ? (
						<div className="text-center py-8 text-gray-500">
							<p>Aún no tienes referidos</p>
							<p className="text-sm mt-2">Comparte tu código para empezar a ganar recompensas</p>
						</div>
					) : (
						<div className="space-y-3">
							{referrals.map((referral) => (
								<div key={referral.id} className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
									<div className="flex items-center gap-4">
										<div>
											<div className="font-medium text-gray-900">
												Referido #{referral.id}
											</div>
											<div className="text-sm text-gray-600">
												{new Date(referral.createdAt).toLocaleDateString('es-ES')}
											</div>
										</div>
									</div>
									<div className="flex items-center gap-4">
										{referral.reward && (
											<div className="text-sm font-medium text-green-600">
												+€{referral.reward}
											</div>
										)}
										<Badge variant={getStatusColor(referral.status)}>
											{getStatusText(referral.status)}
										</Badge>
									</div>
								</div>
							))}
						</div>
					)}
				</div>

				{/* Información adicional */}
				<div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
					<div className="flex items-start gap-3">
						<svg className="h-5 w-5 text-blue-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
							<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
						</svg>
						<div className="text-blue-800">
							<h3 className="font-medium mb-1">Información importante</h3>
							<ul className="text-sm space-y-1">
								<li>• Los referidos deben completar su registro para contar</li>
								<li>• Las recompensas se acreditan cuando el referido es activo por 30 días</li>
								<li>• Máximo 10 referidos por mes para evitar abuso</li>
								<li>• Las recompensas se pagan mensualmente</li>
							</ul>
						</div>
					</div>
				</div>

				{/* Mensajes de error */}
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

export default MyCode;
