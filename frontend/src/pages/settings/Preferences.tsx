
import React, { useState } from 'react';
import { useApp } from '../../contexts/AppContext';
import { Button } from '../../components/ui/Button';

const LANGUAGES = [
	{ code: 'es', label: 'Español' },
	{ code: 'en', label: 'English' },
];

const Preferences: React.FC = () => {
	const { theme, setTheme } = useApp();
	const [language, setLanguage] = useState<string>(() => localStorage.getItem('language') || 'es');
	const [notifications, setNotifications] = useState<boolean>(() => {
		const stored = localStorage.getItem('notifications');
		return stored === null ? true : stored === 'true';
	});
	const [notificationsLocal, setNotificationsLocal] = useState<boolean>(() => {
		const stored = localStorage.getItem('notificationsLocal');
		return stored === null ? true : stored === 'true';
	});
	const [reduceMotion, setReduceMotion] = useState<boolean>(() => {
		const stored = localStorage.getItem('reduceMotion');
		return stored === null ? false : stored === 'true';
	});
	const [validatorLanguage, setValidatorLanguage] = useState<string>(() => localStorage.getItem('validatorLanguage') || 'es');
	const [saving, setSaving] = useState(false);
	const [success, setSuccess] = useState<string | null>(null);

	const handleReset = () => {
		setLanguage('es');
		setTheme('light');
		setNotifications(true);
		setNotificationsLocal(true);
		setReduceMotion(false);
		setValidatorLanguage('es');
		setSuccess(null);
	};

	const handleThemeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
		setTheme(e.target.value);
	};

	const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
		setLanguage(e.target.value);
	};

	const handleNotificationsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setNotifications(e.target.checked);
	};

	const handleSave = async (e: React.FormEvent) => {
		e.preventDefault();
		setSaving(true);
		setSuccess(null);
		// Persist preferences locally (simulate backend persistence)
		localStorage.setItem('language', language);
		localStorage.setItem('notifications', notifications ? 'true' : 'false');
		localStorage.setItem('notificationsLocal', notificationsLocal ? 'true' : 'false');
		localStorage.setItem('reduceMotion', reduceMotion ? 'true' : 'false');
		localStorage.setItem('validatorLanguage', validatorLanguage);
		setTimeout(() => {
			setSaving(false);
			setSuccess('Preferencias guardadas correctamente');
		}, 600);
	};

	return (
		<div className="container-app">
			<div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
				{/* Preferences */}
				<div className="space-y-6">
					<h1 className="text-2xl font-bold">Preferencias</h1>

					{/* Idioma */}
					<div className="space-y-2">
						<label htmlFor="language-select" className="block text-sm font-medium">Idioma</label>
						<select
							id="language-select"
							value={language}
							onChange={handleLanguageChange}
							className="w-full border rounded px-3 py-2"
							disabled={saving}
						>
							<option value="es">Español</option>
							<option value="en">English</option>
						</select>
					</div>

					{/* Tema */}
					<div className="space-y-2">
						<label htmlFor="theme-select" className="block text-sm font-medium">Tema</label>
						<select
							id="theme-select"
							value={theme}
							onChange={handleThemeChange}
							className="w-full border rounded px-3 py-2"
							disabled={saving}
						>
							<option value="light">Claro</option>
							<option value="dark">Oscuro</option>
							<option value="auto">Automático</option>
						</select>
					</div>

					{/* Notificaciones */}
					<div className="space-y-2">
						<label className="block text-sm font-medium">Notificaciones</label>
						<div className="space-y-2">
							<label className="flex items-center gap-2">
								<input
									id="email-notifications"
									type="checkbox"
									checked={notifications}
									onChange={handleNotificationsChange}
									disabled={saving}
								/> Email
							</label>
							<label className="flex items-center gap-2">
								<input
									id="local-notifications"
									type="checkbox"
									checked={notificationsLocal}
									onChange={e => setNotificationsLocal(e.target.checked)}
									disabled={saving}
								/> Local
							</label>
						</div>
					</div>

					{/* Accesibilidad */}
					<div className="space-y-2">
						<label htmlFor="reduce-motion" className="block text-sm font-medium">Accesibilidad</label>
						<label className="flex items-center gap-2">
							<input
								id="reduce-motion"
								type="checkbox"
								checked={reduceMotion}
								onChange={e => setReduceMotion(e.target.checked)}
								disabled={saving}
							/> Reducir animaciones
						</label>
					</div>

					{/* Idioma de validadores */}
					<div className="space-y-2">
						<label htmlFor="validator-language-select" className="block text-sm font-medium">Idioma de validadores</label>
						<select
							id="validator-language-select"
							value={validatorLanguage}
							onChange={e => setValidatorLanguage(e.target.value)}
							className="w-full border rounded px-3 py-2"
							disabled={saving}
						>
							<option value="es">Español</option>
							<option value="en">English</option>
						</select>
					</div>

					{/* Actions */}
					<div className="flex gap-4">
						<Button type="submit" disabled={saving}>
							{saving ? 'Guardando...' : 'Guardar'}
						</Button>
						<Button variant="outline" onClick={handleReset} disabled={saving}>
							Reset
						</Button>
					</div>

					{/* Messages */}
					{success && (
						<div className="bg-green-50 border border-green-200 rounded p-4" role="alert">
							Preferencias guardadas correctamente.
						</div>
					)}
				</div>

				{/* Vista previa */}
				<div className="space-y-6">
					<h2 className="text-xl font-semibold">Vista previa</h2>
					<div className={`p-4 rounded border ${theme === 'dark' ? 'bg-gray-800 text-white' : 'bg-white'}`}>
						<p>Ejemplo de contenido con el tema seleccionado.</p>
						<p>Idioma: {language === 'es' ? 'Español' : 'English'}</p>
						<p>Notificaciones: {notifications ? 'Email activadas' : 'Email desactivadas'}</p>
					</div>
				</div>
			</div>
		</div>
	);
};

export default Preferences;
