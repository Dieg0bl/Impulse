import React, { useState, useEffect, useCallback } from 'react';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorBoundary from '../../components/ErrorBoundary';
import A11yHelper from '../../components/A11yHelper';
import NotificationCard from '../../components/NotificationCard';
import IdleState from '../../components/IdleState';
import { AppButton } from '../../ui/AppButton';
import type { ApiErrorDto } from '../../types/api';

interface TermsVersion {
	version: string;
	effectiveDate: string;
	lastUpdated: string;
	content: {
		introduction: string;
		definitions: string[];
		userObligations: string[];
		serviceDescription: string[];
		privacyPolicy: string[];
		intellectualProperty: string[];
		limitationOfLiability: string[];
		termination: string[];
		governingLaw: string[];
		contact: string;
	};
}

const CURRENT_TERMS: TermsVersion = {
	version: '2.1.0',
	effectiveDate: '2024-01-15',
	lastUpdated: '2024-01-10',
	content: {
		introduction: `Estos Términos de Servicio ("Términos") regulan el uso de la plataforma Impulse, una aplicación web diseñada para ayudar a los usuarios a gestionar sus desafíos personales y profesionales. Al acceder y utilizar nuestros servicios, aceptas estar sujeto a estos Términos.`,

		definitions: [
			'"Servicio" se refiere a la plataforma Impulse y todas sus funcionalidades.',
			'"Usuario" se refiere a cualquier persona que accede o utiliza el Servicio.',
			'"Contenido" se refiere a cualquier información, texto, gráficos, fotos u otros materiales subidos por los usuarios.',
			'"Cuenta" se refiere al perfil personal creado por el usuario en el Servicio.'
		],

		userObligations: [
			'Proporcionar información veraz y actualizada al crear tu cuenta.',
			'Mantener la confidencialidad de tu contraseña y credenciales de acceso.',
			'No utilizar el Servicio para actividades ilegales o prohibidas.',
			'No intentar acceder a áreas restringidas del Servicio sin autorización.',
			'Respetar los derechos de propiedad intelectual de otros usuarios.',
			'No compartir contenido que viole las leyes o regulaciones aplicables.'
		],

		serviceDescription: [
			'Impulse es una plataforma que permite crear y gestionar desafíos personales.',
			'Ofrecemos herramientas para seguimiento de progreso y análisis de datos.',
			'El Servicio está disponible 24/7, sujeto a mantenimiento programado.',
			'Nos reservamos el derecho de modificar o discontinuar funcionalidades.',
			'Todas las actualizaciones se comunican con anticipación a los usuarios.'
		],

		privacyPolicy: [
			'Recopilamos información necesaria para proporcionar el Servicio.',
			'Tus datos personales se protegen según nuestra Política de Privacidad.',
			'Utilizamos encriptación para proteger la información sensible.',
			'Tienes derecho a acceder, rectificar y eliminar tus datos personales.',
			'Cumplimos con el RGPD y otras regulaciones de protección de datos.'
		],

		intellectualProperty: [
			'El Servicio y su contenido original son propiedad de Impulse.',
			'Los derechos de autor y marcas comerciales están protegidos por ley.',
			'No se concede licencia implícita para usar nuestra propiedad intelectual.',
			'El contenido generado por usuarios pertenece al creador respectivo.',
			'Respetamos los derechos de propiedad intelectual de terceros.'
		],

		limitationOfLiability: [
			'El Servicio se proporciona "tal cual" sin garantías expresas o implícitas.',
			'No somos responsables de daños indirectos o consecuentes.',
			'Nuestra responsabilidad máxima se limita al importe pagado en los últimos 12 meses.',
			'No garantizamos la disponibilidad ininterrumpida del Servicio.',
			'Los usuarios son responsables de su propio uso del Servicio.'
		],

		termination: [
			'Puedes cancelar tu cuenta en cualquier momento desde la configuración.',
			'Podemos suspender o terminar tu acceso por violación de estos Términos.',
			'Al terminar, perderás acceso a tu cuenta y contenido.',
			'Algunos derechos y obligaciones sobreviven a la terminación.',
			'Te notificaremos antes de cualquier acción de terminación.'
		],

		governingLaw: [
			'Estos Términos se rigen por las leyes de España.',
			'Cualquier disputa se resolverá en los tribunales competentes.',
			'Intentaremos resolver disputas de manera amistosa primero.',
			'La versión española de estos Términos prevalece en caso de discrepancias.',
			'Cumplimos con las regulaciones europeas aplicables.'
		],

		contact: `Para cualquier pregunta sobre estos Términos, puedes contactarnos en:
		Email: legal@impulse.com
		Dirección: Calle Principal 123, Madrid, España
		Teléfono: +34 900 123 456`
	}
};

const Terms: React.FC = () => {
	const [isLoading, setIsLoading] = useState(true);
	const [error, setError] = useState<ApiErrorDto | null>(null);
	const [isIdle, setIsIdle] = useState(false);
	const [hasAccepted, setHasAccepted] = useState(false);
	const [hasScrolledToBottom, setHasScrolledToBottom] = useState(false);
	const [isSubmitting, setIsSubmitting] = useState(false);
	const [termsData, setTermsData] = useState<TermsVersion | null>(null);

	// Check for idle state
	useEffect(() => {
		const idleThreshold = 30 * 60 * 1000; // 30 minutes
		const lastActivity = localStorage.getItem('lastActivity');
		if (lastActivity) {
			const timeSinceActivity = Date.now() - parseInt(lastActivity);
			setIsIdle(timeSinceActivity > idleThreshold);
		}
	}, []);

	// Load terms data
	useEffect(() => {
		const loadTerms = async () => {
			try {
				setIsLoading(true);
				setError(null);

				// Simulate API call
				await new Promise(resolve => setTimeout(resolve, 1000));

				setTermsData(CURRENT_TERMS);

				// Check if user has already accepted current version
				const acceptedVersion = localStorage.getItem('accepted_terms_version');
				const acceptedDate = localStorage.getItem('accepted_terms_date');

				if (acceptedVersion === CURRENT_TERMS.version && acceptedDate) {
					setHasAccepted(true);
				}

			} catch (err) {
				setError({
					code: 'TERMS_LOAD_ERROR',
					message: 'Error cargando los términos de servicio',
					correlationId: `terms-load-${Date.now()}`
				});
			} finally {
				setIsLoading(false);
			}
		};

		loadTerms();
	}, []);

	const handleScroll = useCallback((e: React.UIEvent<HTMLDivElement>) => {
		const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;
		const isAtBottom = scrollTop + clientHeight >= scrollHeight - 10;
		setHasScrolledToBottom(isAtBottom);
	}, []);

	const handleAcceptTerms = useCallback(async () => {
		if (!hasScrolledToBottom) {
			setError({
				code: 'SCROLL_REQUIRED',
				message: 'Debes leer todos los términos antes de aceptar',
				correlationId: `terms-accept-${Date.now()}`
			});
			return;
		}

		try {
			setIsSubmitting(true);
			setError(null);

			// Simulate API call
			await new Promise(resolve => setTimeout(resolve, 1000));

			// Store acceptance in localStorage
			localStorage.setItem('accepted_terms_version', CURRENT_TERMS.version);
			localStorage.setItem('accepted_terms_date', new Date().toISOString());

			setHasAccepted(true);

		} catch (err) {
			setError({
				code: 'ACCEPT_ERROR',
				message: 'Error aceptando los términos',
				correlationId: `terms-accept-${Date.now()}`
			});
		} finally {
			setIsSubmitting(false);
		}
	}, [hasScrolledToBottom]);

	const handlePrint = useCallback(() => {
		window.print();
	}, []);

	const formatDate = (dateString: string) => {
		return new Date(dateString).toLocaleDateString('es-ES', {
			year: 'numeric',
			month: 'long',
			day: 'numeric'
		});
	};

	// Idle state
	if (isIdle) {
		return <IdleState onResume={() => window.location.reload()} />;
	}

	// Loading state
	if (isLoading || !termsData) {
		return (
			<div className="min-h-screen bg-gray-50">
				<div className="max-w-4xl mx-auto px-4 py-8">
					<div className="animate-pulse space-y-6">
						<div className="h-8 bg-gray-200 rounded w-1/3"></div>
						<div className="bg-white rounded-lg p-6">
							<div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
							<div className="space-y-4">
								{[...Array(5)].map((_, i) => (
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
							Términos de Servicio
						</h1>
						<div className="flex flex-wrap gap-4 text-sm text-gray-600">
							<span><strong>Versión:</strong> {termsData.version}</span>
							<span><strong>Fecha de vigencia:</strong> {formatDate(termsData.effectiveDate)}</span>
							<span><strong>Última actualización:</strong> {formatDate(termsData.lastUpdated)}</span>
						</div>
					</div>

					{/* Terms Content */}
					<div className="bg-white rounded-lg shadow-sm">
						{/* Terms Header */}
						<div className="p-6 border-b border-gray-200">
							<div className="flex justify-between items-center">
								<h2 className="text-xl font-semibold text-gray-900">
									Condiciones de Uso
								</h2>
								<AppButton variant="outline" onClick={handlePrint}>
									Imprimir
								</AppButton>
							</div>
						</div>

						{/* Scrollable Content */}
						<div
							className="max-h-96 overflow-y-auto p-6"
							onScroll={handleScroll}
						>
							<div className="prose prose-sm max-w-none">
								{/* Introduction */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										1. Introducción
									</h3>
									<p className="text-gray-700 leading-relaxed">
										{termsData.content.introduction}
									</p>
								</section>

								{/* Definitions */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										2. Definiciones
									</h3>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.definitions.map((definition, index) => (
											<li key={index}>{definition}</li>
										))}
									</ul>
								</section>

								{/* User Obligations */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										3. Obligaciones del Usuario
									</h3>
									<p className="text-gray-700 mb-3">
										Al utilizar nuestros servicios, te comprometes a:
									</p>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.userObligations.map((obligation, index) => (
											<li key={index}>{obligation}</li>
										))}
									</ul>
								</section>

								{/* Service Description */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										4. Descripción del Servicio
									</h3>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.serviceDescription.map((item, index) => (
											<li key={index}>{item}</li>
										))}
									</ul>
								</section>

								{/* Privacy Policy */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										5. Política de Privacidad
									</h3>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.privacyPolicy.map((item, index) => (
											<li key={index}>{item}</li>
										))}
									</ul>
								</section>

								{/* Intellectual Property */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										6. Propiedad Intelectual
									</h3>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.intellectualProperty.map((item, index) => (
											<li key={index}>{item}</li>
										))}
									</ul>
								</section>

								{/* Limitation of Liability */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										7. Limitación de Responsabilidad
									</h3>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.limitationOfLiability.map((item, index) => (
											<li key={index}>{item}</li>
										))}
									</ul>
								</section>

								{/* Termination */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										8. Terminación
									</h3>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.termination.map((item, index) => (
											<li key={index}>{item}</li>
										))}
									</ul>
								</section>

								{/* Governing Law */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										9. Ley Aplicable
									</h3>
									<ul className="list-disc list-inside text-gray-700 space-y-2">
										{termsData.content.governingLaw.map((item, index) => (
											<li key={index}>{item}</li>
										))}
									</ul>
								</section>

								{/* Contact */}
								<section className="mb-8">
									<h3 className="text-lg font-semibold text-gray-900 mb-3">
										10. Contacto
									</h3>
									<div className="bg-gray-50 p-4 rounded-lg">
										<pre className="text-gray-700 whitespace-pre-wrap font-sans">
											{termsData.content.contact}
										</pre>
									</div>
								</section>
							</div>
						</div>

						{/* Acceptance Section */}
						{!hasAccepted && (
							<div className="border-t border-gray-200 p-6">
								<div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
									<div className="flex items-start">
										<svg className="w-5 h-5 text-yellow-400 mt-0.5 mr-3" fill="currentColor" viewBox="0 0 20 20">
											<path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
										</svg>
										<div>
											<h3 className="text-yellow-800 font-medium text-sm">
												Importante
											</h3>
											<p className="text-yellow-700 text-sm mt-1">
												{hasScrolledToBottom ?
													'Haz leído todos los términos. Ahora puedes aceptarlos.' :
													'Debes leer todos los términos antes de poder aceptarlos.'
												}
											</p>
										</div>
									</div>
								</div>

								<div className="flex justify-center">
									<AppButton
										onClick={handleAcceptTerms}
										disabled={!hasScrolledToBottom || isSubmitting}
										className="w-full md:w-auto"
									>
										{isSubmitting ? 'Aceptando...' : 'Aceptar Términos de Servicio'}
									</AppButton>
								</div>
							</div>
						)}

						{/* Accepted Confirmation */}
						{hasAccepted && (
							<div className="border-t border-gray-200 p-6">
								<div className="text-center">
									<div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
										<svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
										</svg>
									</div>
									<h3 className="text-lg font-medium text-gray-900 mb-1">
										Términos Aceptados
									</h3>
									<p className="text-sm text-gray-600">
										Has aceptado la versión {termsData.version} de los términos de servicio el{' '}
										{formatDate(localStorage.getItem('accepted_terms_date') || '')}
									</p>
								</div>
							</div>
						)}
					</div>

					{/* Navigation Links */}
					<div className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-4">
						<a
							href="/privacy"
							className="bg-white rounded-lg shadow-sm p-4 hover:shadow-md transition-shadow text-center"
						>
							<h3 className="font-medium text-gray-900 mb-1">Política de Privacidad</h3>
							<p className="text-sm text-gray-600">Cómo manejamos tus datos</p>
						</a>
						<a
							href="/about"
							className="bg-white rounded-lg shadow-sm p-4 hover:shadow-md transition-shadow text-center"
						>
							<h3 className="font-medium text-gray-900 mb-1">Sobre Nosotros</h3>
							<p className="text-sm text-gray-600">Conoce nuestro equipo</p>
						</a>
						<a
							href="/contact"
							className="bg-white rounded-lg shadow-sm p-4 hover:shadow-md transition-shadow text-center"
						>
							<h3 className="font-medium text-gray-900 mb-1">Contacto</h3>
							<p className="text-sm text-gray-600">¿Necesitas ayuda?</p>
						</a>
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

export default Terms;
