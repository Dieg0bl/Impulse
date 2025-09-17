import React, { useEffect, useState } from "react";
import { motion } from "framer-motion";
import AdPartnershipService, {
  AdNetworkConfig,
  BrandPartnership,
  SponsoredContent,
  AffiliateProgram,
  AdRevenueReport
} from "../services/AdPartnershipService";
import PageHeader from "../components/PageHeader";

const AdminAdsDashboard: React.FC = () => {
  const [adNetworks, setAdNetworks] = useState<AdNetworkConfig[]>([]);
  const [partnerships, setPartnerships] = useState<BrandPartnership[]>([]);
  const [sponsored, setSponsored] = useState<SponsoredContent[]>([]);
  const [affiliates, setAffiliates] = useState<AffiliateProgram[]>([]);
  const [revenue, setRevenue] = useState<AdRevenueReport | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      setAdNetworks(await AdPartnershipService.getAdNetworks());
      setPartnerships(await AdPartnershipService.getBrandPartnerships());
      setSponsored(await AdPartnershipService.getSponsoredContent());
      setAffiliates(await AdPartnershipService.getAffiliatePrograms());
      setRevenue(await AdPartnershipService.getAdRevenueReport("mensual"));
      setLoading(false);
    }
    load();
  }, []);

  if (loading) return <div className="p-10 text-center text-gray-500">Cargando publicidad y partnerships...</div>;

  return (
    <div className="container-app py-10 space-y-8 max-w-7xl mx-auto">
      <PageHeader
        title={<span className="font-extrabold text-primary-700 tracking-tight">Publicidad & Partnerships</span>}
        subtitle="Gestiona campañas, partnerships, afiliados y revenue de la plataforma."
      />
      {/* Revenue summary */}
      {revenue && (
        <div className="bg-white/80 rounded-2xl shadow-lg border border-gray-200 p-6 flex flex-col gap-2">
          <div className="font-bold text-lg text-gray-900 mb-2">Revenue mensual: <span className="text-primary-700">${revenue.totalRevenue.toFixed(2)}</span></div>
          <div className="flex gap-6 text-sm text-gray-700">
            <span>Impresiones: <b>{revenue.totalImpressions}</b></span>
            <span>Clicks: <b>{revenue.totalClicks}</b></span>
            <span>CPM: <b>${revenue.cpm.toFixed(2)}</b></span>
            <span>CTR: <b>{(revenue.ctr * 100).toFixed(2)}%</b></span>
          </div>
          <div className="mt-2 text-xs text-gray-500">Breakdown: {revenue.breakdown.map(b => `${b.source}: $${b.revenue}`).join(' | ')}</div>
        </div>
      )}
      {/* Ad Networks */}
      <div className="bg-white/80 rounded-2xl shadow-lg border border-gray-200 p-6">
        <div className="font-bold text-gray-900 mb-2">Ad Networks</div>
        <div className="flex flex-wrap gap-4">
          {adNetworks.map(net => (
            <div key={net.network} className="px-4 py-2 rounded-lg border bg-gray-50 text-xs">
              <b>{net.network}</b> {net.enabled ? <span className="text-green-600">(activo)</span> : <span className="text-gray-400">(inactivo)</span>}<br />
              CPM mínimo: ${net.minCpm} | Target: {net.targeting.join(', ')}
            </div>
          ))}
        </div>
      </div>
      {/* Brand Partnerships */}
      <div className="bg-white/80 rounded-2xl shadow-lg border border-gray-200 p-6">
        <div className="font-bold text-gray-900 mb-2">Brand Partnerships</div>
        <div className="flex flex-wrap gap-4">
          {partnerships.map(p => (
            <div key={p.id} className="px-4 py-2 rounded-lg border bg-gray-50 text-xs">
              <b>{p.brand}</b> ({p.campaignType})<br />
              {p.description}<br />
              {p.active ? <span className="text-green-600">Activo</span> : <span className="text-gray-400">Finalizado</span>} | Revenue share: {(p.revenueShare * 100).toFixed(0)}%
            </div>
          ))}
        </div>
      </div>
      {/* Sponsored Content */}
      <div className="bg-white/80 rounded-2xl shadow-lg border border-gray-200 p-6">
        <div className="font-bold text-gray-900 mb-2">Sponsored Content</div>
        <div className="flex flex-wrap gap-4">
          {sponsored.map(s => (
            <div key={s.id} className="px-4 py-2 rounded-lg border bg-gray-50 text-xs">
              <b>{s.title}</b> ({s.brand})<br />
              Tipo: {s.contentType} | CTA: {s.cta}<br />
              Impresiones: {s.impressions} | Clicks: {s.clicks}
            </div>
          ))}
        </div>
      </div>
      {/* Affiliate Programs */}
      <div className="bg-white/80 rounded-2xl shadow-lg border border-gray-200 p-6">
        <div className="font-bold text-gray-900 mb-2">Affiliate Programs</div>
        <div className="flex flex-wrap gap-4">
          {affiliates.map(a => (
            <div key={a.id} className="px-4 py-2 rounded-lg border bg-gray-50 text-xs">
              <b>{a.partner}</b> | Comisión: {(a.commissionRate * 100).toFixed(1)}%<br />
              Revenue: ${a.totalRevenue.toFixed(2)}<br />
              <a href={a.trackingUrl} className="text-primary-700 underline" target="_blank" rel="noopener noreferrer">Tracking</a>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AdminAdsDashboard;
