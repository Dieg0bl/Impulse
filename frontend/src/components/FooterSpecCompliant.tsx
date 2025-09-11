import React from 'react';
import { LEGAL_COPY } from '../config/copy';
import { useConfig } from '../services/configService';

const FooterSpecCompliant: React.FC = () => {
  const config = useConfig();

  return (
    <footer className="bg-gray-900 text-white py-12">
      <div className="max-w-7xl mx-auto px-4">
        
        {/* Main Footer Content */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          
          {/* Company Info */}
          <div className="col-span-1 md:col-span-2">
            <h3 className="text-2xl font-bold mb-4">{LEGAL_COPY.company.name}</h3>
            <p className="text-gray-400 mb-4">
              La plataforma de retos personales con validación comunitaria.
              Crece, comparte y alcanza tus metas con el apoyo de una comunidad comprometida.
            </p>
            
            {/* Beta Status */}
            <div className="bg-blue-600 inline-block px-3 py-1 rounded-full text-sm font-semibold">
              ✨ Beta Abierta - {config.betaDaysRemaining} días restantes
            </div>
          </div>
          
          {/* Quick Links */}
          <div>
            <h4 className="text-lg font-semibold mb-4">Enlaces</h4>
            <ul className="space-y-2 text-gray-400">
              <li><a href="/" className="hover:text-white transition-colors">Dashboard</a></li>
              <li><a href="/challenges" className="hover:text-white transition-colors">Retos</a></li>
              <li><a href="/pricing" className="hover:text-white transition-colors">Planes</a></li>
              <li><a href="/account" className="hover:text-white transition-colors">Mi Cuenta</a></li>
            </ul>
          </div>
          
          {/* Legal Links */}
          <div>
            <h4 className="text-lg font-semibold mb-4">Legal</h4>
            <ul className="space-y-2 text-gray-400">
              <li><a href="/privacy" className="hover:text-white transition-colors">Privacidad</a></li>
              <li><a href="/terms" className="hover:text-white transition-colors">Términos</a></li>
              <li><a href="/cookies" className="hover:text-white transition-colors">Cookies</a></li>
              <li><a href="/gdpr" className="hover:text-white transition-colors">RGPD</a></li>
            </ul>
          </div>
        </div>
        
        {/* Divider */}
        <div className="border-t border-gray-800 pt-8">
          
          {/* Legal Information - Section 19 Specification */}
          <div className="text-center mb-6">
            <p className="text-gray-400 mb-2">
              <strong className="text-white">{LEGAL_COPY.company.name}</strong> – nombre comercial de {LEGAL_COPY.company.owner}
            </p>
            <p className="text-gray-400 mb-4">
              {LEGAL_COPY.company.address}
            </p>
            
            {/* Contact Information */}
            <div className="flex flex-col sm:flex-row justify-center items-center space-y-2 sm:space-y-0 sm:space-x-6 text-sm">
              <div>
                <span className="text-gray-500">Soporte:</span>{' '}
                <a 
                  href={`mailto:${LEGAL_COPY.contacts.support}`}
                  className="text-blue-400 hover:text-blue-300 transition-colors"
                >
                  {LEGAL_COPY.contacts.support}
                </a>
              </div>
              
              <div>
                <span className="text-gray-500">RGPD:</span>{' '}
                <a 
                  href={`mailto:${LEGAL_COPY.contacts.legal}`}
                  className="text-blue-400 hover:text-blue-300 transition-colors"
                >
                  {LEGAL_COPY.contacts.legal}
                </a>
              </div>
              
              <div>
                <span className="text-gray-500">Abusos:</span>{' '}
                <a 
                  href={`mailto:${LEGAL_COPY.contacts.abuse}`}
                  className="text-blue-400 hover:text-blue-300 transition-colors"
                >
                  {LEGAL_COPY.contacts.abuse}
                </a>
              </div>
            </div>
          </div>
          
          {/* Configuration Status (for development) */}
          {process.env.NODE_ENV === 'development' && (
            <div className="bg-gray-800 rounded-lg p-4 mb-6">
              <h5 className="text-sm font-semibold mb-2 text-gray-300">Estado de Configuración:</h5>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-2 text-xs">
                <div className={`px-2 py-1 rounded ${config.isBillingEnabled ? 'bg-green-600' : 'bg-red-600'}`}>
                  BILLING: {config.isBillingEnabled ? 'ON' : 'OFF'}
                </div>
                <div className={`px-2 py-1 rounded ${config.isCoachMarketEnabled ? 'bg-green-600' : 'bg-red-600'}`}>
                  COACH: {config.isCoachMarketEnabled ? 'ON' : 'OFF'}
                </div>
                <div className={`px-2 py-1 rounded ${config.areAdsEnabled ? 'bg-green-600' : 'bg-red-600'}`}>
                  ADS: {config.areAdsEnabled ? 'ON' : 'OFF'}
                </div>
                <div className={`px-2 py-1 rounded ${config.isCMPEnabled ? 'bg-green-600' : 'bg-red-600'}`}>
                  CMP: {config.isCMPEnabled ? 'ON' : 'OFF'}
                </div>
              </div>
            </div>
          )}
          
          {/* Copyright and version */}
          <div className="text-center text-sm text-gray-500">
            <p className="mb-2">
              © {new Date().getFullYear()} {LEGAL_COPY.company.name}. Todos los derechos reservados.
            </p>
            <p>
              IMPULSE LEAN v1.0.0 - Sistema de Retos y Validación Comunitaria
            </p>
            <p className="mt-2 text-xs">
              Desarrollado con ❤️ siguiendo principios de privacidad por defecto y RGPD-first
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default FooterSpecCompliant;
