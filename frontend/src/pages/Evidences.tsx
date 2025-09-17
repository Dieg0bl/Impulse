import React from 'react';
import PageHeader from '../components/PageHeader';
import DataState from '../components/DataState';
import { Button, Card, CardContent } from '../components/ui';
import { UploadCloud, Image as ImageIcon, FileText, Plus } from 'lucide-react';

const Evidences: React.FC = () => {
  // Placeholder states (simular fetch)
  const loading = false;
  const error = null;
  const evidences: Array<{ id: string; title: string; createdAt: string; type: string; }> = [];

  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 max-w-4xl mx-auto space-y-10">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Evidencias</span>}
          subtitle={<span className="text-lg text-gray-600">Gestiona y revisa las evidencias subidas a tus retos</span>}
          actions={<Button variant="primary" size="lg" className="shadow-colored flex items-center gap-2 px-6 py-3 text-base font-semibold"><Plus className="w-5 h-5 mr-2"/>Subir nueva</Button>}
        />

        <section className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 mb-8" aria-labelledby="upload-helper">
          <h3 className="text-xl font-bold text-primary-700 flex items-center gap-2 mb-2"><UploadCloud className="w-5 h-5"/>Subir evidencia</h3>
          <p id="upload-helper" className="text-base text-gray-600 mb-4">Selecciona un archivo (imagen o documento breve) y añade una nota opcional.</p>
          <div className="border border-dashed border-gray-300 rounded-lg p-8 text-center flex flex-col items-center gap-4 bg-gray-50">
            <UploadCloud className="w-10 h-10 text-primary-600 mb-2" />
            <p className="text-base text-gray-600">Arrastra y suelta un archivo aquí o pulsa para explorar</p>
            <Button variant="outline" size="lg" className="shadow-colored px-6 py-3 text-base font-semibold">Elegir archivo</Button>
          </div>
        </section>

        <section aria-labelledby="lista-evidencias" className="mb-8">
          <h3 id="lista-evidencias" className="text-xl font-bold text-primary-700 mb-4">Listado</h3>
          <DataState
            loading={loading}
            error={error}
            empty={evidences.length === 0}
            emptyTitle={<span className="text-lg font-semibold text-gray-600">Sin evidencias todavía</span>}
            emptyDescription={<span className="text-sm text-gray-500">Cuando subas evidencias aparecerán aquí</span>}
          >
            {evidences.length === 0 ? (
              <div />
            ) : (
              <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                {evidences.map(ev => (
                  <Card key={ev.id} className="shadow-md border border-gray-100">
                    <CardContent className="p-6 space-y-3">
                      <div className="flex items-center gap-3">
                        {ev.type === 'image' ? <ImageIcon className="w-5 h-5 text-primary-600"/> : <FileText className="w-5 h-5 text-primary-600"/>}
                        <h4 className="text-base font-semibold truncate text-gray-900">{ev.title}</h4>
                      </div>
                      <p className="text-sm text-gray-500">{new Date(ev.createdAt).toLocaleDateString()}</p>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </DataState>
        </section>
      </div>
    </div>
  );
};

export default Evidences;
