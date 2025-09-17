import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import mockStore, { Challenge, Evidence } from '../services/mockStore';
import EvidenceUpload from './EvidenceUpload';
import PageHeader from '../components/PageHeader';
import DataState from '../components/DataState';
import { Button, Card, CardContent, Badge } from '../components/ui';
import { ArrowLeft, Calendar, Users, Target, UploadCloud } from 'lucide-react';

const ChallengeDetail: React.FC = () => {
  const { id } = useParams();
  const [challenge, setChallenge] = useState<Challenge | null>(null);

  useEffect(() => {
    if (!id) return;
    const found = mockStore.getChallenge(id);
    setChallenge(found);
  }, [id]);

  const [evidences, setEvidences] = useState<Evidence[]>([]);

  const loadEvidences = () => {
    if (!id) return;
    mockStore.listEvidences(id).then(setEvidences);
  };

  React.useEffect(() => {
    loadEvidences();
  }, [id]);

  if (!challenge) return (
    <div className="container-app pt-10 pb-20">
      <PageHeader
        title={<span className="text-2xl md:text-3xl font-extrabold text-error-700 tracking-tight">Reto no encontrado</span>}
        subtitle={<span className="text-lg text-gray-600">El reto que buscas no existe o fue eliminado</span>}
        actions={
          <Link to="/challenges">
            <Button variant="outline" size="sm" className="shadow-colored">
              <ArrowLeft className="w-4 h-4 mr-2" /> <span className="font-medium">Volver</span>
            </Button>
          </Link>
        }
      />
    </div>
  );

  return (
    <div className="pb-20 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-10 max-w-4xl mx-auto">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">{challenge.title}</span>}
          subtitle={<span className="text-lg text-gray-600">{challenge.description}</span>}
          actions={
            <Link to="/challenges">
              <Button variant="outline" size="sm" className="shadow-colored">
                <ArrowLeft className="w-4 h-4 mr-2" /> <span className="font-medium">Volver</span>
              </Button>
            </Link>
          }
        />

        {/* Challenge meta */}
        <div className="grid gap-6 md:grid-cols-3 mb-10">
          <Card className="shadow-md border border-gray-100">
            <CardContent className="p-6 flex items-center gap-4">
              <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                <Calendar className="w-6 h-6 text-primary-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Duración</p>
                <p className="text-base font-semibold text-gray-900">7 días</p>
              </div>
            </CardContent>
          </Card>
          <Card className="shadow-md border border-gray-100">
            <CardContent className="p-6 flex items-center gap-4">
              <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                <Users className="w-6 h-6 text-primary-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Participantes</p>
                <p className="text-base font-semibold text-gray-900">{challenge.participantCount || 0}</p>
              </div>
            </CardContent>
          </Card>
          <Card className="shadow-md border border-gray-100">
            <CardContent className="p-6 flex items-center gap-4">
              <div className="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                <Target className="w-6 h-6 text-primary-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Estado</p>
                <p className="text-base font-semibold text-gray-900"><Badge variant="primary">{challenge.status}</Badge></p>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Evidence upload */}
        <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 mb-10">
          <h3 className="text-xl font-bold text-primary-700 flex items-center gap-2 mb-4"><UploadCloud className="w-5 h-5"/>Enviar evidencia</h3>
          <EvidenceUpload challengeId={String(challenge.id)} onUploaded={() => loadEvidences()} />
        </div>

        {/* Evidences list */}
        <section aria-labelledby="evidencias-titulo" className="mb-10">
          <h3 id="evidencias-titulo" className="text-xl font-bold text-primary-700 mb-4">Evidencias</h3>
          <DataState
            loading={false}
            error={null}
            empty={evidences.length === 0}
            emptyTitle={<span className="text-lg font-semibold text-gray-600">Sin evidencias aún</span>}
            emptyDescription={<span className="text-sm text-gray-500">Cuando se suban evidencias aparecerán aquí</span>}
          >
            {evidences.length === 0 ? (
              <div />
            ) : (
              <ul className="space-y-3">
                {evidences.map(ev => (
                  <li key={ev.id} className="bg-gray-50 border border-gray-200 p-4 rounded-lg flex justify-between items-center text-sm shadow-sm">
                    <span className="font-medium truncate text-gray-900">{ev.filename}</span>
                    <span className="text-gray-500 text-xs">{new Date(ev.submittedAt).toLocaleString()}</span>
                  </li>
                ))}
              </ul>
            )}
          </DataState>
        </section>
      </div>
    </div>
  );
};

export default ChallengeDetail;
