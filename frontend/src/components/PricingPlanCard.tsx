import React from 'react';
import { PRICING_PLANS, formatPrice, PricingPlan } from '../config/pricing';
import { useConfig } from '../services/configService';

interface PricingPlanCardProps {
  planId: keyof typeof PRICING_PLANS;
  isPopular?: boolean;
  onSelectPlan: (planId: string) => void;
}

const PricingPlanCard: React.FC<PricingPlanCardProps> = ({ 
  planId, 
  isPopular = false, 
  onSelectPlan 
}) => {
  const config = useConfig();
  const plan: PricingPlan = PRICING_PLANS[planId];

  return (
    <div className={`
      relative bg-white rounded-lg shadow-lg border-2 transition-all duration-300 hover:shadow-xl
      ${isPopular ? 'border-blue-500 scale-105' : 'border-gray-200 hover:border-blue-300'}
    `}>
      
      {/* Popular Badge */}
      {isPopular && (
        <div className="absolute -top-3 left-1/2 transform -translate-x-1/2">
          <span className="bg-blue-500 text-white px-4 py-1 rounded-full text-sm font-semibold">
            Más Popular
          </span>
        </div>
      )}

      <div className="p-6">
        
        {/* Plan Header */}
        <div className="text-center mb-6">
          <h3 className="text-2xl font-bold text-gray-900 mb-2">{plan.name}</h3>
          <p className="text-gray-600 text-sm mb-4">
            {planId === 'basic' && 'Perfecto para empezar con retos personales'}
            {planId === 'pro' && 'Para usuarios que quieren máximo rendimiento'}
            {planId === 'teams' && 'Gestión profesional de equipos y retos grupales'}
            {planId === 'coach' && 'Monetiza tu experiencia como coach certificado'}
          </p>
          
          {/* Pricing */}
          <div className="mb-4">
            {plan.price > 0 ? (
              <div>
                <div className="text-4xl font-bold text-gray-900">
                  {formatPrice(plan.price)}
                  <span className="text-lg text-gray-600">/{plan.period === 'month' ? 'mes' : 'año'}</span>
                </div>
                {plan.yearlyPrice && plan.yearlyPrice > 0 && (
                  <div className="text-lg text-gray-600 mt-1">
                    o {formatPrice(plan.yearlyPrice)}/año
                    <span className="text-green-600 text-sm ml-2">
                      (Ahorra {Math.round((1 - (plan.yearlyPrice / 12) / plan.price) * 100)}%)
                    </span>
                  </div>
                )}
              </div>
            ) : (
              <div className="text-4xl font-bold text-green-600">Gratis</div>
            )}
          </div>
        </div>

        {/* Features List */}
        <div className="mb-6">
          <ul className="space-y-3">
            {plan.features.map((feature, index) => (
              <li key={`${planId}-feature-${index}`} className="flex items-start">
                <svg 
                  className="w-5 h-5 text-green-500 mr-3 mt-0.5 flex-shrink-0" 
                  fill="currentColor" 
                  viewBox="0 0 20 20"
                >
                  <path 
                    fillRule="evenodd" 
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" 
                    clipRule="evenodd" 
                  />
                </svg>
                <span className="text-gray-700 text-sm">{feature.name}</span>
              </li>
            ))}
          </ul>
        </div>

        {/* Plan Limits */}
        <div className="mb-6">
          <h4 className="text-sm font-semibold text-gray-900 mb-2">Características:</h4>
          <ul className="space-y-2">
            <li className="flex justify-between text-sm">
              <span className="text-gray-600">Retos activos:</span>
              <span className="font-medium">{plan.limits.activeChallenges}</span>
            </li>
            <li className="flex justify-between text-sm">
              <span className="text-gray-600">Validadores:</span>
              <span className="font-medium">{plan.limits.validatorsPerChallenge}</span>
            </li>
            <li className="flex justify-between text-sm">
              <span className="text-gray-600">Soporte:</span>
              <span className="font-medium">{plan.limits.support}</span>
            </li>
          </ul>
        </div>

        {/* Special Notes */}
        {planId === 'teams' && plan.teamConfig && (
          <div className="mb-4 p-3 bg-blue-50 rounded-lg">
            <p className="text-sm text-blue-800">
              <strong>{plan.teamConfig.included} miembros incluidos.</strong> Miembros adicionales: {formatPrice(plan.teamConfig.extraMemberPrice)}/mes cada uno.
            </p>
          </div>
        )}

        {planId === 'coach' && !config.isCoachMarketEnabled && (
          <div className="mb-4 p-3 bg-yellow-50 rounded-lg">
            <p className="text-sm text-yellow-800">
              Próximamente disponible. El mercado de coaches estará activo en la siguiente fase.
            </p>
          </div>
        )}

        {/* Action Button */}
        <button
          onClick={() => onSelectPlan(planId)}
          disabled={planId === 'coach' && !config.isCoachMarketEnabled}
          className={`
            w-full py-3 px-4 rounded-lg font-semibold text-sm transition-all duration-200
            ${isPopular 
              ? 'bg-blue-600 text-white hover:bg-blue-700 shadow-lg' 
              : 'bg-gray-100 text-gray-900 hover:bg-gray-200'
            }
            ${(planId === 'coach' && !config.isCoachMarketEnabled) 
              ? 'opacity-50 cursor-not-allowed' 
              : 'hover:shadow-md'
            }
          `}
        >
          {planId === 'basic' && 'Comenzar Gratis'}
          {planId === 'pro' && 'Upgrade a Pro'}
          {planId === 'teams' && 'Iniciar Plan Teams'}
          {planId === 'coach' && (config.isCoachMarketEnabled ? 'Aplicar como Coach' : 'Próximamente')}
        </button>

        {/* Additional Info */}
        <div className="mt-4 text-center">
          <p className="text-xs text-gray-500">
            {planId === 'basic' && 'Sin tarjeta de crédito requerida'}
            {planId === 'pro' && 'Cancela en cualquier momento'}
            {planId === 'teams' && 'Gestión centralizada de equipo'}
            {planId === 'coach' && 'Comisión del 15% en servicios'}
          </p>
        </div>
      </div>
    </div>
  );
};

export default PricingPlanCard;
