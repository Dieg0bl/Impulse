import React, { useState } from 'react';
import { tryUploadEvidence } from '../services/api';
import { Button } from '../components/ui';
import { UploadCloud } from 'lucide-react';

const EvidenceUpload: React.FC<{ challengeId: string; onUploaded?: () => void }> = ({
  challengeId,
  onUploaded,
}) => {
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState("");
  const [uploading, setUploading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return setMessage("Seleccione un archivo");
    setUploading(true);
    setMessage("Subiendo...");
    try {
      // call api wrapper which will fallback to mock
      await tryUploadEvidence(challengeId, file);
      setMessage("Evidencia enviada (mock/real)");
      if (onUploaded) onUploaded();
    } catch (err: any) {
      console.warn("upload error", err);
      setMessage("Error al subir (simulado)");
    } finally {
      setFile(null);
      setUploading(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-100 max-w-lg mx-auto">
      <form onSubmit={submit} className="space-y-6" aria-describedby="ayuda-evidencia">
        <p id="ayuda-evidencia" className="sr-only">Formulario para subir un archivo como evidencia</p>
        <div className="flex flex-col gap-3">
          <label htmlFor="archivo-evidencia" className="text-base font-semibold text-primary-700 mb-1">Archivo</label>
          <input
            id="archivo-evidencia"
            type="file"
            onChange={(e) => setFile(e.target?.files?.[0] ?? null)}
            className="block w-full text-base px-4 py-3 rounded-lg border border-gray-300 focus:ring-2 focus:ring-primary-500 bg-gray-50 file:mr-4 file:py-2 file:px-3 file:rounded-md file:border-0 file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
          />
        </div>
        <div className="flex justify-end">
          <Button type="submit" variant="primary" size="lg" className="shadow-colored flex items-center gap-2 px-6 py-3 text-base font-semibold" disabled={uploading || !file} icon={<UploadCloud className="w-5 h-5"/>}>
            {uploading ? 'Subiendo...' : 'Enviar evidencia'}
          </Button>
        </div>
        {message && <output className="block text-sm text-gray-600 mt-2" aria-live="polite">{message}</output>}
      </form>
    </div>
  );
};

export default EvidenceUpload;
