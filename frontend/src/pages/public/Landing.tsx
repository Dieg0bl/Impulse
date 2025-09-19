import React, { useEffect, useCallback, useState } from 'react';
import { PRICING_PLANS, formatPrice } from '../../config/pricing';
import { useConfig } from '../../services/configService';
import { statsApi } from '../../services/api';
import AppButton from '../../ui/AppButton';

const Landing: React.FC = () => {
  const config = useConfig();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [platformStats, setPlatformStats] = useState<any | null>(null);
  const [offline, setOffline] = useState(false);

  // Simple metrics helper (silent failures)
  const postEvent = useCallback(async (type: string, details: Record<string, any> = {}) => {
    try {
      await fetch('/api/v1/events', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ type, details, timestamp: new Date().toISOString() }),
      });
    } catch {
      // ignore
    }
  }, []);

  useEffect(() => {
    document.title = 'IMPULSE — Retos, evidencias y validación humana';
    const run = async () => {
      setLoading(true);
      setError(null);
      try {
        const stats = await statsApi.getPlatformStats();
        setPlatformStats(stats || null);
        postEvent('landing_view', {});
      } catch (e: any) {
        if (!navigator.onLine) setOffline(true);
        setError(e?.message || 'Error cargando datos');
      } finally {
        setLoading(false);
      }
    };
    run();
    const onOnline = () => { setOffline(false); run(); };
    const onOffline = () => setOffline(true);
    window.addEventListener('online', onOnline);
    window.addEventListener('offline', onOffline);
    return () => { window.removeEventListener('online', onOnline); window.removeEventListener('offline', onOffline); };
  }, [postEvent]);

  const handlePrimaryCTA = () => {
    postEvent('cta_register_click', {});
    // If user is logged redirect to create, else to register
    const token = localStorage.getItem('token');
    if (token) {
      window.location.href = '/challenges/new';
    } else {
      window.location.href = '/register';
    }
  };

  const handleViewPricing = () => {
    postEvent('cta_view_pricing', {});
    window.location.href = '/pricing';
  };

  if (loading) {
    return (
      <main className="container-app">
        <div className="animate-pulse">
          <div className="h-96 bg-gray-200 rounded mb-8"></div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
            <div className="h-32 bg-gray-200 rounded"></div>
            <div className="h-32 bg-gray-200 rounded"></div>
            <div className="h-32 bg-gray-200 rounded"></div>
          </div>
          <div className="h-64 bg-gray-200 rounded"></div>
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main className="container-app text-center py-12">
        <div className="bg-red-50 border border-red-200 rounded p-4 mb-6" role="alert">
          <p className="text-red-800">{error}</p>
        </div>
        <AppButton onClick={() => window.location.reload()}>Reintentar</AppButton>
      </main>
    );
  }

  return (
    <main className="container-app">
      {/* CMP banner */}
      {config.isCMPEnabled && (
        <section className="bg-gray-50 border-b px-4 py-3 text-sm flex items-center gap-4" aria-label="Consentimiento de cookies">
          <div className="flex-1">Usamos cookies para mejorar la experiencia. Puedes gestionarlas desde el panel de cookies.</div>
          <div className="flex items-center gap-2">
            <a href="/cookies" className="underline text-sm">Gestionar</a>
            <AppButton onClick={() => postEvent('cmp_accept', {})} size="compact">Aceptar</AppButton>
          </div>
        </section>
      )}

      {/* Hero above-the-fold */}
      <section className="hero-section py-12" aria-labelledby="hero-title">
        <div className="text-center">
          <h1 id="hero-title" className="text-4xl md:text-5xl font-extrabold leading-tight mb-4">
            Impulsa tus retos con evidencias y validación humana
          </h1>
          <p className="text-lg text-gray-600 mb-6 max-w-2xl mx-auto">
            Crea retos, sube evidencias y obtén validación fiable y auditada. Privado por defecto. Diseñado para equipos y profesionales.
          </p>
          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <AppButton onClick={handlePrimaryCTA} variant="contained" size="large">
              Crear reto
            </AppButton>
            <AppButton onClick={handleViewPricing} variant="outlined" size="large">
              Ver precios
            </AppButton>
          </div>
          {offline && (
            <div className="mt-4 text-sm text-yellow-700 bg-yellow-50 border border-yellow-200 rounded p-2 inline-block" role="alert">
              Estás sin conexión — algunos datos pueden estar en caché.
            </div>
          )}
        </div>
        <div className="mt-8 flex justify-center">
          <img
            src="/pwa-192x192.png"
            alt="Vista previa de la aplicación IMPULSE"
            width={480}
            height={320}
            className="rounded-lg shadow-lg"
            loading="eager"
          />
        </div>
      </section>

      {/* Cómo funciona */}
      <section className="py-12" aria-labelledby="how-title">
        <h2 id="how-title" className="text-2xl md:text-3xl font-semibold text-center mb-8">Cómo funciona</h2>
        <div className="grid gap-6 md:grid-cols-3">
          <div className="text-center p-6 border rounded-lg">
            <div className="text-4xl mb-4">1️⃣</div>
            <h3 className="text-lg font-semibold mb-2">Crea un reto</h3>
            <p className="text-gray-600">Define objetivo, reglas y visibilidad (privado por defecto).</p>
          </div>
          <div className="text-center p-6 border rounded-lg">
            <div className="text-4xl mb-4">2️⃣</div>
            <h3 className="text-lg font-semibold mb-2">Sube evidencias</h3>
            <p className="text-gray-600">Texto, imágenes o vídeo. EXIF se elimina y pasa por revisión.</p>
          </div>
          <div className="text-center p-6 border rounded-lg">
            <div className="text-4xl mb-4">3️⃣</div>
            <h3 className="text-lg font-semibold mb-2">Validación humana</h3>
            <p className="text-gray-600">Validadores revisan y deciden — todo con trazabilidad y correlationId.</p>
          </div>
        </div>
      </section>

      {/* Privacidad / DSA */}
      <section className="py-12 bg-gray-50" aria-labelledby="privacy-title">
        <div className="text-center">
          <h2 id="privacy-title" className="text-2xl md:text-3xl font-semibold mb-4">Privacidad y auditabilidad</h2>
          <p className="text-gray-600 max-w-3xl mx-auto">
            Por defecto los retos son privados. Cumplimos con DSA y GDPR: control de datos, export y derecho al olvido según política.
            Consulta la política completa en <a href="/privacy" className="underline text-blue-600">Privacidad</a>.
          </p>
        </div>
      </section>

      {/* Planes */}
      {Object.keys(PRICING_PLANS).length > 0 && (
        <section className="py-12" aria-labelledby="plans-title">
          <h2 id="plans-title" className="text-2xl md:text-3xl font-semibold text-center mb-8">Planes</h2>
          <div className="grid gap-6 md:grid-cols-3">
            {Object.keys(PRICING_PLANS).map((k) => {
              const plan = (PRICING_PLANS as any)[k];
              return (
                <article key={plan.id} className="p-6 border rounded-lg shadow-sm">
                  <h3 className="text-xl font-semibold mb-2">{plan.name}</h3>
                  <div className="text-3xl font-bold mb-4">
                    {formatPrice(plan.price)}
                    <span className="text-sm font-normal text-gray-600">/{plan.period === 'month' ? 'mes' : 'año'}</span>
                  </div>
                  <ul className="text-sm text-gray-600 mb-6 space-y-1">
                    {plan.features.slice(0, 3).map((f: any, i: number) => (
                      <li key={f.name}>• {f.name}</li>
                    ))}
                  </ul>
                  <AppButton
                    onClick={() => {
                      postEvent('plan_select', { planId: plan.id });
                      window.location.href = '/pricing';
                    }}
                    variant="contained"
                    size="compact"
                    className="w-full"
                  >
                    Continuar
                  </AppButton>
                </article>
              );
            })}
          </div>
        </section>
      )}

      {/* Prueba social */}
      <section className="py-12 bg-blue-50" aria-labelledby="social-title">
        <div className="text-center">
          <h2 id="social-title" className="text-2xl md:text-3xl font-semibold mb-4">Prueba social</h2>
          <p className="text-gray-600">
            Más del 85% de evidencias validadas en &lt;48h (métrica North Star agregada).
          </p>
        </div>
      </section>

      {/* FAQ */}
      <section className="py-12" aria-labelledby="faq-title">
        <h2 id="faq-title" className="text-2xl md:text-3xl font-semibold text-center mb-8">Preguntas frecuentes</h2>
        <div className="max-w-2xl mx-auto space-y-4">
          <details className="border rounded p-4">
            <summary className="font-medium cursor-pointer">¿Hay costes?</summary>
            <p className="text-sm text-gray-600 mt-2">
              Durante la beta los planes están deshabilitados; consulta los precios en la página de Pricing.
            </p>
          </details>
          <details className="border rounded p-4">
            <summary className="font-medium cursor-pointer">¿Quién valida?</summary>
            <p className="text-sm text-gray-600 mt-2">
              Validadores humanos certificados según la política; también hay opciones automatizadas de apoyo.
            </p>
          </details>
          <details className="border rounded p-4">
            <summary className="font-medium cursor-pointer">¿Qué pasa con mi privacidad?</summary>
            <p className="text-sm text-gray-600 mt-2">
              Controlas la visibilidad por reto y puedes solicitar export o borrado (DSAR).
            </p>
          </details>
        </div>
      </section>

      {/* Footer legal */}
      <footer className="border-t pt-8 mt-12" role="contentinfo">
        <div className="flex flex-col md:flex-row justify-between items-start gap-4">
          <div>
            <div className="font-semibold text-lg">IMPULSE</div>
            <nav className="mt-2 flex flex-wrap gap-4">
              <a href="/terms" className="underline text-gray-600 hover:text-gray-800">Términos</a>
              <a href="/privacy" className="underline text-gray-600 hover:text-gray-800">Privacidad</a>
              <a href="/cookies" className="underline text-gray-600 hover:text-gray-800">Cookies</a>
              <a href="/vdp" className="underline text-gray-600 hover:text-gray-800">VDP</a>
              <a href="/rules" className="underline text-gray-600 hover:text-gray-800">Reglas</a>
              <a href="/contact" className="underline text-gray-600 hover:text-gray-800">Contacto</a>
            </nav>
          </div>
          <div className="text-gray-500 text-sm">
            <p>IMPULSE — impulse.soporte@gmail.com</p>
            <p className="mt-1">Versión FE: 1.0.0 | BE: 1.0.0</p>
          </div>
        </div>
      </footer>
    </main>
  );
};

export default Landing;

