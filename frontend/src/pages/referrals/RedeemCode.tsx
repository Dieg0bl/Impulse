import React, { useState, useEffect } from 'react';
import { Button } from '../../components/ui/Button';

const RedeemCode: React.FC = () => {
	const [code, setCode] = useState('');
	const [loading, setLoading] = useState(false);
	const [success, setSuccess] = useState<string | null>(null);
	const [error, setError] = useState<string | null>(null);
	const [correlationId, setCorrelationId] = useState<string | null>(null);
	const [lastRedeemed, setLastRedeemed] = useState<string | null>(null);
	const [cooldownRemaining, setCooldownRemaining] = useState<number>(0);

	// Simular cooldown
	useEffect(() => {
		const stored = localStorage.getItem('lastRedeemTime');
		if (stored) {
			const lastTime = parseInt(stored);
			const now = Date.now();
			const cooldownMs = 24 * 60 * 60 * 1000; // 24 horas
			const remaining = Math.max(0, cooldownMs - (now - lastTime));
			setCooldownRemaining(Math.ceil(remaining / 1000)); // segundos
		}
	}, []);

	// Countdown del cooldown
	useEffect(() => {
		if (cooldownRemaining > 0) {
			const interval = setInterval(() => {
				setCooldownRemaining(prev => Math.max(0, prev - 1));
			}, 1000);
			return () => clearInterval(interval);
		}
	}, [cooldownRemaining]);

	const formatCooldown = (seconds: number) => {
		const hours = Math.floor(seconds / 3600);
		const minutes = Math.floor((seconds % 3600) / 60);
		const secs = seconds % 60;
		return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
	};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();

		if (!code.trim()) {
			setError('Por favor ingresa un código de referido');
			return;
		}

		if (cooldownRemaining > 0) {
			setError(`Debes esperar ${formatCooldown(cooldownRemaining)} antes de canjear otro código`);
			return;
		}

		setLoading(true);
		setError(null);
		setSuccess(null);
		setCorrelationId(null);

		try {
			// Simular API call con diferentes escenarios
			await new Promise(resolve => setTimeout(resolve, 1500));

			const upperCode = code.trim().toUpperCase();

			if (upperCode === 'IMPULSE2025') {
				throw new Error('No puedes canjear tu propio código de referido');
			}

			if (upperCode === 'INVALID') {
				throw new Error('Código de referido inválido o expirado');
			}

			if (upperCode === 'USED') {
				throw new Error('Este código ya ha sido utilizado');
			}

			if (upperCode === 'LIMIT') {
				throw new Error('Has alcanzado el límite máximo de canjes este mes');
			}

			// Éxito
			setSuccess('¡Código canjeado exitosamente! Has recibido €20 en recompensas.');
			setLastRedeemed(new Date().toISOString());
			localStorage.setItem('lastRedeemTime', Date.now().toString());
			setCode('');
			setCooldownRemaining(24 * 60 * 60); // 24 horas

		} catch (err: any) {
			setError(err.message || 'Error canjeando código');
			if (err.correlationId) setCorrelationId(err.correlationId);
		} finally {
			setLoading(false);
		}
	};

	const isDisabled = loading || cooldownRemaining > 0 || !code.trim();

	return (
		<div className="container-app">
			<div className="max-w-md mx-auto space-y-8">
				{/* Header */}
				<div className="text-center">
					<h1 className="text-3xl font-bold text-gray-900 mb-2">Canjear Código</h1>
					<p className="text-gray-600">
						Ingresa un código de referido para obtener recompensas
					</p>
				</div>

				{/* Formulario */}
				<div className="bg-white border border-gray-200 rounded-lg p-6">
					<form onSubmit={handleSubmit} className="space-y-6">
						<div>
							<label htmlFor="referral-code" className="block text-sm font-medium text-gray-700 mb-2">
								Código de referido
							</label>
							<input
								id="referral-code"
								type="text"
								value={code}
								onChange={(e) => setCode(e.target.value.toUpperCase())}
								className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 uppercase"
								placeholder="Ingresa el código aquí"
								autoComplete="off"
								autoCapitalize="characters"
								disabled={loading}
								aria-describedby={error ? "error-message" : undefined}
							/>
							<p className="text-xs text-gray-500 mt-1">
								El código se convertirá automáticamente a mayúsculas
							</p>
						</div>

						{/* Información del cooldown */}
						{cooldownRemaining > 0 && (
							<div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
								<div className="flex items-center gap-2">
									<svg className="h-4 w-4 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
										<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
									</svg>
									<p className="text-sm text-yellow-800">
										Próximo canje disponible en: <span className="font-mono font-medium">{formatCooldown(cooldownRemaining)}</span>
									</p>
								</div>
							</div>
						)}

						<Button
							type="submit"
							disabled={isDisabled}
							className="w-full"
							aria-busy={loading}
						>
							{loading ? 'Canjeando...' : 'Canjear Código'}
						</Button>
					</form>
				</div>

				{/* Mensajes de éxito */}
				{success && (
					<div className="bg-green-50 border border-green-200 rounded-lg p-4" role="alert">
						<div className="flex items-start gap-3">
							<svg className="h-5 w-5 text-green-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
								<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
							</svg>
							<div>
								<p className="text-green-800 font-medium">{success}</p>
								{lastRedeemed && (
									<p className="text-green-700 text-sm mt-1">
										Canjeado el {new Date(lastRedeemed).toLocaleDateString('es-ES')}
									</p>
								)}
							</div>
						</div>
					</div>
				)}

				{/* Mensajes de error */}
				{error && (
					<div className="bg-red-50 border border-red-200 rounded-lg p-4" role="alert">
						<div className="flex items-start gap-3">
							<svg className="h-5 w-5 text-red-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
								<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
							</svg>
							<div>
								<p className="text-red-800 font-medium" id="error-message">{error}</p>
								{correlationId && (
									<p className="text-red-700 text-sm mt-1">
										ID de correlación: {correlationId}
									</p>
								)}
							</div>
						</div>
					</div>
				)}

				{/* Información adicional */}
				<div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
					<div className="flex items-start gap-3">
						<svg className="h-5 w-5 text-blue-600 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
							<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
						</svg>
						<div className="text-blue-800">
							<h3 className="font-medium mb-1">¿Cómo funciona?</h3>
							<ul className="text-sm space-y-1">
								<li>• Ingresa el código que te dieron</li>
								<li>• Solo puedes canjear un código cada 24 horas</li>
								<li>• Máximo 5 canjes por mes</li>
								<li>• Las recompensas se acreditan automáticamente</li>
							</ul>
						</div>
					</div>
				</div>

				{/* Ejemplos de códigos para testing */}
				<div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
					<h3 className="text-sm font-medium text-gray-900 mb-2">Códigos de prueba:</h3>
					<div className="text-xs text-gray-600 space-y-1">
						<p><code className="bg-white px-1 py-0.5 rounded">IMPULSE2025</code> - Tu propio código (error)</p>
						<p><code className="bg-white px-1 py-0.5 rounded">INVALID</code> - Código inválido</p>
						<p><code className="bg-white px-1 py-0.5 rounded">USED</code> - Código ya usado</p>
						<p><code className="bg-white px-1 py-0.5 rounded">LIMIT</code> - Límite alcanzado</p>
						<p><code className="bg-white px-1 py-0.5 rounded">VALIDCODE</code> - Código válido</p>
					</div>
				</div>
			</div>
		</div>
	);
};

export default RedeemCode;
