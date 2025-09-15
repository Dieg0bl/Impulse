import React, { useState, useEffect } from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { useConfig, configService } from "../services/configService";
import {
  Settings,
  ToggleLeft,
  ToggleRight,
  Shield,
  CreditCard,
  Users,
  Calendar,
  Mail,
  MapPin,
  Building2,
  Bookmark,
  Save,
  RefreshCw,
  Gauge,
  Eye,
  Cookie,
  DollarSign,
  Clock,
  Database,
  Lock,
  Globe,
  Gift,
  AlertTriangle
} from "lucide-react";

// Helper components moved outside
const ToggleSwitch: React.FC<{
  label: string;
  value: boolean;
  onChange: (value: boolean) => void;
  icon?: React.ReactNode;
  description?: string;
  disabled?: boolean;
}> = ({ label, value, onChange, icon, description, disabled = false }) => (
  <div className="flex items-center justify-between p-3 bg-gray-800/30 rounded-lg">
    <div className="flex items-center space-x-3">
      {icon && <div className="text-gray-400">{icon}</div>}
      <div>
        <div className="text-sm font-medium text-white">{label}</div>
        {description && <div className="text-xs text-gray-400">{description}</div>}
      </div>
    </div>
    <button
      onClick={() => !disabled && onChange(!value)}
      disabled={disabled}
      className={`transition-colors ${disabled ? "opacity-50 cursor-not-allowed" : "cursor-pointer"}`}
    >
      {value ? (
        <ToggleRight className="w-6 h-6 text-green-400" />
      ) : (
        <ToggleLeft className="w-6 h-6 text-gray-500" />
      )}
    </button>
  </div>
);

const InputField: React.FC<{
  label: string;
  value: string | number;
  onChange: (value: any) => void;
  type?: "text" | "email" | "number" | "date";
  placeholder?: string;
  icon?: React.ReactNode;
}> = ({ label, value, onChange, type = "text", placeholder, icon }) => (
  <div className="space-y-2">
    <label className="flex items-center space-x-2 text-sm font-medium text-gray-300">
      {icon && <div className="text-gray-400">{icon}</div>}
      <span>{label}</span>
    </label>
    <input
      type={type}
      value={value}
      onChange={(e) => onChange(type === "number" ? Number(e.target.value) : e.target.value)}
      placeholder={placeholder}
      className="w-full px-3 py-2 bg-gray-800 border border-gray-600 rounded-md text-white placeholder-gray-400 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-colors"
    />
  </div>
);

interface AdminConfigPanelProps {
  variant?: "full" | "compact" | "toggles-only";
  className?: string;
  onConfigChange?: (config: any) => void;
}

const AdminConfigPanel: React.FC<AdminConfigPanelProps> = ({
  variant = "full",
  className = "",
  onConfigChange
}) => {
  const config = useConfig();
  const [localConfig, setLocalConfig] = useState(configService.getFullConfig());
  const [hasChanges, setHasChanges] = useState(false);
  const [saveStatus, setSaveStatus] = useState<"idle" | "saving" | "saved" | "error">("idle");

  // Update local config when service config changes
  useEffect(() => {
    const newConfig = configService.getFullConfig();
    setLocalConfig(newConfig);
    setHasChanges(false);
  }, [config]);

  const handleToggle = (key: keyof typeof localConfig, value: boolean) => {
    const newConfig = { ...localConfig, [key]: value };
    setLocalConfig(newConfig);
    setHasChanges(true);

    // Apply the change immediately to the service
    switch (key) {
      case "BILLING_ON":
        configService.toggleBilling(value);
        break;
      case "COACH_MARKET_ON":
        configService.toggleCoachMarket(value);
        break;
      case "ADS_ON":
        configService.toggleAds(value);
        break;
      case "CMP_ON":
        configService.toggleCMP(value);
        break;
    }

    if (onConfigChange) {
      onConfigChange(newConfig);
    }
  };

  const handleValueChange = (key: keyof typeof localConfig, value: any) => {
    const newConfig = { ...localConfig, [key]: value };
    setLocalConfig(newConfig);
    setHasChanges(true);

    if (onConfigChange) {
      onConfigChange(newConfig);
    }
  };

  const handleSave = async () => {
    setSaveStatus("saving");
    try {
      // In a real app, this would make an API call
      await new Promise(resolve => setTimeout(resolve, 500));
      setSaveStatus("saved");
      setHasChanges(false);
      setTimeout(() => setSaveStatus("idle"), 2000);
      } catch (error) {
        console.error("Error saving configuration:", error);
      setSaveStatus("error");
      setTimeout(() => setSaveStatus("idle"), 2000);
    }
  };

  const handleReset = () => {
    const freshConfig = configService.getFullConfig();
    setLocalConfig(freshConfig);
    setHasChanges(false);
  };


  if (variant === "toggles-only") {
    return (
      <div className={`space-y-3 ${className}`}>
        <ToggleSwitch
          label="Facturación"
          value={localConfig.BILLING_ON}
          onChange={(value) => handleToggle("BILLING_ON", value)}
          icon={<CreditCard className="w-4 h-4" />}
          description="Sistema de pagos y suscripciones"
        />
        <ToggleSwitch
          label="Marketplace Coach"
          value={localConfig.COACH_MARKET_ON}
          onChange={(value) => handleToggle("COACH_MARKET_ON", value)}
          icon={<Users className="w-4 h-4" />}
          description="Mercado de coaches y servicios"
        />
        <ToggleSwitch
          label="Publicidad"
          value={localConfig.ADS_ON}
          onChange={(value) => handleToggle("ADS_ON", value)}
          icon={<Eye className="w-4 h-4" />}
          description="Sistema de anuncios (Pro siempre sin ads)"
        />
        <ToggleSwitch
          label="CMP (Cookies)"
          value={localConfig.CMP_ON}
          onChange={(value) => handleToggle("CMP_ON", value)}
          icon={<Cookie className="w-4 h-4" />}
          description="Gestión de consentimiento de cookies"
        />
      </div>
    );
  }

  if (variant === "compact") {
    return (
      <Card className={`${className}`}>
        <div className="p-4">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-2">
              <Settings className="w-5 h-5 text-blue-400" />
              <h3 className="text-lg font-semibold text-white">Configuración</h3>
            </div>
            <Badge variant="info" size="sm">ADMIN</Badge>
          </div>

          <div className="grid grid-cols-2 gap-4 mb-4">
            <div className={`p-3 rounded-lg text-center ${localConfig.BILLING_ON ? "bg-green-900/50" : "bg-red-900/50"}`}>
              <CreditCard className="w-5 h-5 mx-auto mb-1 text-gray-300" />
              <div className="text-xs text-gray-300">BILLING</div>
              <div className={`text-sm font-bold ${localConfig.BILLING_ON ? "text-green-400" : "text-red-400"}`}>
                {localConfig.BILLING_ON ? "ON" : "OFF"}
              </div>
            </div>
            <div className={`p-3 rounded-lg text-center ${localConfig.COACH_MARKET_ON ? "bg-green-900/50" : "bg-red-900/50"}`}>
              <Users className="w-5 h-5 mx-auto mb-1 text-gray-300" />
              <div className="text-xs text-gray-300">COACH</div>
              <div className={`text-sm font-bold ${localConfig.COACH_MARKET_ON ? "text-green-400" : "text-red-400"}`}>
                {localConfig.COACH_MARKET_ON ? "ON" : "OFF"}
              </div>
            </div>
          </div>

          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-400">Beta Days:</span>
              <span className="text-white">{localConfig.BETA_DAYS_REMAINING}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Currency:</span>
              <span className="text-white">{localConfig.CURRENCY}</span>
            </div>
          </div>
        </div>
      </Card>
    );
  }

  return (
    <div className={`space-y-6 ${className}`}>
      {/* Header */}
      <Card>
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-center text-white">
                <Settings className="w-5 h-5" />
              </div>
              <div>
                <h2 className="text-xl font-semibold text-white">Panel de Administración</h2>
                <p className="text-sm text-gray-400">Gestión de configuración y feature toggles</p>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <Badge variant="warning" size="sm">DEVELOPMENT</Badge>
              {hasChanges && (
                <Badge variant="info" size="sm">Cambios pendientes</Badge>
              )}
            </div>
          </div>

          {hasChanges && (
            <div className="flex items-center justify-between p-3 bg-yellow-900/20 border border-yellow-600/30 rounded-lg">
              <div className="flex items-center space-x-2">
                <AlertTriangle className="w-4 h-4 text-yellow-400" />
                <span className="text-sm text-yellow-200">Hay cambios sin guardar</span>
              </div>
              <div className="flex items-center space-x-2">
                <button
                  onClick={handleReset}
                  className="px-3 py-1 text-xs bg-gray-600 text-white rounded hover:bg-gray-500 transition-colors"
                >
                  <RefreshCw className="w-3 h-3 inline mr-1" />
                  Resetear
                </button>
                <button
                  onClick={handleSave}
                  disabled={saveStatus === "saving"}
                  className="px-3 py-1 text-xs bg-blue-600 text-white rounded hover:bg-blue-500 transition-colors disabled:opacity-50"
                >
                  <Save className="w-3 h-3 inline mr-1" />
                  {saveStatus === "saving" ? "Guardando..." : "Guardar"}
                </button>
              </div>
            </div>
          )}
        </div>
      </Card>

      {/* Feature Toggles */}
      <Card>
        <div className="p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center">
            <Shield className="w-5 h-5 mr-2 text-blue-400" />
            Feature Toggles
          </h3>

          <div className="space-y-4">
            <ToggleSwitch
              label="Sistema de Facturación"
              value={localConfig.BILLING_ON}
              onChange={(value) => handleToggle("BILLING_ON", value)}
              icon={<CreditCard className="w-4 h-4" />}
              description="Activa pagos, suscripciones y Stripe. Durante beta está desactivado."
            />
            <ToggleSwitch
              label="Marketplace de Coaches"
              value={localConfig.COACH_MARKET_ON}
              onChange={(value) => handleToggle("COACH_MARKET_ON", value)}
              icon={<Users className="w-4 h-4" />}
              description="Mercado de coaches, servicios 1-a-1 y marca blanca."
            />
            <ToggleSwitch
              label="Sistema de Publicidad"
              value={localConfig.ADS_ON}
              onChange={(value) => handleToggle("ADS_ON", value)}
              icon={<Eye className="w-4 h-4" />}
              description="Anuncios para usuarios Basic. Pro siempre libre de ads."
            />
            <ToggleSwitch
              label="Cookie Management Platform"
              value={localConfig.CMP_ON}
              onChange={(value) => handleToggle("CMP_ON", value)}
              icon={<Cookie className="w-4 h-4" />}
              description="Gestión de consentimiento GDPR y cookies de terceros."
            />
          </div>
        </div>
      </Card>

      {/* Beta Configuration */}
      <Card>
        <div className="p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center">
            <Calendar className="w-5 h-5 mr-2 text-green-400" />
            Configuración Beta
          </h3>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <InputField
              label="Días Restantes de Beta"
              value={localConfig.BETA_DAYS_REMAINING}
              onChange={(value) => handleValueChange("BETA_DAYS_REMAINING", value)}
              type="number"
              icon={<Clock className="w-4 h-4" />}
            />
            <InputField
              label="Fecha de Fin de Beta"
              value={localConfig.BETA_END_DATE}
              onChange={(value) => handleValueChange("BETA_END_DATE", value)}
              type="date"
              icon={<Calendar className="w-4 h-4" />}
            />
          </div>
        </div>
      </Card>

      {/* Company & Legal Info */}
      <Card>
        <div className="p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center">
            <Building2 className="w-5 h-5 mr-2 text-purple-400" />
            Información Legal y Empresa
          </h3>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <InputField
              label="Nombre de la Empresa"
              value={localConfig.COMPANY_NAME}
              onChange={(value) => handleValueChange("COMPANY_NAME", value)}
              icon={<Building2 className="w-4 h-4" />}
            />
            <InputField
              label="Propietario"
              value={localConfig.OWNER_NAME}
              onChange={(value) => handleValueChange("OWNER_NAME", value)}
              icon={<Users className="w-4 h-4" />}
            />
            <InputField
              label="Email de Soporte"
              value={localConfig.SUPPORT_EMAIL}
              onChange={(value) => handleValueChange("SUPPORT_EMAIL", value)}
              type="email"
              icon={<Mail className="w-4 h-4" />}
            />
            <InputField
              label="Email Legal"
              value={localConfig.LEGAL_EMAIL}
              onChange={(value) => handleValueChange("LEGAL_EMAIL", value)}
              type="email"
              icon={<Shield className="w-4 h-4" />}
            />
            <InputField
              label="Email de Abusos"
              value={localConfig.ABUSE_EMAIL}
              onChange={(value) => handleValueChange("ABUSE_EMAIL", value)}
              type="email"
              icon={<AlertTriangle className="w-4 h-4" />}
            />
            <InputField
              label="Dirección"
              value={localConfig.ADDRESS}
              onChange={(value) => handleValueChange("ADDRESS", value)}
              icon={<MapPin className="w-4 h-4" />}
            />
          </div>
        </div>
      </Card>

      {/* Pricing Configuration */}
      <Card>
        <div className="p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center">
            <DollarSign className="w-5 h-5 mr-2 text-yellow-400" />
            Configuración de Precios
          </h3>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <InputField
              label="Moneda"
              value={localConfig.CURRENCY}
              onChange={(value) => handleValueChange("CURRENCY", value)}
              icon={<DollarSign className="w-4 h-4" />}
            />
            <InputField
              label="Pro Mensual"
              value={localConfig.PRO_PRICE_MONTHLY}
              onChange={(value) => handleValueChange("PRO_PRICE_MONTHLY", value)}
              type="number"
              icon={<Bookmark className="w-4 h-4" />}
            />
            <InputField
              label="Pro Anual"
              value={localConfig.PRO_PRICE_YEARLY}
              onChange={(value) => handleValueChange("PRO_PRICE_YEARLY", value)}
              type="number"
              icon={<Bookmark className="w-4 h-4" />}
            />
            <InputField
              label="Teams"
              value={localConfig.TEAMS_PRICE}
              onChange={(value) => handleValueChange("TEAMS_PRICE", value)}
              type="number"
              icon={<Users className="w-4 h-4" />}
            />
          </div>
        </div>
      </Card>

      {/* System Status */}
      <Card>
        <div className="p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center">
            <Gauge className="w-5 h-5 mr-2 text-blue-400" />
            Estado del Sistema
          </h3>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center p-3 bg-gray-800/50 rounded-lg">
              <Database className="w-5 h-5 mx-auto mb-2 text-blue-400" />
              <div className="text-xs text-gray-400 mb-1">Base de Datos</div>
              <div className="text-sm font-medium text-white">{localConfig.DATABASE_TYPE}</div>
              <div className="text-xs text-gray-500">{localConfig.DATABASE_VERSION}</div>
            </div>
            <div className="text-center p-3 bg-gray-800/50 rounded-lg">
              <Lock className="w-5 h-5 mx-auto mb-2 text-green-400" />
              <div className="text-xs text-gray-400 mb-1">JWT</div>
              <div className="text-sm font-medium text-white">{localConfig.JWT_ALGORITHM}</div>
              <div className="text-xs text-gray-500">{localConfig.ACCESS_TOKEN_DURATION}min</div>
            </div>
            <div className="text-center p-3 bg-gray-800/50 rounded-lg">
              <Globe className="w-5 h-5 mx-auto mb-2 text-purple-400" />
              <div className="text-xs text-gray-400 mb-1">Región</div>
              <div className="text-sm font-medium text-white">{localConfig.TARGET_REGIONS[0]}</div>
              <div className="text-xs text-gray-500">Primaria</div>
            </div>
            <div className="text-center p-3 bg-gray-800/50 rounded-lg">
              <Gift className="w-5 h-5 mx-auto mb-2 text-orange-400" />
              <div className="text-xs text-gray-400 mb-1">Referrals</div>
              <div className="text-sm font-medium text-white">{localConfig.REFERRAL_LIMIT_MONTHLY}</div>
              <div className="text-xs text-gray-500">por mes</div>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default AdminConfigPanel;
