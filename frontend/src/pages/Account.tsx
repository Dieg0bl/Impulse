import React from "react";
import { useAuth } from "../providers/AuthProvider";
import mockStore, { Challenge } from "../services/mockStore";
import PageHeader from "../components/PageHeader";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";

const Account: React.FC = () => {
  const { user, logout } = useAuth();

  const exportData = async () => {
    if (!user) return;
    const data: { user: any; challenges: Challenge[] } = { user, challenges: [] };
    data.challenges = await mockStore.listChallenges();
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url; a.download = "impulse-data.json"; a.click();
  };

  const deleteAccount = async () => {
    if (confirm("¿Borrar cuenta (simulado)?")) {
      logout();
      alert("Cuenta borrada (simulado)");
    }
  };

  return (
    <div className="min-h-[70vh] flex flex-col bg-gradient-to-br from-gray-50 via-white to-primary-50 pb-20">
      <div className="container-app pt-10 max-w-3xl mx-auto w-full">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Cuenta</span>}
          subtitle={user ? <span className="text-lg text-gray-600">Gestiona tu perfil y datos <span className="font-semibold text-primary-600">({user.email})</span></span> : <span className="text-lg text-gray-600">No has iniciado sesión.</span>}
        />

        <div className="space-y-8">
          {!user && (
            <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 text-center">
              <p className="mb-4 text-gray-600 text-base">Para ver los detalles de tu cuenta inicia sesión.</p>
              <a href="/login" className="inline-block">
                <Button variant="primary" size="lg" className="shadow-colored px-6 py-3 text-base font-semibold">Ir a Login</Button>
              </a>
            </div>
          )}

          {user && (
            <div className="grid gap-8 md:grid-cols-2">
              <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 space-y-6">
                <h3 className="text-xl font-bold text-primary-700 mb-2">Perfil</h3>
                <div className="text-base space-y-3">
                  <p><span className="font-semibold text-gray-700">Nombre:</span> <span className="text-gray-900">{user.firstName} {user.lastName}</span></p>
                  <p><span className="font-semibold text-gray-700">Email:</span> <span className="text-gray-900">{user.email}</span></p>
                  <p>
                    <span className="font-semibold text-gray-700">Estado:</span>{' '}
                    <Badge variant="success" size="sm">Activo</Badge>
                  </p>
                </div>
                <div className="flex flex-wrap gap-3 pt-4">
                  <Button onClick={logout} variant="secondary" size="sm" className="shadow-colored">Cerrar sesión</Button>
                  <Button onClick={exportData} variant="outline" size="sm" className="shadow-colored">Exportar datos</Button>
                  <Button onClick={deleteAccount} variant="error" size="sm" className="shadow-colored">Borrar cuenta</Button>
                </div>
              </div>

              <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 space-y-6">
                <h3 className="text-xl font-bold text-primary-700 mb-2">Privacidad & Datos</h3>
                <p className="text-base text-gray-600 leading-relaxed">
                  Puedes exportar tus datos en formato JSON para portabilidad. En futuras versiones podrás configurar la visibilidad de tus retos, borrado selectivo y descarga de evidencias.
                </p>
                <ul className="list-disc list-inside text-sm text-gray-500 space-y-2">
                  <li>Exportación incluye retos listados actualmente</li>
                  <li>Los borrados son permanentes (simulado en esta versión)</li>
                  <li>Próximamente: configuración de idioma y notificaciones</li>
                </ul>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Account;
