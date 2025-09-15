import React from "react";
import { useConfig } from "../services/configService";

const Footer: React.FC = () => {
  const config = useConfig();

  return (
    <footer className="bg-gray-50 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700 mt-16">
      <div className="container mx-auto px-4 py-8">
        {/* Main Footer Content */}
        <div className="grid md:grid-cols-4 gap-8">
          {/* Company Info */}
          <div>
            <h3 className="font-bold text-gray-900 dark:text-white mb-4">{config.companyName}</h3>
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
              Invierte en ti. Haz que cada meta cuente.
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-500">{config.address}</p>
          </div>

          {/* Legal Links */}
          <div>
            <h4 className="font-medium text-gray-900 dark:text-white mb-4">Legal</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <a
                  href="/terms"
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  Términos de Uso
                </a>
              </li>
              <li>
                <a
                  href="/privacy"
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  Política de Privacidad
                </a>
              </li>
              <li>
                <a
                  href="/cookies"
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  Política de Cookies
                </a>
              </li>
              <li>
                <a
                  href="/dsa"
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  DSA Compliance
                </a>
              </li>
            </ul>
          </div>

          {/* Product */}
          <div>
            <h4 className="font-medium text-gray-900 dark:text-white mb-4">Producto</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <a
                  href="/pricing"
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  Planes y Precios
                </a>
              </li>
              <li>
                <a
                  href="/features"
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  Características
                </a>
              </li>
              <li>
                <a
                  href="/security"
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  Seguridad
                </a>
              </li>
              {config.isCoachMarketEnabled && (
                <li>
                  <a
                    href="/coach-marketplace"
                    className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                  >
                    Marketplace Coach
                  </a>
                </li>
              )}
            </ul>
          </div>

          {/* Contact & Support */}
          <div>
            <h4 className="font-medium text-gray-900 dark:text-white mb-4">Contacto</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <a
                  href={`mailto:${config.supportEmail}`}
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  📧 Soporte
                </a>
              </li>
              <li>
                <a
                  href={`mailto:${config.legalEmail}`}
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  ⚖️ RGPD/Legal
                </a>
              </li>
              <li>
                <a
                  href={`mailto:${config.abuseEmail}`}
                  className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                >
                  🚫 Reportar Abuso
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Beta Notice */}
        {config.betaDaysRemaining > 0 && (
          <div className="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
            <div className="bg-blue-50 dark:bg-blue-900 rounded-lg p-4">
              <p className="text-sm text-blue-800 dark:text-blue-200 text-center">
                <strong>Beta Abierta:</strong> Sin tarjeta, sin cobros, sin renovación automática
                durante {config.betaDaysRemaining} días. Al finalizar, podrás elegir plan o
                continuar en Basic gratis.
              </p>
            </div>
          </div>
        )}

        {/* Copyright & Legal Notice */}
        <div className="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
          <div className="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
            <div className="text-xs text-gray-500 dark:text-gray-400 text-center md:text-left">
              <p>
                <strong>{config.companyName}</strong> – nombre comercial de {config.ownerName}
              </p>
              <p>{config.address}</p>
            </div>

            <div className="text-xs text-gray-500 dark:text-gray-400 text-center md:text-right">
              <p>
                © {new Date().getFullYear()} {config.ownerName}
              </p>
              {config.isBillingEnabled && (
                <p className="mt-1">
                  <a
                    href="/billing-portal"
                    className="hover:text-blue-600 dark:hover:text-blue-400"
                  >
                    Portal de Facturación
                  </a>
                </p>
              )}
            </div>
          </div>
        </div>

        {/* RGPD Compliance Notice */}
        <div className="mt-4 text-center">
          <p className="text-xs text-gray-400 dark:text-gray-500">
            Al continuar usando IMPULSE, aceptas nuestros{" "}
            <a href="/terms" className="text-blue-600 dark:text-blue-400 hover:underline">
              Términos
            </a>
            ,{" "}
            <a href="/privacy" className="text-blue-600 dark:text-blue-400 hover:underline">
              Privacidad
            </a>{" "}
            y{" "}
            <a href="/cookies" className="text-blue-600 dark:text-blue-400 hover:underline">
              Cookies
            </a>
            .
            {config.isBillingEnabled &&
              " Cuando actives un plan de pago, podrás cancelar en 1 clic desde tu Portal."}
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
