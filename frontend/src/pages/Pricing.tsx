import React, { useState } from "react";
import { motion } from "framer-motion";
import { Check, Zap, Users, Crown, ChevronDown, ChevronUp } from "lucide-react";
import PageHeader from "../components/PageHeader";
import { AppButton } from "../ui/AppButton";
import { GlassSurface } from "../ui/GlassSurface";

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

interface FAQItem {
  question: string;
  answer: string;
}

const plans: Plan[] = [
  {
    id: "basic",
    name: "Basic",
    price: "Gratis",
    period: "siempre",
    description: "Perfecto para empezar tu viaje de crecimiento personal",
    icon: <Zap className="h-6 w-6" />,
    features: [
      { text: "Hasta 2 retos activos", included: true },
      { text: "3 validadores por reto", included: true },
      { text: "Texto + 1 imagen por evidencia", included: true },
      { text: "Privacidad estándar", included: true },
      { text: "Historial básico", included: true },
      { text: "Retos ilimitados", included: false },
      { text: "Multimedia completa", included: false },
      { text: "Retos privados/equipo", included: false },
    ],
    buttonText: "Plan Actual",
  },
  {
    id: "pro",
    name: "Pro",
    price: "9.99€",
    period: "mes",
    description: "Para usuarios que quieren maximizar su potencial",
    icon: <Users className="h-6 w-6" />,
    popular: true,
    features: [
      { text: "Retos ilimitados activos", included: true },
      { text: "10 validadores por reto", included: true },
      { text: "Multimedia completa (video, audio)", included: true },
      { text: "Retos privados y de equipo", included: true },
      { text: "Analytics avanzado", included: true },
      { text: "Prioridad en soporte", included: true },
      { text: "Insignias y logros", included: true },
      { text: "Exportar progreso", included: true },
    ],
    buttonText: "Obtener Pro",
  },
  {
    id: "premium",
    name: "Premium",
    price: "19.99€",
    period: "mes",
    description: "La experiencia definitiva para líderes y equipos",
    icon: <Crown className="h-6 w-6" />,
    features: [
      { text: "Todo lo de Pro", included: true },
      { text: "Validadores ilimitados", included: true },
      { text: "Coach personal asignado", included: true },
      { text: "Retos corporativos", included: true },
      { text: "API y integraciones", included: true },
      { text: "Reportes personalizados", included: true },
      { text: "Soporte 24/7", included: true },
      { text: "White-label disponible", included: true },
    ],
    buttonText: "Contactar Ventas",
  },
];

const faqItems: FAQItem[] = [
  {
    question: "¿Puedo cambiar de plan en cualquier momento?",
    answer: "Sí, puedes actualizar o degradar tu plan en cualquier momento. Los cambios se aplicarán inmediatamente y se facturarán de forma proporcional.",
  },
  {
    question: "¿Hay compromiso de permanencia?",
    answer: "No, todos nuestros planes son sin compromiso. Puedes cancelar tu suscripción en cualquier momento desde tu panel de control.",
  },
  {
    question: "¿Qué métodos de pago aceptan?",
    answer: "Aceptamos todas las tarjetas de crédito principales (Visa, Mastercard, American Express), PayPal y transferencias bancarias para planes empresariales.",
  },
  {
    question: "¿Ofrecen descuentos para equipos?",
    answer: "Sí, ofrecemos descuentos especiales para equipos de 10+ usuarios y planes corporativos. Contacta con nuestro equipo de ventas para más información.",
  },
];

const Pricing: React.FC = () => {
  const [openFAQ, setOpenFAQ] = useState<number | null>(null);

  const handleSelectPlan = (planId: string) => {
    // Aquí iría la lógica de navegación al checkout
    console.log(`Selected plan: ${planId}`);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 via-white to-primary-50">
      <PageHeader
        title="Elige tu plan"
      />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Pricing Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          {plans.map((plan, index) => (
            <motion.div
              key={plan.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="relative"
            >
              <GlassSurface
                elevation={plan.popular ? 2 : 1}
                variant={plan.popular ? "prominent" : "default"}
                sx={{
                  p: 4,
                  transform: plan.popular ? "scale(1.05)" : "scale(1)",
                  border: plan.popular ? "2px solid rgb(var(--color-primary))" : undefined,
                  transition: "all var(--duration-medium) var(--ease-standard)",
                  "&:hover": {
                    transform: plan.popular ? "scale(1.06)" : "scale(1.02)",
                  }
                }}
              >
              {plan.popular && (
                <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
                  <span className="bg-gradient-to-r from-primary-600 to-primary-700 text-white px-4 py-1 rounded-full text-sm font-semibold">
                    Más Popular
                  </span>
                </div>
              )}

              <div className="text-center mb-8">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-primary-100 to-primary-200 rounded-2xl mb-4">
                  <div className="text-primary-600">
                    {plan.icon}
                  </div>
                </div>

                <h3 className="text-2xl font-bold text-gray-900 mb-2">{plan.name}</h3>
                <p className="text-gray-600 mb-4">{plan.description}</p>

                <div className="flex items-baseline justify-center mb-4">
                  <span className="text-4xl font-extrabold text-gray-900">{plan.price}</span>
                  {plan.period && (
                    <span className="text-gray-500 ml-2">/{plan.period}</span>
                  )}
                </div>
              </div>

              <ul className="space-y-4 mb-8">
                {plan.features.map((feature) => (
                  <li key={`${plan.id}-feature-${feature.text.slice(0, 10)}`} className="flex items-start">
                    <div className={`flex-shrink-0 w-5 h-5 rounded-full flex items-center justify-center mr-3 mt-0.5 ${
                      feature.included
                        ? 'bg-green-100 text-green-600'
                        : 'bg-gray-100 text-gray-400'
                    }`}>
                      <Check className="w-3 h-3" />
                    </div>
                    <span className={`text-sm ${
                      feature.included ? 'text-gray-700' : 'text-gray-400'
                    }`}>
                      {feature.text}
                    </span>
                  </li>
                ))}
              </ul>

              <AppButton
                onClick={() => handleSelectPlan(plan.id)}
                variant={plan.popular ? "contained" : "outlined"}
                color={plan.popular ? "primary" : "secondary"}
                size="large"
                disabled={plan.disabled}
                fullWidth
                sx={{ mt: 2 }}
              >
                {plan.buttonText || `Seleccionar ${plan.name}`}
              </AppButton>
              </GlassSurface>
            </motion.div>
          ))}
        </div>

        {/* FAQ Section */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="max-w-4xl mx-auto"
        >
          <div className="text-center mb-12">
            <h2 className="text-3xl font-extrabold text-gray-900 mb-4">
              Preguntas Frecuentes
            </h2>
            <p className="text-xl text-gray-600">
              Resolvemos las dudas más comunes sobre nuestros planes
            </p>
          </div>

          <div className="space-y-6">
            {faqItems.map((item, index) => (
              <motion.div
                key={`faq-${item.question.slice(0, 20).replace(/\s+/g, '-')}`}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.5 + index * 0.1 }}
              >
                <GlassSurface
                  elevation={1}
                  sx={{
                    overflow: 'hidden',
                    '&:hover': {
                      elevation: 2,
                      transform: 'translateY(-2px)',
                      transition: 'all var(--duration-fast) var(--ease-standard)'
                    }
                  }}
                >
                  <AppButton
                    onClick={() => setOpenFAQ(openFAQ === index ? null : index)}
                    variant="text"
                    size="large"
                    fullWidth
                    sx={{
                      justifyContent: 'space-between',
                      px: 4,
                      py: 3,
                      textAlign: 'left',
                      '&:hover': {
                        backgroundColor: 'rgba(var(--color-surface-2), 0.5)'
                      }
                    }}
                    iconPosition="end"
                    icon={openFAQ === index ?
                      <ChevronUp className="w-5 h-5 text-gray-500" /> :
                      <ChevronDown className="w-5 h-5 text-gray-500" />
                    }
                  >
                    <span className="text-lg font-semibold text-gray-900">
                      {item.question}
                    </span>
                  </AppButton>

                  {openFAQ === index && (
                    <motion.div
                      initial={{ height: 0, opacity: 0 }}
                      animate={{ height: "auto", opacity: 1 }}
                      exit={{ height: 0, opacity: 0 }}
                      transition={{ duration: 0.3 }}
                      className="px-8 pb-6"
                    >
                      <p className="text-gray-600 leading-relaxed">
                        {item.answer}
                      </p>
                    </motion.div>
                  )}
                </GlassSurface>
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* CTA Section */}
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.6 }}
          className="text-center mt-16 bg-gradient-to-r from-primary-600 to-primary-700 rounded-3xl p-12 text-white"
        >
          <h2 className="text-3xl font-extrabold mb-4">
            ¿Listo para transformar tu vida?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Únete a miles de usuarios que ya están alcanzando sus objetivos
          </p>
          <AppButton
            onClick={() => handleSelectPlan("pro")}
            variant="contained"
            size="large"
            sx={{
              backgroundColor: 'white',
              color: 'rgb(var(--color-primary))',
              fontWeight: 600,
              py: 2,
              px: 4,
              '&:hover': {
                backgroundColor: 'rgba(255, 255, 255, 0.9)',
                transform: 'translateY(-2px) scale(1.02)',
                boxShadow: 'var(--shadow-lg)'
              }
            }}
          >
            Comenzar ahora
          </AppButton>
        </motion.div>
      </div>
    </div>
  );
};

export default Pricing;
