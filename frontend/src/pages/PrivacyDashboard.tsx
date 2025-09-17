import React from "react";
import PageHeader from "../components/PageHeader";
import { Card, CardContent } from "../components/ui";
import { Shield, Lock, Eye, Download } from "lucide-react";

const PrivacyDashboard: React.FC = () => {
  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 max-w-4xl mx-auto">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Panel de Privacidad</span>}
          subtitle={<span className="text-lg text-gray-600">Gestiona tu privacidad y controla tus datos personales</span>}
        />

        <div className="grid gap-8 md:grid-cols-2">
          <Card className="bg-white shadow-lg border border-gray-100">
            <CardContent className="p-6">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                  <Shield className="w-6 h-6 text-primary-600" />
                </div>
                <h3 className="text-xl font-bold text-primary-700">Protección de Datos</h3>
              </div>
              <p className="text-gray-600 mb-4">
                Tus datos están protegidos con cifrado de extremo a extremo y almacenados de forma segura.
              </p>
              <div className="space-y-2 text-sm text-gray-600">
                <p>• Cifrado AES-256 para datos sensibles</p>
                <p>• Acceso limitado a personal autorizado</p>
                <p>• Auditorías de seguridad regulares</p>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white shadow-lg border border-gray-100">
            <CardContent className="p-6">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                  <Lock className="w-6 h-6 text-primary-600" />
                </div>
                <h3 className="text-xl font-bold text-primary-700">Control de Acceso</h3>
              </div>
              <p className="text-gray-600 mb-4">
                Configura quién puede ver tu perfil y actividad en la plataforma.
              </p>
              <div className="space-y-2 text-sm text-gray-600">
                <p>• Perfil público/privado</p>
                <p>• Visibilidad de retos</p>
                <p>• Compartir estadísticas</p>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white shadow-lg border border-gray-100">
            <CardContent className="p-6">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                  <Eye className="w-6 h-6 text-primary-600" />
                </div>
                <h3 className="text-xl font-bold text-primary-700">Transparencia</h3>
              </div>
              <p className="text-gray-600 mb-4">
                Mantente informado sobre cómo utilizamos y protegemos tu información.
              </p>
              <div className="space-y-2 text-sm text-gray-600">
                <p>• Registro de actividad</p>
                <p>• Notificaciones de cambios</p>
                <p>• Políticas actualizadas</p>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-white shadow-lg border border-gray-100">
            <CardContent className="p-6">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                  <Download className="w-6 h-6 text-primary-600" />
                </div>
                <h3 className="text-xl font-bold text-primary-700">Exportar Datos</h3>
              </div>
              <p className="text-gray-600 mb-4">
                Descarga una copia de todos tus datos en formato portable.
              </p>
              <div className="space-y-2 text-sm text-gray-600">
                <p>• Datos de perfil</p>
                <p>• Historial de retos</p>
                <p>• Evidencias subidas</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default PrivacyDashboard;
