import React, { useEffect } from 'react';
import { Button } from '../components/ui';
import { ArrowRight } from 'lucide-react';

// Deprecated wrapper: keep route compatibility and redirect to unified pricing page
const PricingPage: React.FC = () => {
  useEffect(() => {
    // SPA redirect to new pricing path (assuming /pricing)
    if (window.location.pathname.toLowerCase().includes('pricingpage')) {
      window.location.replace('/pricing');
    }
  }, []);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 via-white to-primary-50">
      <div className="bg-white rounded-2xl shadow-lg p-12 border border-gray-100 text-center max-w-md mx-auto">
        <h2 className="text-2xl font-bold text-primary-700 mb-4">Redirigiendo...</h2>
        <p className="text-base text-gray-600 mb-6">Te estamos llevando a la página de precios actualizada</p>
        <a href="/pricing">
          <Button variant="primary" size="lg" className="shadow-colored flex items-center gap-2 px-6 py-3 text-base font-semibold">
            Ir ahora <ArrowRight className="w-5 h-5" />
          </Button>
        </a>
      </div>
    </div>
  );
};

export default PricingPage;
