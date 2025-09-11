import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Badge } from '../components/ui/Badge';
import { Check, Zap, Users, Crown } from 'lucide-react';
import { useConfig } from '../services/configService';

interface PlanFeature {
  text: string;
  included: boolean;
}

interface Plan {
  id: string;
  name: string;
  price: string;
  period: string;
  description: string;
  icon: React.ReactNode;
  features: PlanFeature[];
  popular?: boolean;
  disabled?: boolean;
  buttonText?: string;
}

const plans: Plan[] = [
  {
    id: 'basic',
    name: 'Basic',
    price: 'Gratis',
    period: 'siempre',
    description: 'Perfecto para empezar tu viaje de crecimiento personal',
    icon: <Zap className="h-6 w-6" />,
    features: [
      { text: 'Hasta 2 retos activos', included: true },
      { text: '3 validadores por reto', included: true },
      { text: 'Texto + 1 imagen por evidencia', included: true },
      { text: 'Privacidad estándar', included: true },
      { text: 'Historial básico', included: true },
      { text: 'Retos ilimitados', included: false },
      { text: 'Multimedia completa', included: false },
      { text: 'Retos privados/equipo', included: false },
    ],
    buttonText: 'Plan Actual'
  },
  {
    id: 'pro',
    name: 'Pro',
    price: '12,99 €/mes',
    period: '99 €/año',
    description: 'Para usuarios serios que quieren maximizar su potencial',
    icon: <Crown className="h-6 w-6" />,
    features: [
      { text: 'Retos activos ilimitados', included: true },
      { text: 'Validadores ilimitados', included: true },
      { text: 'Multimedia completa (vídeo, audio)', included: true },
      { text: 'Retos privados y de equipo', included: true },
      { text: 'Estadísticas avanzadas', included: true },
      { text: 'Exportación de datos', included: true },
      { text: 'Soporte prioritario', included: true },
      { text: 'Sin anuncios (cuando estén disponibles)', included: true },
    ],
    popular: true,
    disabled: true,
    buttonText: 'Próximamente'
  },
  {
    id: 'teams',
    name: 'Teams',
    price: '39,99 €/mes',
    period: 'incluye 10 miembros (+4€/extra)',
    description: 'Ideal para equipos y organizaciones que crecen juntos',
    icon: <Users className="h-6 w-6" />,
    features: [
      { text: 'Todo lo de Pro para cada miembro', included: true },
      { text: 'Dashboard de equipo', included: true },
      { text: 'Retos colaborativos', included: true },
      { text: 'Competiciones internas', included: true },
      { text: 'Reportes de progreso grupal', included: true },
      { text: 'Roles y permisos', included: true },
      { text: 'Onboarding personalizado', included: true },
      { text: 'Gestión centralizada', included: true },
    ],
    disabled: true,
    buttonText: 'Próximamente'
  }
];

const PricingPage: React.FC = () => {
  const config = useConfig();

  const handlePlanSelect = (planId: string) => {
    if (planId === 'basic') {
      // Ya está en el plan básico
      return;
    }
    
    if (!config.shouldShowPaymentButton(planId)) {
      alert(config.getBillingDisabledMessage());
      return;
    }
    
    // Aquí iría la lógica de Stripe cuando BILLING_ON = true
    alert('Redirigiendo a Stripe Checkout...');
  };

  const getButtonClassName = (plan: Plan) => {
    if (plan.popular && config.shouldShowPaymentButton(plan.id)) {
      return 'bg-blue-600 hover:bg-blue-700 text-white';
    }
    if (plan.id === 'basic') {
      return 'bg-gray-200 text-gray-700 hover:bg-gray-300';
    }
    return 'bg-gray-100 text-gray-500 cursor-not-allowed';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800">
      <div className="container mx-auto px-4 py-16">
        {/* Header */}
        <div className="text-center mb-16">
          <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
            Planes y Precios
          </h1>
          <p className="text-xl text-gray-600 dark:text-gray-300 mb-8">
            Elige el plan que mejor se adapte a tus objetivos
          </p>
          
          {/* Beta Notice */}
          <div className="bg-green-100 dark:bg-green-900 border border-green-200 dark:border-green-700 rounded-lg p-4 max-w-3xl mx-auto">
            <div className="flex items-center justify-center space-x-2">
              <Badge variant="secondary" className="bg-green-200 text-green-800 dark:bg-green-800 dark:text-green-200">
                BETA ABIERTA
              </Badge>
              <span className="text-green-800 dark:text-green-200 font-medium">
                90 días gratis sin tarjeta, sin cobros, sin renovación automática
              </span>
            </div>
            <p className="text-sm text-green-700 dark:text-green-300 mt-2">
              Al finalizar la beta, podrás elegir tu plan o continuar en Basic gratis para siempre
            </p>
          </div>
        </div>

        {/* Plans Grid */}
        <div className="grid md:grid-cols-3 gap-8 max-w-7xl mx-auto">
          {plans.map((plan) => {
            const dynamicPlan = {
              ...plan,
              buttonText: config.getCheckoutButtonText(plan.id),
              disabled: !config.shouldShowPaymentButton(plan.id) && plan.id !== 'basic'
            };
            
            return (
            <Card 
              key={dynamicPlan.id} 
              className={`relative ${dynamicPlan.popular ? 'ring-2 ring-blue-500 shadow-xl scale-105' : 'shadow-lg'} transition-all duration-300 hover:shadow-xl`}
            >
              {dynamicPlan.popular && (
                <div className="absolute -top-3 left-1/2 transform -translate-x-1/2">
                  <Badge className="bg-blue-500 text-white px-3 py-1">
                    Más Popular
                  </Badge>
                </div>
              )}
              
              <CardHeader className="text-center pb-4">
                <div className="flex justify-center mb-4">
                  <div className={`p-3 rounded-full ${dynamicPlan.popular ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-600'}`}>
                    {dynamicPlan.icon}
                  </div>
                </div>
                <CardTitle className="text-2xl font-bold">{dynamicPlan.name}</CardTitle>
                <div className="mt-4">
                  <div className="text-3xl font-bold text-gray-900 dark:text-white">
                    {dynamicPlan.price}
                  </div>
                  <div className="text-sm text-gray-500 dark:text-gray-400">
                    {dynamicPlan.period}
                  </div>
                </div>
                <CardDescription className="mt-4 text-base">
                  {dynamicPlan.description}
                </CardDescription>
              </CardHeader>

              <CardContent>
                <ul className="space-y-3 mb-8">
                  {dynamicPlan.features.map((feature, index) => (
                    <li key={`${dynamicPlan.id}-feature-${index}`} className="flex items-start space-x-3">
                      <Check 
                        className={`h-5 w-5 mt-0.5 flex-shrink-0 ${
                          feature.included 
                            ? 'text-green-500' 
                            : 'text-gray-300 dark:text-gray-600'
                        }`} 
                      />
                      <span className={`text-sm ${
                        feature.included 
                          ? 'text-gray-700 dark:text-gray-300' 
                          : 'text-gray-400 dark:text-gray-500 line-through'
                      }`}>
                        {feature.text}
                      </span>
                    </li>
                  ))}
                </ul>

                <Button
                  onClick={() => handlePlanSelect(dynamicPlan.id)}
                  disabled={dynamicPlan.disabled}
                  className={`w-full ${getButtonClassName(dynamicPlan)}`}
                  size="lg"
                >
                  {dynamicPlan.buttonText || 'Seleccionar Plan'}
                </Button>
              </CardContent>
            </Card>
          );
        })}
        </div>

        {/* FAQ Section */}
        <div className="mt-20 max-w-4xl mx-auto">
          <h2 className="text-2xl font-bold text-center mb-8 text-gray-900 dark:text-white">
            Preguntas Frecuentes
          </h2>
          
          <div className="space-y-6">
            <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-md">
              <h3 className="font-semibold mb-2 text-gray-900 dark:text-white">
                ¿Qué pasa después de los 90 días de beta?
              </h3>
              <p className="text-gray-600 dark:text-gray-300">
                Podrás elegir el plan que prefieras o continuar en Basic gratis para siempre. 
                No hay renovación automática ni sorpresas en tu tarjeta.
              </p>
            </div>
            
            <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-md">
              <h3 className="font-semibold mb-2 text-gray-900 dark:text-white">
                ¿Hay garantía de devolución?
              </h3>
              <p className="text-gray-600 dark:text-gray-300">
                Sí, cuando comencemos a cobrar, ofreceremos 30 días de garantía en tu primera compra 
                (no aplicable a renovaciones).
              </p>
            </div>
            
            <div className="bg-white dark:bg-gray-800 rounded-lg p-6 shadow-md">
              <h3 className="font-semibold mb-2 text-gray-900 dark:text-white">
                ¿Puedo cambiar de plan en cualquier momento?
              </h3>
              <p className="text-gray-600 dark:text-gray-300">
                Absolutamente. Podrás cambiar, pausar o cancelar tu suscripción con 1 clic 
                desde tu portal de usuario.
              </p>
            </div>
          </div>
        </div>

        {/* Contact Footer */}
        <div className="text-center mt-16 text-gray-600 dark:text-gray-400">
          <p className="mb-2">¿Tienes dudas sobre los planes?</p>
          <p className="text-sm">
            Escríbenos a <a href="mailto:impulse.soporte@gmail.com" className="text-blue-600 hover:underline">impulse.soporte@gmail.com</a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default PricingPage;
