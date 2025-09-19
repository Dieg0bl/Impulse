import React from 'react';

const About: React.FC = () => (
  <div className="min-h-screen bg-gray-50">
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow-sm p-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Sobre Impulse</h1>
        <p className="text-lg text-gray-600 mb-6">
          Impulse es una plataforma dedicada a potenciar el crecimiento personal y profesional a través de desafíos, análisis y comunidad.
        </p>
        <section className="mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Nuestra misión</h2>
          <p className="text-gray-700">
            Empoderar a las personas para alcanzar sus metas mediante tecnología, datos y acompañamiento humano.
          </p>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">El equipo</h2>
          <ul className="list-disc list-inside text-gray-700 space-y-1">
            <li>Diego Blanco – Fundador & CEO</li>
            <li>María López – CTO</li>
            <li>Carlos Ruiz – Lead Designer</li>
            <li>Equipo de ingeniería, producto y soporte</li>
          </ul>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Valores</h2>
          <ul className="list-disc list-inside text-gray-700 space-y-1">
            <li>Innovación y mejora continua</li>
            <li>Privacidad y seguridad de los datos</li>
            <li>Inclusión y accesibilidad</li>
            <li>Transparencia y ética</li>
          </ul>
        </section>
        <section className="mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Contacto</h2>
          <p className="text-gray-700">
            ¿Tienes preguntas? Escríbenos a <a href="mailto:hola@impulse.com" className="text-blue-600 underline">hola@impulse.com</a>
          </p>
        </section>
      </div>
    </div>
  </div>
);

export default About;
