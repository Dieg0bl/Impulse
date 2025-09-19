
import React from 'react';

const PRIVACY_CONTENT = {
	version: '1.2.0',
	effectiveDate: '2024-01-15',
	lastUpdated: '2024-01-10',
	sections: [
		{
			title: '1. Introducción',
			content: 'Esta Política de Privacidad describe cómo Impulse recopila, utiliza y protege tu información personal cuando utilizas nuestra plataforma.'
		},
		{
			title: '2. Información que recopilamos',
			content: 'Recopilamos información que nos proporcionas directamente (nombre, email, datos de perfil), así como datos de uso, cookies y metadatos para mejorar el servicio.'
		},
		{
			title: '3. Uso de la información',
			content: 'Utilizamos tus datos para operar la plataforma, personalizar tu experiencia, enviar notificaciones y cumplir con obligaciones legales.'
		},
		{
			title: '4. Compartición de datos',
			content: 'No vendemos tus datos. Podemos compartir información con proveedores de servicios bajo acuerdos de confidencialidad y solo para fines operativos.'
		},
		{
			title: '5. Seguridad',
			content: 'Implementamos medidas técnicas y organizativas para proteger tus datos contra acceso no autorizado, pérdida o alteración.'
		},
		{
			title: '6. Tus derechos',
			content: 'Puedes acceder, rectificar o eliminar tus datos personales desde tu perfil o contactando a soporte. Tienes derecho a la portabilidad y a oponerte a ciertos tratamientos.'
		},
		{
			title: '7. Retención de datos',
			content: 'Conservamos tus datos solo el tiempo necesario para los fines descritos y según lo exija la ley.'
		},
		{
			title: '8. Cambios en la política',
			content: 'Te notificaremos sobre cambios importantes en esta política. Consulta la versión y fecha de vigencia para estar informado.'
		},
		{
			title: '9. Contacto',
			content: 'Si tienes preguntas sobre esta política, escríbenos a privacidad@impulse.com.'
		}
	]
};

const Privacy: React.FC = () => (
	<div className="min-h-screen bg-gray-50">
		<div className="max-w-4xl mx-auto px-4 py-8">
			<div className="bg-white rounded-lg shadow-sm p-8">
				<h1 className="text-3xl font-bold text-gray-900 mb-4">Política de Privacidad</h1>
				<div className="flex flex-wrap gap-4 text-sm text-gray-600 mb-6">
					<span><strong>Versión:</strong> {PRIVACY_CONTENT.version}</span>
					<span><strong>Fecha de vigencia:</strong> {PRIVACY_CONTENT.effectiveDate}</span>
					<span><strong>Última actualización:</strong> {PRIVACY_CONTENT.lastUpdated}</span>
				</div>
				<div className="prose prose-sm max-w-none">
					{PRIVACY_CONTENT.sections.map((section, idx) => (
						<section className="mb-8" key={section.title}>
							<h2 className="text-lg font-semibold text-gray-900 mb-2">{section.title}</h2>
							<p className="text-gray-700 leading-relaxed">{section.content}</p>
						</section>
					))}
				</div>
				<div className="mt-8 grid grid-cols-1 md:grid-cols-2 gap-4">
					<a
						href="/terms"
						className="bg-white rounded-lg shadow-sm p-4 hover:shadow-md transition-shadow text-center"
					>
						<h3 className="font-medium text-gray-900 mb-1">Términos de Servicio</h3>
						<p className="text-sm text-gray-600">Condiciones de uso de la plataforma</p>
					</a>
					<a
						href="/about"
						className="bg-white rounded-lg shadow-sm p-4 hover:shadow-md transition-shadow text-center"
					>
						<h3 className="font-medium text-gray-900 mb-1">Sobre Nosotros</h3>
						<p className="text-sm text-gray-600">Conoce nuestro equipo</p>
					</a>
				</div>
			</div>
		</div>
	</div>
);

export default Privacy;
