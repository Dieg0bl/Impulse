import React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { Check, X, Zap, Users, Crown, Star } from "lucide-react";
import { useConfig } from "../services/configService";
import { PRICING_COPY, formatPrice, getAllPlans, isPlanAvailable } from "../config/pricing";

const Pricing: React.FC = () => {
  const config = useConfig();

  // Filter plans based on availability
  const availablePlans = getAllPlans().filter((plan) =>
    isPlanAvailable(plan.id, config.isCoachMarketEnabled),
  );

  const getButtonText = (planId: string): string => {
    if (!config.isBillingEnabled) {
      return planId === "basic" ? "Plan Actual" : "Próximamente";
    }
    return config.getCheckoutButtonText(planId);
  };

  const handlePlanSelection = (planId: string) => {
    if (planId === "basic") {
      alert("Ya estás en el plan Basic gratuito");
      return;
    }

    if (!config.isBillingEnabled) {
      alert(config.getBillingDisabledMessage());
      return;
    }

    // Aquí iría la integración con Stripe cuando BILLING_ON = true
    alert(`Redirigiendo a checkout para plan ${planId}`);
  };

  const getIcon = (planId: string) => {
    switch (planId) {
      case "basic":
        return <Zap className="h-6 w-6" />;
      case "pro":
        return <Star className="h-6 w-6" />;
      case "teams":
        return <Users className="h-6 w-6" />;
      case "coach":
        return <Crown className="h-6 w-6" />;
      default:
        return <Zap className="h-6 w-6" />;
    }
  };

  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 max-w-6xl mx-auto">
        {/* Hero Section with Specification Copy */}
        <div className="text-center mb-16">
          <h1 className="text-4xl font-extrabold text-primary-700 tracking-tight mb-4">{PRICING_COPY.hero}</h1>

          {/* Beta Notice */}
          <div className="bg-primary-100 border border-primary-200 rounded-2xl shadow-lg p-6 mb-8 max-w-4xl mx-auto">
            <p className="text-primary-800 text-lg font-semibold">
              ✨ <strong>Beta Abierta</strong> - {PRICING_COPY.betaNote}
            </p>
            <p className="text-primary-600 text-sm mt-2">
              Días restantes de beta: <strong>{config.betaDaysRemaining}</strong>
            </p>
          </div>
        </div>

        {/* Pricing Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
          {availablePlans.map((plan) => (
            <Card
              key={plan.id}
              className={`relative bg-white shadow-lg border border-gray-100 ${plan.popular ? "ring-2 ring-primary-500 shadow-xl scale-105" : ""}`}
            >
              {plan.popular && (
                <Badge className="absolute -top-3 left-1/2 transform -translate-x-1/2 bg-primary-500 text-white shadow-colored">
                  Más Popular
                </Badge>
              )}

              <CardHeader className="text-center pb-2">
                <div className="w-12 h-12 mx-auto bg-primary-100 rounded-lg flex items-center justify-center mb-4">
                  {getIcon(plan.id)}
                </div>

                <CardTitle className="text-2xl font-bold text-primary-700">{plan.name}</CardTitle>

                <div className="mt-4">
                  <span className="text-4xl font-bold text-gray-900">
                    {formatPrice(plan.price)}
                  </span>
                  {plan.price > 0 && <span className="text-gray-500 ml-1">/{plan.period}</span>}
                </div>

                {/* Yearly pricing for Pro */}
                {plan.yearlyPrice && (
                  <p className="text-sm text-green-600 mt-1 font-medium">
                    o {formatPrice(plan.yearlyPrice)}/año (ahorra 2 meses)
                  </p>
                )}

                {/* Team configuration */}
                {plan.teamConfig && (
                  <p className="text-sm text-gray-600 mt-1 font-medium">
                    Incluye {plan.teamConfig.included} miembros
                    <br />+{formatPrice(plan.teamConfig.extraMemberPrice)}/miembro extra
                  </p>
                )}
              </CardHeader>

              <CardContent className="p-6">
                <ul className="space-y-3 mb-6">
                  {plan.features.map((feature) => (
                    <li key={feature.name} className="flex items-start">
                      <Check className="h-5 w-5 text-green-500 mt-0.5 mr-3 flex-shrink-0" />
                      <span className="text-gray-700 font-medium">{feature.name}</span>
                    </li>
                  ))}
                </ul>

                <Button
                  onClick={() => handlePlanSelection(plan.id)}
                  className={`w-full shadow-colored ${
                    plan.popular ? "bg-primary-600 hover:bg-primary-700" : "bg-gray-900 hover:bg-gray-800"
                  }`}
                  disabled={plan.id !== "basic" && !config.isBillingEnabled}
                >
                  {getButtonText(plan.id)}
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Guarantee Section */}
        {config.isBillingEnabled && (
          <div className="text-center mb-12">
            <div className="bg-green-50 border border-green-200 rounded-2xl shadow-lg p-6 max-w-2xl mx-auto">
              <h3 className="text-lg font-semibold text-green-800 mb-2">🛡️ Garantía de 30 días</h3>
              <p className="text-green-700 font-medium">{PRICING_COPY.guarantee}</p>
            </div>
          </div>
        )}

        {/* Features Comparison Table */}
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden mb-12 border border-gray-100">
          <div className="px-6 py-4 bg-gray-50 border-b">
            <h3 className="text-xl font-bold text-primary-700">Comparación Detallada de Planes</h3>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Característica
                  </th>
                  {availablePlans.map((plan) => (
                    <th
                      key={plan.id}
                      className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider"
                    >
                      {plan.name}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                <tr>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    Retos Activos
                  </td>
                  {availablePlans.map((plan) => (
                    <td
                      key={plan.id}
                      className="px-6 py-4 whitespace-nowrap text-sm text-gray-700 font-medium text-center"
                    >
                      {plan.limits.activeChallenges === "unlimited"
                        ? "∞"
                        : plan.limits.activeChallenges}
                    </td>
                  ))}
                </tr>

                <tr className="bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    Validadores por Reto
                  </td>
                  {availablePlans.map((plan) => (
                    <td
                      key={plan.id}
                      className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center"
                    >
                      {plan.limits.validatorsPerChallenge === "unlimited"
                        ? "∞"
                        : plan.limits.validatorsPerChallenge}
                    </td>
                  ))}
                </tr>

                <tr>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    Soporte Multimedia
                  </td>
                  {availablePlans.map((plan) => (
                    <td
                      key={plan.id}
                      className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center"
                    >
                      {plan.limits.mediaSupport}
                    </td>
                  ))}
                </tr>

                <tr className="bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    Exportación de Datos
                  </td>
                  {availablePlans.map((plan) => (
                    <td
                      key={plan.id}
                      className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center"
                    >
                      {plan.limits.export ? (
                        <Check className="h-5 w-5 text-green-500 mx-auto" />
                      ) : (
                        <X className="h-5 w-5 text-red-500 mx-auto" />
                      )}
                    </td>
                  ))}
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        {/* FAQ Section */}
        <div className="max-w-4xl mx-auto mb-12">
          <h3 className="text-2xl font-bold text-gray-900 text-center mb-8">
            Preguntas Frecuentes
          </h3>

          <div className="space-y-6">
            <div className="bg-white rounded-lg shadow p-6">
              <h4 className="font-semibold text-gray-900 mb-2">
                ¿Qué pasa cuando termine la beta?
              </h4>
              <p className="text-gray-700">
                Al finalizar los 90 días de beta, recibirás avisos con 15, 7 y 1 día de antelación.
                Podrás elegir un plan de pago o continuar con Basic gratis, sin interrupciones.
              </p>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <h4 className="font-semibold text-gray-900 mb-2">
                ¿Puedo cancelar en cualquier momento?
              </h4>
              <p className="text-gray-700">
                Sí, puedes cancelar tu suscripción en cualquier momento desde tu Portal de Usuario
                con un solo clic. No hay permanencia ni penalizaciones.
              </p>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <h4 className="font-semibold text-gray-900 mb-2">
                ¿Cómo funciona la garantía de 30 días?
              </h4>
              <p className="text-gray-700">
                La garantía aplica solo a la primera compra de cualquier plan (no a renovaciones).
                Excluye casos de uso intensivo manifiesto del servicio.
              </p>
            </div>
          </div>
        </div>

        {/* Footer Legal */}
        <div className="text-center text-sm text-gray-600 bg-gray-50 rounded-lg p-6">
          <p className="mb-4">{PRICING_COPY.footer}</p>

          <div className="space-y-2">
            <p>
              <strong>IMPULSE</strong> – nombre comercial de {config.ownerName}
            </p>
            <p>{config.address}</p>
            <p>
              Soporte:{" "}
              <a href={`mailto:${config.supportEmail}`} className="text-blue-600 hover:underline">
                {config.supportEmail}
              </a>{" "}
              · RGPD:{" "}
              <a href={`mailto:${config.legalEmail}`} className="text-blue-600 hover:underline">
                {config.legalEmail}
              </a>{" "}
              · Abusos:{" "}
              <a href={`mailto:${config.abuseEmail}`} className="text-blue-600 hover:underline">
                {config.abuseEmail}
              </a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Pricing;
