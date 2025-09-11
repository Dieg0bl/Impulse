// IMPULSE LEAN - Pricing Configuration (Specification Compliant)
// Implements exact pricing from specification

export interface PlanFeature {
  name: string;
  included: boolean;
  limit?: number;
  description?: string;
}

export interface PricingPlan {
  id: string;
  name: string;
  price: number;
  yearlyPrice?: number;
  currency: string;
  period: 'month' | 'year';
  popular?: boolean;
  features: PlanFeature[];
  limits: {
    activeChallenges: number | 'unlimited';
    validatorsPerChallenge: number | 'unlimited';
    mediaSupport: string;
    privacy: string;
    history: string;
    export: boolean;
    support: string;
  };
  teamConfig?: {
    included: number;
    extraMemberPrice: number;
  };
}

// Specification-compliant pricing plans
export const PRICING_PLANS: Record<string, PricingPlan> = {
  basic: {
    id: 'basic',
    name: 'Basic',
    price: 0,
    currency: 'EUR',
    period: 'month',
    features: [
      { name: 'Hasta 2 retos activos', included: true, limit: 2 },
      { name: '3 validadores por reto', included: true, limit: 3 },
      { name: 'Texto + 1 imagen', included: true },
      { name: 'Privacidad estándar', included: true },
      { name: 'Historial básico', included: true }
    ],
    limits: {
      activeChallenges: 2,
      validatorsPerChallenge: 3,
      mediaSupport: 'text + 1 image',
      privacy: 'standard',
      history: 'basic',
      export: false,
      support: 'community'
    }
  },
  
  pro: {
    id: 'pro',
    name: 'Pro',
    price: 12.99,
    yearlyPrice: 99,
    currency: 'EUR',
    period: 'month',
    popular: true,
    features: [
      { name: 'Retos ilimitados', included: true },
      { name: 'Validadores ilimitados', included: true },
      { name: 'Multimedia completa', included: true },
      { name: 'Retos privados/equipo', included: true },
      { name: 'Estadísticas avanzadas', included: true },
      { name: 'Exportación de datos', included: true },
      { name: 'Soporte prioritario', included: true }
    ],
    limits: {
      activeChallenges: 'unlimited',
      validatorsPerChallenge: 'unlimited',
      mediaSupport: 'full multimedia',
      privacy: 'advanced + private/team challenges',
      history: 'complete + analytics',
      export: true,
      support: 'priority'
    }
  },
  
  teams: {
    id: 'teams',
    name: 'Teams',
    price: 39.99,
    currency: 'EUR',
    period: 'month',
    features: [
      { name: 'Incluye 10 miembros', included: true },
      { name: 'Dashboard de equipo', included: true },
      { name: 'Retos colaborativos', included: true },
      { name: 'Competiciones internas', included: true },
      { name: 'Reportes de equipo', included: true },
      { name: 'Roles y onboarding', included: true },
      { name: 'Todas las características Pro', included: true }
    ],
    limits: {
      activeChallenges: 'unlimited',
      validatorsPerChallenge: 'unlimited',
      mediaSupport: 'full multimedia',
      privacy: 'enterprise + team management',
      history: 'complete + team analytics',
      export: true,
      support: 'dedicated'
    },
    teamConfig: {
      included: 10,
      extraMemberPrice: 4
    }
  },
  
  coach: {
    id: 'coach',
    name: 'Coach',
    price: 0, // Personalizado
    currency: 'EUR',
    period: 'month',
    features: [
      { name: 'Marketplace personal', included: true },
      { name: 'Sesiones 1-a-1', included: true },
      { name: 'Marca blanca', included: true },
      { name: 'Analíticas avanzadas', included: true },
      { name: 'Configuración personalizada', included: true }
    ],
    limits: {
      activeChallenges: 'unlimited',
      validatorsPerChallenge: 'unlimited',
      mediaSupport: 'full multimedia + custom',
      privacy: 'white-label + custom branding',
      history: 'complete + custom analytics',
      export: true,
      support: 'dedicated + technical'
    }
  }
};

// Copy text from specification
export const PRICING_COPY = {
  hero: "Invierte en ti. Haz que cada meta cuente.",
  
  betaNote: "IMPULSE está en beta abierta 90 días: sin tarjeta, sin cobros, sin renovación. Al finalizar, podrás elegir plan o seguir en Basic gratis.",
  
  guarantee: "Garantía 30 días en primera compra (no renovaciones). Excluye uso intensivo manifiesto.",
  
  footer: "Al continuar aceptas Términos, Privacidad y Cookies. Cuando actives un plan de pago, podrás cancelar en 1 clic desde tu Portal.",
  
  emails: {
    welcome: "Tienes acceso gratis 90 días sin tarjeta. El {fecha_fin} podrás elegir plan o seguir en Basic gratis.",
    reminder15: "En 15 días finaliza tu beta. Si quieres Pro, lo activas tú (sin sorpresas). Si no, pasas a Basic gratis.",
    reminder7: "En 7 días finaliza tu beta. Si quieres Pro, lo activas tú (sin sorpresas). Si no, pasas a Basic gratis.",
    reminder1: "Mañana finaliza tu beta. Si quieres Pro, lo activas tú (sin sorpresas). Si no, pasas a Basic gratis."
  }
};

// Currency formatting
export const formatPrice = (price: number, currency: string = 'EUR'): string => {
  if (price === 0) return 'Gratis';
  
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 2
  }).format(price);
};

// Get plan by ID with type safety
export const getPlan = (planId: string): PricingPlan | undefined => {
  return PRICING_PLANS[planId];
};

// Get all plans
export const getAllPlans = (): PricingPlan[] => {
  return Object.values(PRICING_PLANS);
};

// Check if plan is available (Coach is disabled until COACH_MARKET_ON = true)
export const isPlanAvailable = (planId: string, coachMarketEnabled: boolean = false): boolean => {
  if (planId === 'coach') {
    return coachMarketEnabled;
  }
  return true;
};

export default PRICING_PLANS;
