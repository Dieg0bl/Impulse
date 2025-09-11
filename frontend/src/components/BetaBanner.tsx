import React from 'react';
import { Badge } from './ui/Badge';
import { useConfig } from '../services/configService';

interface BetaBannerProps {
  daysRemaining?: number;
}

interface AlertConfig {
  variant: 'success' | 'warning' | 'danger';
  bgColor: string;
  borderColor: string;
  textColor: string;
  message: string;
  showButton: boolean;
  buttonColor: string;
}

const BetaBanner: React.FC<BetaBannerProps> = ({ daysRemaining }) => {
  const config = useConfig();
  
  const getAlertConfig = (): AlertConfig => {
    const effectiveDays = daysRemaining ?? config.betaDaysRemaining;
    
    if (effectiveDays <= 1) {
      return {
        variant: 'danger',
        bgColor: 'bg-red-50 dark:bg-red-900',
        borderColor: 'border-red-400 dark:border-red-600',
        textColor: 'text-red-800 dark:text-red-200',
        message: '¡Último día de beta! Elige tu plan o continúa gratis en Basic.',
        showButton: true,
        buttonColor: 'bg-red-600 text-white hover:bg-red-700'
      };
    }
    
    if (effectiveDays <= 7) {
      return {
        variant: 'warning',
        bgColor: 'bg-yellow-50 dark:bg-yellow-900',
        borderColor: 'border-yellow-400 dark:border-yellow-600',
        textColor: 'text-yellow-800 dark:text-yellow-200',
        message: `Beta termina en ${effectiveDays} días. Sin sorpresas: elige plan o Basic gratis.`,
        showButton: true,
        buttonColor: 'bg-yellow-600 text-white hover:bg-yellow-700'
      };
    }
    
    if (effectiveDays <= 15) {
      return {
        variant: 'warning',
        bgColor: 'bg-yellow-50 dark:bg-yellow-900',
        borderColor: 'border-yellow-400 dark:border-yellow-600',
        textColor: 'text-yellow-800 dark:text-yellow-200',
        message: `Beta termina en ${effectiveDays} días. Podrás elegir plan o continuar gratis.`,
        showButton: true,
        buttonColor: 'bg-yellow-600 text-white hover:bg-yellow-700'
      };
    }
    
    return {
      variant: 'success',
      bgColor: 'bg-green-50 dark:bg-green-900',
      borderColor: 'border-green-400 dark:border-green-600',
      textColor: 'text-green-800 dark:text-green-200',
      message: `Beta gratuita: ${effectiveDays} días restantes sin tarjeta ni cobros.`,
      showButton: false,
      buttonColor: ''
    };
  };

  const alertConfig = getAlertConfig();
  const effectiveDays = daysRemaining ?? config.betaDaysRemaining;
  const isUrgent = effectiveDays <= 7;

  return (
    <div className={`border-l-4 p-4 mb-4 rounded-r-lg ${alertConfig.bgColor} ${alertConfig.borderColor}`}>
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <Badge variant={alertConfig.variant}>
            BETA ABIERTA
          </Badge>
          <span className={`text-sm font-medium ${alertConfig.textColor}`}>
            {alertConfig.message}
          </span>
        </div>
        
        {alertConfig.showButton && (
          <button
            onClick={() => window.location.href = '/pricing'}
            className={`px-3 py-1 rounded text-xs font-medium transition-colors ${alertConfig.buttonColor}`}
          >
            Ver Planes
          </button>
        )}
      </div>
      
      {isUrgent && (
        <UrgentInfo textColor={alertConfig.textColor} />
      )}
    </div>
  );
};

const UrgentInfo: React.FC<{ textColor: string }> = ({ textColor }) => (
  <div className={`mt-2 text-xs ${textColor}`}>
    <p>📧 También recibirás un email recordatorio</p>
    <p>✅ Sin renovación automática - tú decides</p>
    <p>🆓 Plan Basic gratuito disponible para siempre</p>
  </div>
);

export default BetaBanner;
