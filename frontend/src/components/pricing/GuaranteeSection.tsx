import React from 'react';

interface GuaranteeSectionProps {
  text: string;
}

const GuaranteeSection: React.FC<GuaranteeSectionProps> = ({ text }) => (
  <section className="max-w-4xl mx-auto mb-20 text-center">
    <div className="surface p-6 md:p-8 rounded-xl border border-green-200/60 bg-green-50 dark:bg-green-900/30 dark:border-green-700/50">
      <h3 className="heading-3 text-green-800 dark:text-green-200 mb-2">🛡️ Garantía 30 días</h3>
      <p className="text-sm md:text-base text-green-700 dark:text-green-300">{text}</p>
    </div>
  </section>
);

export default GuaranteeSection;
