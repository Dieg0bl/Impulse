import React from "react";
import PricingPlanCard from "../components/PricingPlanCard";
import { useConfig } from "../services/configService";

const PricingPage: React.FC = () => {
  const config = useConfig();

  const handleSelectPlan = (planId: string) => {
    if (planId === "basic") {
      // Redirect to registration/dashboard
      window.location.href = "/dashboard";
    } else if (planId === "coach" && !config.isCoachMarketEnabled) {
      // Show coming soon message
      alert("El mercado de coaches estará disponible próximamente.");
    } else {
      // Redirect to Stripe checkout or billing page
      window.location.href = `/checkout/${planId}`;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <div className="bg-gradient-to-br from-blue-600 to-indigo-700 text-white py-16">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <h1 className="text-5xl font-bold mb-6">Planes y Precios</h1>
          <p className="text-xl text-blue-100 mb-8 max-w-3xl mx-auto">
            Elige el plan perfecto para tus objetivos. Desde retos personales hasta gestión de
            equipos.
          </p>

          {/* Beta Status */}
          <div className="inline-flex items-center bg-blue-500/20 backdrop-blur-sm border border-blue-300/30 rounded-full px-6 py-3">
            <div className="w-2 h-2 bg-green-400 rounded-full mr-3 animate-pulse"></div>
            <span className="text-sm font-medium">
              Beta Abierta - {config.betaDaysRemaining} días restantes
            </span>
          </div>
        </div>
      </div>

      {/* Pricing Cards */}
      <div className="max-w-7xl mx-auto px-4 py-16">
        {/* Billing Toggle (when enabled) */}
        {config.isBillingEnabled && (
          <div className="text-center mb-12">
            <div className="inline-flex items-center bg-white rounded-lg p-1 shadow-md">
              <button className="px-6 py-2 rounded-md text-sm font-medium bg-gray-100 text-gray-900">
                Mensual
              </button>
              <button className="px-6 py-2 rounded-md text-sm font-medium text-gray-600 hover:text-gray-900">
                Anual (Ahorra 23%)
              </button>
            </div>
          </div>
        )}

        {/* Plans Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Basic Plan */}
          <PricingPlanCard planId="basic" onSelectPlan={handleSelectPlan} />

          {/* Pro Plan */}
          <PricingPlanCard planId="pro" isPopular={true} onSelectPlan={handleSelectPlan} />

          {/* Teams Plan */}
          <PricingPlanCard planId="teams" onSelectPlan={handleSelectPlan} />

          {/* Coach Plan */}
          <PricingPlanCard planId="coach" onSelectPlan={handleSelectPlan} />
        </div>

        {/* Additional Information */}
        <div className="mt-16 text-center">
          <div className="bg-white rounded-lg shadow-lg p-8 max-w-4xl mx-auto">
            <h3 className="text-2xl font-bold text-gray-900 mb-6">
              ¿Tienes preguntas sobre nuestros planes?
            </h3>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              <div className="text-center">
                <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                  <svg
                    className="w-6 h-6 text-blue-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                    />
                  </svg>
                </div>
                <h4 className="font-semibold text-gray-900 mb-2">Seguridad Garantizada</h4>
                <p className="text-gray-600 text-sm">Cumplimiento RGPD y datos encriptados</p>
              </div>

              <div className="text-center">
                <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                  <svg
                    className="w-6 h-6 text-green-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                </div>
                <h4 className="font-semibold text-gray-900 mb-2">Sin Compromiso</h4>
                <p className="text-gray-600 text-sm">Cancela o cambia de plan cuando quieras</p>
              </div>

              <div className="text-center">
                <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                  <svg
                    className="w-6 h-6 text-yellow-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192L5.636 18.364M12 12h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                    />
                  </svg>
                </div>
                <h4 className="font-semibold text-gray-900 mb-2">Soporte Especializado</h4>
                <p className="text-gray-600 text-sm">Equipo dedicado para ayudarte a crecer</p>
              </div>
            </div>

            <div className="flex flex-col sm:flex-row items-center justify-center space-y-4 sm:space-y-0 sm:space-x-6">
              <a
                href="mailto:soporte@impulselean.com"
                className="inline-flex items-center px-6 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
              >
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                  />
                </svg>
                Contactar Soporte
              </a>

              <a
                href="/faq"
                className="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
                Ver FAQ
              </a>
            </div>
          </div>
        </div>

        {/* Enterprise Contact */}
        <div className="mt-16 text-center">
          <div className="bg-gradient-to-r from-gray-900 to-gray-800 rounded-lg p-8 text-white">
            <h3 className="text-2xl font-bold mb-4">¿Necesitas una solución empresarial?</h3>
            <p className="text-gray-300 mb-6 max-w-2xl mx-auto">
              Para organizaciones con más de 50 usuarios, ofrecemos planes personalizados con
              características avanzadas de administración, integraciones y soporte prioritario.
            </p>
            <a
              href="mailto:enterprise@impulselean.com"
              className="inline-flex items-center px-8 py-3 bg-white text-gray-900 rounded-lg font-semibold hover:bg-gray-100 transition-colors"
            >
              Contactar Ventas Enterprise
              <svg className="w-5 h-5 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M17 8l4 4m0 0l-4 4m4-4H3"
                />
              </svg>
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PricingPage;
