import React from 'react';
import { Check, X } from 'lucide-react';
import { PricingPlan, formatPrice } from '../../config/pricing';

interface ComparisonTableProps {
  plans: PricingPlan[];
}

const ComparisonTable: React.FC<ComparisonTableProps> = ({ plans }) => {
  if (!plans.length) return null;
  return (
    <section aria-label="Comparativa de planes" className="max-w-full md:max-w-7xl mx-auto mb-24 px-4">
      <div className="surface rounded-xl overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th className="text-left px-4 py-3 font-semibold text-gray-700 dark:text-gray-200">Característica</th>
              {plans.map(p => (
                <th key={p.id} className="px-4 py-3 text-center font-semibold text-gray-700 dark:text-gray-200 whitespace-nowrap">
                  {p.name}
                  <div className="text-xs font-normal text-gray-500 dark:text-gray-400">{p.price === 0 ? 'Gratis' : formatPrice(p.price)}</div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr className="border-t">
              <td className="px-4 py-3 font-medium">Retos activos</td>
              {plans.map(p => (
                <td key={p.id} className="px-4 py-3 text-center">{p.limits.activeChallenges === 'unlimited' ? '∞' : p.limits.activeChallenges}</td>
              ))}
            </tr>
            <tr className="border-t bg-gray-50/60 dark:bg-gray-800/40">
              <td className="px-4 py-3 font-medium">Validadores por reto</td>
              {plans.map(p => (
                <td key={p.id} className="px-4 py-3 text-center">{p.limits.validatorsPerChallenge === 'unlimited' ? '∞' : p.limits.validatorsPerChallenge}</td>
              ))}
            </tr>
            <tr className="border-t">
              <td className="px-4 py-3 font-medium">Multimedia</td>
              {plans.map(p => (
                <td key={p.id} className="px-4 py-3 text-center text-xs md:text-sm">{p.limits.mediaSupport}</td>
              ))}
            </tr>
            <tr className="border-t bg-gray-50/60 dark:bg-gray-800/40">
              <td className="px-4 py-3 font-medium">Exportación</td>
              {plans.map(p => (
                <td key={p.id} className="px-4 py-3 text-center">
                  {p.limits.export ? <Check className="w-5 h-5 text-green-500 inline"/> : <X className="w-5 h-5 text-red-400 inline"/>}
                </td>
              ))}
            </tr>
            <tr className="border-t">
              <td className="px-4 py-3 font-medium">Soporte</td>
              {plans.map(p => (
                <td key={p.id} className="px-4 py-3 text-center text-xs md:text-sm">{p.limits.support}</td>
              ))}
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  );
};

export default ComparisonTable;
