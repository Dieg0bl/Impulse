import React from 'react';
import { useConfig } from '../services/configService';
import { HERO_COPY } from '../config/copy';

const BetaBannerSimple: React.FC = () => {
  const config = useConfig();

  // Don't show banner if billing is enabled (post-beta)
  if (config.isBillingEnabled) {
    return null;
  }

  return (
    <div className="bg-gradient-to-r from-blue-500 to-purple-600 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 py-3">
        <div className="flex items-center justify-between flex-wrap gap-2">
          
          {/* Beta Notice - Section 2 specification compliant */}
          <div className="flex items-center space-x-3">
            <span className="text-xl">✨</span>
            <div>
              <span className="font-semibold">Beta Abierta:</span>
              <span className="ml-2 text-blue-100">
                {HERO_COPY.betaNotice}
              </span>
            </div>
          </div>
          
          {/* Beta Countdown */}
          <div className="flex items-center space-x-4">
            <div className="bg-white/20 rounded-lg px-3 py-1 text-center">
              <div className="font-bold text-lg">{config.betaDaysRemaining}</div>
              <div className="text-xs text-blue-200">días restantes</div>
            </div>
            
            <div className="hidden sm:block text-sm text-blue-200">
              Finaliza: <span className="font-semibold">{config.betaEndDate}</span>
            </div>
          </div>
        </div>
        
        {/* Beta Benefits */}
        <div className="mt-3 pt-3 border-t border-white/20">
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 text-sm">
            <div className="flex items-center space-x-2">
              <span className="text-green-300">✓</span>
              <span>Sin tarjeta requerida</span>
            </div>
            <div className="flex items-center space-x-2">
              <span className="text-green-300">✓</span>
              <span>Sin cobros automáticos</span>
            </div>
            <div className="flex items-center space-x-2">
              <span className="text-green-300">✓</span>
              <span>Acceso completo Pro</span>
            </div>
            <div className="flex items-center space-x-2">
              <span className="text-green-300">✓</span>
              <span>Basic gratis después</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BetaBannerSimple;
