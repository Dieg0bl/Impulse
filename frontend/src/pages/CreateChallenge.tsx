import React, { useState } from 'react';
import mockStore, { Challenge } from '../services/mockStore';
import { useNavigate } from 'react-router-dom';
import { ChallengeCategory, ChallengeDifficulty, ChallengeStatus } from '../types/enums';
import PageHeader from '../components/PageHeader';
import FormField from '../components/FormField';
import { Button } from '../components/ui';
import { Plus, ArrowLeft } from 'lucide-react';

const CreateChallenge: React.FC = () => {
  const [title, setTitle] = useState("");
  const [desc, setDesc] = useState("");
  const navigate = useNavigate();

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title) return alert("Título requerido");

    const c: Challenge = {
      id: Date.now(),
      uuid: "c_" + Date.now(),
      title,
      description: desc,
      category: ChallengeCategory.FITNESS,
      difficulty: ChallengeDifficulty.BEGINNER,
      status: ChallengeStatus.ACTIVE,
      rewardAmount: 0,
      rewardCurrency: "USD",
      startDate: new Date().toISOString(),
      endDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      creatorId: 1,
      creatorUsername: "current_user",
      participantCount: 0,
      isPublic: true,
    };

    mockStore.addChallenge(c);
    navigate(`/challenges/${c.id}`);
  };

  return (
    <div className="pb-16 bg-gradient-to-br from-gray-50 via-white to-primary-50 min-h-screen">
      <div className="container-app pt-8 max-w-3xl mx-auto">
        <PageHeader
          title={<span className="text-3xl font-extrabold text-primary-700 tracking-tight">Crear reto</span>}
          subtitle={<span className="text-lg text-gray-600">Define los detalles básicos para iniciar un nuevo reto público</span>}
          actions={
            <Button variant="outline" size="sm" className="shadow-colored" onClick={() => navigate(-1)}>
              <ArrowLeft className="w-4 h-4 mr-2" /> <span className="font-medium">Volver</span>
            </Button>
          }
        />
        <div className="mt-6">
          <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100">
            <form onSubmit={submit} className="space-y-8" aria-describedby="ayuda-crear-reto">
              <p id="ayuda-crear-reto" className="sr-only">Formulario para crear un nuevo reto</p>
              <div className="grid grid-cols-1 gap-8">
                <FormField
                  id="titulo"
                  label={<span className="text-lg font-semibold text-gray-900">Título</span>}
                  description={<span className="text-sm text-gray-500">Un nombre claro y motivador (máx. 120 caracteres)</span>}
                  required
                  hint={<span className="text-xs text-gray-400">{`${title.length}/120`}</span>}
                >
                  <input
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    maxLength={120}
                    className="w-full input px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-primary-500 text-base font-medium bg-gray-50"
                    placeholder="Ej. 7 días de hábitos saludables"
                    required
                  />
                </FormField>

                <FormField
                  id="descripcion"
                  label={<span className="text-lg font-semibold text-gray-900">Descripción</span>}
                  description={<span className="text-sm text-gray-500">Explica objetivos, reglas y resultado esperado</span>}
                  hint={<span className="text-xs text-gray-400">{`${desc.length}/10000`}</span>}
                >
                  <textarea
                    value={desc}
                    onChange={(e) => setDesc(e.target.value)}
                    maxLength={10000}
                    className="w-full input min-h-[160px] resize-y px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-primary-500 text-base font-medium bg-gray-50"
                    placeholder="Describe el propósito del reto, los pasos diarios y cualquier regla..."
                  />
                </FormField>
              </div>
              <div className="flex justify-end mt-4">
                <Button type="submit" variant="primary" size="lg" className="shadow-colored flex items-center gap-2 px-6 py-3 text-base font-semibold">
                  <Plus className="w-5 h-5" /> <span>Crear reto</span>
                </Button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateChallenge;
