import React from "react";
import { HERO_COPY } from "../config/copy";
import { useConfig } from "../services/configService";

interface HeroSectionProps {
  showBetaBanner?: boolean;
  className?: string;
}

const HeroSection: React.FC<HeroSectionProps> = ({ showBetaBanner = true, className = "" }) => {
  const config = useConfig();

  return (
    <section
      className={`bg-gradient-to-br from-blue-600 to-purple-700 text-white py-20 px-4 ${className}`}
    >
      <div className="max-w-4xl mx-auto text-center">
        {/* Main Hero Copy - Specification Compliant */}
        <h1 className="text-5xl md:text-6xl font-bold mb-6 leading-tight">{HERO_COPY.main}</h1>

        <p className="text-xl md:text-2xl mb-8 text-blue-100">{HERO_COPY.subtitle}</p>

        {/* Beta Notice Banner - Section 2 specification */}
        {showBetaBanner && (
          <div className="bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl p-6 mb-8 max-w-3xl mx-auto">
            <div className="flex items-center justify-center mb-3">
              <span className="text-2xl mr-2">✨</span>
              <h3 className="text-lg font-semibold">Beta Abierta - 90 Días Gratis</h3>
            </div>

            <p className="text-blue-100 text-lg leading-relaxed">{HERO_COPY.betaNotice}</p>

            {/* Beta countdown */}
            <div className="mt-4 flex items-center justify-center space-x-6 text-sm">
              <div className="bg-white/20 rounded-lg px-4 py-2">
                <span className="font-bold text-lg">{config.betaDaysRemaining}</span>
                <span className="block text-xs text-blue-200">días restantes</span>
              </div>
              <div className="text-blue-200">
                Finaliza: <span className="font-semibold">{config.betaEndDate}</span>
              </div>
            </div>
          </div>
        )}

        {/* CTA Buttons */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <button className="bg-white text-blue-600 font-semibold px-8 py-3 rounded-lg hover:bg-blue-50 transition-colors shadow-lg">
            Comenzar Ahora
          </button>

          <button className="border-2 border-white text-white font-semibold px-8 py-3 rounded-lg hover:bg-white hover:text-blue-600 transition-colors">
            Ver Planes
          </button>
        </div>

        {/* Features highlight */}
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6 text-center">
          <div className="bg-white/10 backdrop-blur-sm rounded-lg p-4">
            <div className="text-3xl mb-2">🎯</div>
            <h4 className="font-semibold mb-1">Retos Personales</h4>
            <p className="text-sm text-blue-200">Crea y completa retos adaptados a tus objetivos</p>
          </div>

          <div className="bg-white/10 backdrop-blur-sm rounded-lg p-4">
            <div className="text-3xl mb-2">👥</div>
            <h4 className="font-semibold mb-1">Validación Comunitaria</h4>
            <p className="text-sm text-blue-200">
              La comunidad valida tu progreso de forma transparente
            </p>
          </div>

          <div className="bg-white/10 backdrop-blur-sm rounded-lg p-4">
            <div className="text-3xl mb-2">🔒</div>
            <h4 className="font-semibold mb-1">Privado por Defecto</h4>
            <p className="text-sm text-blue-200">
              Tu información es privada salvo que decidas compartirla
            </p>
          </div>
        </div>

        {/* Legal footer notice */}
        <div className="mt-16 pt-8 border-t border-white/20">
          <p className="text-xs text-blue-200 leading-relaxed max-w-2xl mx-auto">
            {HERO_COPY.legalFooter}
          </p>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
