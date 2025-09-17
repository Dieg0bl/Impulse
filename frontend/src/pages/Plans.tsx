import React from "react";
import { FEATURE_FLAGS } from "../config";
import PageHeader from "../components/PageHeader";
import { Button, Card, CardContent } from "../components/ui";
import { Zap, Crown, Users, Star } from "lucide-react";

const Plans: React.FC = () => {
  const plans = [
    {
      id: "basic",
      name: "Basic",
      price: "Gratis",
      icon: <Zap className="w-6 h-6 text-primary-600" />,
      features: ["Hasta 2 retos activos", "Validación básica", "Soporte comunidad"],
      current: true
    },
    {
      id: "pro",
      name: "Pro",
      price: "€12.99/mes",
      icon: <Crown className="w-6 h-6 text-primary-600" />,
      features: ["Retos ilimitados", "Validación premium", "Soporte prioritario"],
      popular: true
    },
    {
      id: "teams",
      name: "Teams",
      price: "€29.99/mes",
      icon: <Users className="w-6 h-6 text-primary-600" />,
      features: ["Todo Pro + equipos", "Dashboard colaborativo", "Gestión centralizada"]
    },
    {
      id: "coach",
      name: "Coach",
      price: "Próximamente",
      icon: <Star className="w-6 h-6 text-primary-600" />,
      features: ["Marketplace de coaches", "Sesiones 1:1", "Herramientas avanzadas"],
      disabled: true
    }
  ];

  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 max-w-5xl mx-auto">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Planes</span>}
          subtitle={<span className="text-lg text-gray-600">Encuentra el plan perfecto para alcanzar tus objetivos</span>}
        />

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {plans.map((plan) => (
            <Card
              key={plan.id}
              className={`bg-white shadow-lg border border-gray-100 relative ${plan.popular ? 'ring-2 ring-primary-500 scale-105' : ''}`}
            >
              {plan.popular && (
                <div className="absolute -top-3 left-1/2 transform -translate-x-1/2 bg-primary-500 text-white px-3 py-1 rounded-full text-sm font-semibold shadow-colored">
                  Más Popular
                </div>
              )}

              <CardContent className="p-6 text-center">
                <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                  {plan.icon}
                </div>

                <h3 className="text-xl font-bold text-primary-700 mb-2">{plan.name}</h3>
                <div className="text-2xl font-bold text-gray-900 mb-4">{plan.price}</div>

                <ul className="space-y-2 mb-6 text-sm">
                  {plan.features.map((feature) => (
                    <li key={feature} className="text-gray-600">{feature}</li>
                  ))}
                </ul>

                {plan.current ? (
                  <Button variant="secondary" size="sm" className="w-full shadow-colored" disabled>
                    Plan Actual
                  </Button>
                ) : (
                  <>
                    {FEATURE_FLAGS.BILLING_ON ? (
                      <Button
                        variant={plan.popular ? "primary" : "outline"}
                        size="sm"
                        className="w-full shadow-colored"
                        disabled={plan.disabled}
                      >
                        Suscribirme
                      </Button>
                    ) : (
                      <Button variant="outline" size="sm" className="w-full" disabled>
                        Próximamente
                      </Button>
                    )}
                  </>
                )}
              </CardContent>
            </Card>
          ))}
        </div>

        {!FEATURE_FLAGS.BILLING_ON && (
          <div className="mt-12 text-center">
            <div className="bg-primary-100 border border-primary-200 rounded-2xl shadow-lg p-6 max-w-2xl mx-auto">
              <p className="text-primary-800 font-semibold">
                🚀 Los planes de pago estarán disponibles pronto
              </p>
              <p className="text-primary-600 text-sm mt-2">
                Mientras tanto, disfruta de todas las funciones básicas completamente gratis
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Plans;
