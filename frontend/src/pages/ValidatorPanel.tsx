import React from "react";
import mockStore from "../services/mockStore";
import PageHeader from "../components/PageHeader";
import { Button, Card, CardContent } from "../components/ui";
import { CheckCircle, XCircle, AlertTriangle } from "lucide-react";

const ValidatorPanel: React.FC = () => {
  const [items, setItems] = React.useState<any[]>([]);

  React.useEffect(() => {
    // For demo, list all challenges as items to validate
    mockStore.listChallenges().then((ch) => setItems(ch));
  }, []);

  const approve = (id: string) => {
    alert("Aprobado (simulado) " + id);
  };

  const reject = (id: string) => {
    const reason = prompt("Motivo de rechazo") || "sin motivo";
    alert("Rechazado (simulado) " + id + "\n" + reason);
  };

  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 max-w-4xl mx-auto">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Panel de Validadores</span>}
          subtitle={<span className="text-lg text-gray-600">Revisa y valida las evidencias enviadas por los participantes</span>}
        />

        <div className="space-y-6">
          {items.map((item) => (
            <Card key={item.id} className="bg-white shadow-lg border border-gray-100">
              <CardContent className="p-6">
                <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
                  <div className="flex-1">
                    <h3 className="text-xl font-bold text-gray-900 mb-2">{item.title}</h3>
                    <p className="text-gray-600 mb-4">{item.description}</p>
                    <div className="flex items-center gap-2">
                      <AlertTriangle className="w-4 h-4 text-warning-600" />
                      <span className="text-sm text-gray-500">Pendiente de validación</span>
                    </div>
                  </div>
                  <div className="flex gap-3">
                    <Button
                      onClick={() => approve(item.id)}
                      variant="primary"
                      size="sm"
                      className="shadow-colored flex items-center gap-2"
                    >
                      <CheckCircle className="w-4 h-4" />
                      Aprobar
                    </Button>
                    <Button
                      onClick={() => reject(item.id)}
                      variant="error"
                      size="sm"
                      className="shadow-colored flex items-center gap-2"
                    >
                      <XCircle className="w-4 h-4" />
                      Rechazar
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}

          {items.length === 0 && (
            <div className="text-center py-16">
              <CheckCircle className="w-16 h-16 mx-auto mb-4 text-gray-400" />
              <p className="text-lg font-semibold text-gray-600 mb-2">No hay elementos para validar</p>
              <p className="text-sm text-gray-500">Cuando haya evidencias pendientes aparecerán aquí</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ValidatorPanel;
