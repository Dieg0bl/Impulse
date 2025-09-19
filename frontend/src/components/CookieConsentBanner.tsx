
import React, { useEffect, useState } from "react";

const COOKIE_KEY = "impulse_cookie_consent";

const CookieConsentBanner: React.FC = () => {
	const [visible, setVisible] = useState(false);
		// Solo visible controla renderizado, no se usa consent

	useEffect(() => {
		const stored = localStorage.getItem(COOKIE_KEY);
			if (stored === "accepted" || stored === "rejected") {
				setVisible(false);
			} else {
				setVisible(true);
			}
	}, []);

		const handleAccept = () => {
			localStorage.setItem(COOKIE_KEY, "accepted");
			setVisible(false);
			// Aquí puedes inicializar cookies de terceros si aplica
		};

		const handleReject = () => {
			localStorage.setItem(COOKIE_KEY, "rejected");
			setVisible(false);
			// Aquí puedes bloquear cookies de terceros si aplica
		};

		if (!visible) return null;

		return (
			<dialog
				open
				className="fixed bottom-0 left-0 w-full z-50 bg-white border-t border-gray-200 shadow-lg p-4 flex flex-col md:flex-row items-center justify-between gap-4"
				aria-label="Consentimiento de cookies"
			>
				<div className="flex-1 text-sm text-gray-700">
					Usamos cookies para mejorar tu experiencia y analizar el uso del sitio. Consulta nuestra{' '}
					<a href="/privacy" className="text-blue-600 underline">Política de Privacidad</a>{' '}y{' '}
					<a href="/cookies" className="text-blue-600 underline">Política de Cookies</a>.
				</div>
				<div className="flex gap-2">
					<button
						onClick={handleAccept}
						className="bg-primary-600 text-white px-4 py-2 rounded font-semibold focus:outline-none focus:ring-2 focus:ring-primary-400"
						aria-label="Aceptar cookies"
					>
						Aceptar
					</button>
					<button
						onClick={handleReject}
						className="bg-gray-200 text-gray-800 px-4 py-2 rounded font-semibold focus:outline-none focus:ring-2 focus:ring-primary-400"
						aria-label="Rechazar cookies"
					>
						Rechazar
					</button>
				</div>
			</dialog>
		);
};

export default CookieConsentBanner;
