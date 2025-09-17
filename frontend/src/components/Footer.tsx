import React from "react";
import { useConfig } from "../services/configService";

const Footer: React.FC = () => {
  const config = useConfig();
  const year = new Date().getFullYear();

  return (
    <footer className="bg-gray-50 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700 mt-auto" role="contentinfo">
      <div className="container-app py-10 md:py-12">
        {/* Desktop Grid */}
        <div className="hidden md:grid md:grid-cols-4 gap-10">
          <div>
            <h3 className="font-bold text-gray-900 dark:text-white mb-3">{config.companyName}</h3>
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2 measure-narrow">
              Invierte en ti. Haz que cada meta cuente.
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-500">{config.address}</p>
          </div>

            <FooterColumn title="Legal" links={[
              { href: '/terms', label: 'Términos de Uso' },
              { href: '/privacy', label: 'Política de Privacidad' },
              { href: '/cookies', label: 'Política de Cookies' },
              { href: '/dsa', label: 'DSA Compliance' }
            ]} />

            <FooterColumn title="Producto" links={[
              { href: '/pricing', label: 'Planes y Precios' },
              { href: '/features', label: 'Características' },
              { href: '/security', label: 'Seguridad' },
              ...(config.isCoachMarketEnabled ? [{ href: '/coach-marketplace', label: 'Marketplace Coach' }] : [])
            ]} />

            <FooterColumn title="Contacto" links={[
              { href: `mailto:${config.supportEmail}`, label: '📧 Soporte' },
              { href: `mailto:${config.legalEmail}`, label: '⚖️ RGPD/Legal' },
              { href: `mailto:${config.abuseEmail}`, label: '🚫 Reportar Abuso' }
            ]} />
        </div>

        {/* Mobile Accordions */}
        <div className="md:hidden space-y-4">
          <FooterAccordion title={config.companyName}>
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
              Invierte en ti. Haz que cada meta cuente.
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-500">{config.address}</p>
          </FooterAccordion>
          <FooterAccordion title="Legal">
            <FooterLinkList links={[
              { href: '/terms', label: 'Términos de Uso' },
              { href: '/privacy', label: 'Política de Privacidad' },
              { href: '/cookies', label: 'Política de Cookies' },
              { href: '/dsa', label: 'DSA Compliance' }
            ]} />
          </FooterAccordion>
          <FooterAccordion title="Producto">
            <FooterLinkList links={[
              { href: '/pricing', label: 'Planes y Precios' },
              { href: '/features', label: 'Características' },
              { href: '/security', label: 'Seguridad' },
              ...(config.isCoachMarketEnabled ? [{ href: '/coach-marketplace', label: 'Marketplace Coach' }] : [])
            ]} />
          </FooterAccordion>
          <FooterAccordion title="Contacto">
            <FooterLinkList links={[
              { href: `mailto:${config.supportEmail}`, label: '📧 Soporte' },
              { href: `mailto:${config.legalEmail}`, label: '⚖️ RGPD/Legal' },
              { href: `mailto:${config.abuseEmail}`, label: '🚫 Reportar Abuso' }
            ]} />
          </FooterAccordion>
        </div>

        {/* Beta Notice (singular) */}
        {config.betaDaysRemaining > 0 && (
          <div className="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
            <p className="text-xs text-center text-gray-600 dark:text-gray-400">
              <strong>Beta Abierta:</strong> {config.betaDaysRemaining} días restantes sin tarjeta ni cobros. Luego podrás elegir plan o continuar gratis en Basic.
            </p>
          </div>
        )}

        {/* Bottom Meta */}
        <div className="mt-6 pt-6 border-t border-gray-200 dark:border-gray-700 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="text-xs text-gray-500 dark:text-gray-400">
            <p><strong>{config.companyName}</strong> – nombre comercial de {config.ownerName}</p>
            <p>{config.address}</p>
          </div>
          <div className="text-xs text-gray-500 dark:text-gray-400 md:text-right">
            <p>© {year} {config.ownerName}</p>
            {config.isBillingEnabled && (
              <p className="mt-1"><a href="/billing-portal" className="hover:text-blue-600 dark:hover:text-blue-400">Portal de Facturación</a></p>
            )}
          </div>
        </div>

        <div className="mt-4 text-center">
          <p className="text-xs text-gray-400 dark:text-gray-500">
            Al continuar usando IMPULSE, aceptas nuestros <a href="/terms" className="text-blue-600 dark:text-blue-400 hover:underline">Términos</a>, <a href="/privacy" className="text-blue-600 dark:text-blue-400 hover:underline">Privacidad</a> y <a href="/cookies" className="text-blue-600 dark:text-blue-400 hover:underline">Cookies</a>.
            {config.isBillingEnabled && ' Podrás cancelar en 1 clic desde tu Portal.'}
          </p>
        </div>
      </div>
    </footer>
  );
};

interface FooterColumnProps { title: string; links: { href: string; label: string }[]; }
const FooterColumn: React.FC<FooterColumnProps> = ({ title, links }) => (
  <div>
    <h4 className="font-medium text-gray-900 dark:text-white mb-3">{title}</h4>
    <FooterLinkList links={links} />
  </div>
);

const FooterLinkList: React.FC<{ links: { href: string; label: string }[] }> = ({ links }) => (
  <ul className="space-y-2 text-sm">
    {links.map(l => (
      <li key={l.href}>
        <a href={l.href} className="text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 focus-outline">
          {l.label}
        </a>
      </li>
    ))}
  </ul>
);

const FooterAccordion: React.FC<{ title: string; children: React.ReactNode }> = ({ title, children }) => (
  <details className="group border border-gray-200 dark:border-gray-700 rounded-lg px-4 py-3 bg-white dark:bg-gray-800">
    <summary className="flex justify-between items-center cursor-pointer list-none focus-outline">
      <span className="font-medium text-gray-900 dark:text-white">{title}</span>
      <span aria-hidden className="transition-transform group-open:rotate-180 text-gray-500">▼</span>
    </summary>
    <div className="mt-3 text-sm">{children}</div>
  </details>
);

export default Footer;
