
import React, { useState, useEffect } from 'react';
import { userApi } from '../../services/api';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';

const DeleteAccount: React.FC = () => {
	const [checked, setChecked] = useState(false);
	const [confirmText, setConfirmText] = useState('');
	const [loading, setLoading] = useState(false);
	const [success, setSuccess] = useState<string | null>(null);
	const [error, setError] = useState<string | null>(null);
	const [correlationId, setCorrelationId] = useState<string | null>(null);
	const [deleteRequestedAt, setDeleteRequestedAt] = useState<Date | null>(null);
	const [countdown, setCountdown] = useState<string>('');
	const [roleChecked, setRoleChecked] = useState(false);
	const [isUser, setIsUser] = useState<boolean | null>(null);

	// Validar RBAC al montar
	useEffect(() => {
		let mounted = true;
			userApi.getCurrentUser().then(u => {
				if (!mounted) return;
				// Solo permitimos si el usuario está ACTIVE (no hay roles en el DTO)
				setIsUser(u && u.status === 'ACTIVE');
				setRoleChecked(true);
			}).catch(() => {
				setIsUser(false);
				setRoleChecked(true);
			});
		return () => { mounted = false; };
	}, []);

	const canDelete = checked && confirmText.trim().toUpperCase() === 'BORRAR' && !loading;

	const handleDelete = async () => {
		setLoading(true);
		setError(null);
		setSuccess(null);
		setCorrelationId(null);
		try {
			const user = await userApi.getCurrentUser();
			await userApi.deleteUser(user.id);
			setSuccess('Solicitud de borrado enviada correctamente. Recibirás un email de confirmación.');
			const now = new Date();
			setDeleteRequestedAt(now);
		} catch (err: any) {
			setError(err.message || 'Error solicitando el borrado de cuenta');
			if (err.correlationId) setCorrelationId(err.correlationId);
		} finally {
			setLoading(false);
		}
	};

	// Countdown de 14 días
	useEffect(() => {
		if (!deleteRequestedAt) return;
		const interval = setInterval(() => {
			const now = new Date();
			const end = new Date(deleteRequestedAt.getTime() + 14 * 24 * 60 * 60 * 1000);
			const diff = end.getTime() - now.getTime();
			if (diff <= 0) {
				setCountdown('El periodo de reversibilidad ha finalizado.');
				clearInterval(interval);
				return;
			}
			const days = Math.floor(diff / (24 * 60 * 60 * 1000));
			const hours = Math.floor((diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000));
			const minutes = Math.floor((diff % (60 * 60 * 1000)) / (60 * 1000));
			setCountdown(`${days} días, ${hours}h, ${minutes}m restantes para cancelar el borrado.`);
		}, 1000 * 30);
		return () => clearInterval(interval);
	}, [deleteRequestedAt]);

	if (!roleChecked) {
		return (
			<div className="container-app">
				<div className="flex items-center justify-center min-h-[200px]">
					<div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
					<span className="ml-2 text-gray-600">Cargando...</span>
				</div>
			</div>
		);
	}

	if (isUser === false) {
		return (
			<div className="container-app">
				<div className="max-w-2xl mx-auto">
					<div className="bg-red-50 border border-red-200 rounded-lg p-6" role="alert">
						<h1 className="text-xl font-semibold text-red-800 mb-2">Acceso denegado</h1>
						<p className="text-red-700">
							Solo los usuarios activos pueden solicitar el borrado de cuenta.
						</p>
					</div>
				</div>
			</div>
		);
	}

	return (
		<div className="container-app">
			<div className="max-w-2xl mx-auto space-y-8">
				{/* Header */}
				<div className="text-center">
					<h1 className="text-3xl font-bold text-red-700 mb-2">Eliminar cuenta</h1>
					<p className="text-gray-600">
						Esta acción es irreversible. Lee atentamente las consecuencias antes de continuar.
					</p>
				</div>
				{/* Advertencias */}
				<div className="bg-red-50 border border-red-200 rounded-lg p-6">
					<div className="flex items-start gap-3">
						<div className="flex-shrink-0">
							<svg className="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
								<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z" />
							</svg>
						</div>
						<div className="space-y-3">
							<h2 className="text-lg font-semibold text-red-800">
								Consecuencias de eliminar tu cuenta
							</h2>
							<div className="text-red-700 space-y-2">
								<p>
									<strong>Esta acción eliminará permanentemente tu cuenta y todos tus datos asociados.</strong>
									El borrado es reversible durante <Badge variant="warning">14 días</Badge> desde la solicitud.
								</p>
								<ul className="list-disc pl-5 space-y-1">
									<li>Se eliminarán tus datos de perfil, retos, evidencias y participaciones</li>
									<li>Se conservarán registros mínimos por obligaciones legales (<span className="font-medium">RGPD art. 32</span>)</li>
									<li>Durante el período de reversibilidad puedes cancelar desde tu email</li>
								</ul>
							</div>
							<div className="bg-yellow-50 border border-yellow-200 rounded p-3">
								<p className="text-yellow-800 text-sm">
									<strong>Nota legal:</strong> Algunos datos pueden retenerse por facturación, prevención de fraude o cumplimiento normativo.
								</p>
							</div>
						</div>
					</div>
				</div>

				{/* Formulario de confirmación */}
				<div className="bg-white border border-gray-200 rounded-lg p-6 space-y-6">
					<h2 className="text-xl font-semibold text-gray-900">Confirmación requerida</h2>

					{/* Checkbox de confirmación */}
					<div className="flex items-start gap-3">
						<input
							id="confirm-understanding"
							type="checkbox"
							checked={checked}
							onChange={e => setChecked(e.target.checked)}
							className="mt-1 h-4 w-4 text-red-600 border-gray-300 rounded focus:ring-red-500"
						/>
						<label htmlFor="confirm-understanding" className="text-sm text-gray-700 leading-5">
							Confirmo que entiendo las consecuencias y deseo solicitar el borrado de mi cuenta.
						</label>
					</div>

					{/* Campo de confirmación */}
					<div>
						<label htmlFor="confirm-text" className="block text-sm font-medium text-gray-700 mb-2">
							Escribe <span className="font-mono text-red-600 bg-red-50 px-2 py-1 rounded">BORRAR</span> para confirmar:
						</label>
						<input
							id="confirm-text"
							type="text"
							value={confirmText}
							onChange={e => setConfirmText(e.target.value)}
							className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-red-500"
							placeholder="Escribe BORRAR aquí"
							autoComplete="off"
						/>
					</div>

					{/* Botón de acción */}
					<Button
						type="button"
						variant="error"
						className="w-full"
						disabled={!canDelete}
						onClick={handleDelete}
					>
						{loading ? 'Enviando solicitud...' : 'Solicitar borrado de cuenta'}
					</Button>

					{/* Mensajes de feedback */}
					{success && (
						<div className="bg-green-50 border border-green-200 rounded-lg p-4" role="alert">
							<div className="flex items-start gap-3">
								<svg className="h-5 w-5 text-green-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
									<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
								</svg>
								<div>
									<p className="text-green-800 font-medium">{success}</p>
									{deleteRequestedAt && (
										<div className="mt-3 bg-yellow-50 border border-yellow-200 rounded p-3">
											<p className="text-yellow-800 font-medium text-sm mb-1">
												Borrado reversible durante 14 días
											</p>
											<p className="text-yellow-700 text-sm">{countdown}</p>
											<p className="text-gray-600 text-xs mt-2">
												Puedes cancelar la solicitud desde el email recibido o contactando soporte.
											</p>
										</div>
									)}
								</div>
							</div>
						</div>
					)}

					{error && (
						<div className="bg-red-50 border border-red-200 rounded-lg p-4" role="alert">
							<div className="flex items-start gap-3">
								<svg className="h-5 w-5 text-red-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
									<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
								</svg>
								<div>
									<p className="text-red-800 font-medium">{error}</p>
									{correlationId && (
										<p className="text-gray-600 text-xs mt-1">
											Correlation ID: {correlationId}
										</p>
									)}
								</div>
							</div>
						</div>
					)}
				</div>
			</div>
		</div>
	);
};

export default DeleteAccount;
