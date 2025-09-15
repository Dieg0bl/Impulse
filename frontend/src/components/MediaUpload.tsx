import React, { useState, useRef, useCallback } from "react";
import { Card } from "./ui/Card";
import { Button } from "./ui/Button";
import { EvidenceType } from "../types/enums";
import {
  Upload,
  X,
  FileText,
  Image,
  Video,
  Music,
  Link,
  CheckCircle,
  AlertCircle
} from "lucide-react";

interface MediaUploadProps {
  onFileSelect: (file: File, type: EvidenceType) => void;
  onUrlSubmit?: (url: string, type: EvidenceType) => void;
  acceptedTypes?: EvidenceType[];
  maxFileSize?: number; // in MB
  className?: string;
}

const MediaUpload: React.FC<MediaUploadProps> = ({
  onFileSelect,
  onUrlSubmit,
  acceptedTypes = [EvidenceType.IMAGE, EvidenceType.VIDEO, EvidenceType.DOCUMENT],
  maxFileSize = 10,
  className = ""
}) => {
  const [dragOver, setDragOver] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [urlInput, setUrlInput] = useState("");
  const [selectedType, setSelectedType] = useState<EvidenceType>(acceptedTypes[0]);
  const [error, setError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const getTypeIcon = (type: EvidenceType) => {
    const icons = {
      [EvidenceType.IMAGE]: <Image className="w-5 h-5" />,
      [EvidenceType.VIDEO]: <Video className="w-5 h-5" />,
      [EvidenceType.DOCUMENT]: <FileText className="w-5 h-5" />,
      [EvidenceType.AUDIO]: <Music className="w-5 h-5" />,
      [EvidenceType.LINK]: <Link className="w-5 h-5" />,
      [EvidenceType.TEXT]: <FileText className="w-5 h-5" />
    };
    return icons[type];
  };

  const getAcceptedFileTypes = () => {
    const typeMap = {
      [EvidenceType.IMAGE]: "image/*",
      [EvidenceType.VIDEO]: "video/*",
      [EvidenceType.AUDIO]: "audio/*",
      [EvidenceType.DOCUMENT]: ".pdf,.doc,.docx,.txt,.xls,.xlsx,.ppt,.pptx"
    };

    return acceptedTypes
      .filter(type => type !== EvidenceType.LINK && type !== EvidenceType.TEXT)
      .map(type => typeMap[type])
      .filter(Boolean)
      .join(",");
  };

  const validateFile = (file: File): string | null => {
    if (file.size > maxFileSize * 1024 * 1024) {
      return `El archivo es demasiado grande. Tamaño máximo: ${maxFileSize}MB`;
    }
    return null;
  };

  const handleFileSelect = useCallback((file: File) => {
    const validationError = validateFile(file);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError(null);
    setSelectedFile(file);

    // Auto-detect file type
    if (file.type.startsWith('image/')) setSelectedType(EvidenceType.IMAGE);
    else if (file.type.startsWith('video/')) setSelectedType(EvidenceType.VIDEO);
    else if (file.type.startsWith('audio/')) setSelectedType(EvidenceType.AUDIO);
    else setSelectedType(EvidenceType.DOCUMENT);
  }, [maxFileSize, selectedType]);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);

    const files = Array.from(e.dataTransfer.files);
    if (files.length > 0) {
      handleFileSelect(files[0]);
    }
  }, [handleFileSelect]);

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleSubmit = () => {
    if (selectedFile) {
      onFileSelect(selectedFile, selectedType);
      clearSelection();
    } else if (urlInput && onUrlSubmit) {
      onUrlSubmit(urlInput, selectedType);
      setUrlInput("");
    }
  };

  const clearSelection = () => {
    setSelectedFile(null);
    setUrlInput("");
    setError(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  return (
    <Card className={`bg-gray-800/50 border-gray-700 ${className}`}>
      <div className="p-6">
        <h3 className="text-lg font-semibold text-white mb-4">Subir Evidencia</h3>

        {/* Type Selection */}
        <div className="mb-4">
          <label className="text-sm font-medium text-gray-400 mb-2 block">
            Tipo de evidencia
          </label>
          <div className="flex flex-wrap gap-2">
            {acceptedTypes.map((type) => (
              <Button
                key={type}
                variant={selectedType === type ? "primary" : "secondary"}
                size="sm"
                onClick={() => setSelectedType(type)}
                className="flex items-center space-x-1"
              >
                {getTypeIcon(type)}
                <span>{type}</span>
              </Button>
            ))}
          </div>
        </div>

        {/* File Upload Area */}
        {(selectedType !== EvidenceType.LINK && selectedType !== EvidenceType.TEXT) && (
          <div
            className={`border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
              dragOver
                ? "border-blue-500 bg-blue-500/10"
                : "border-gray-600 hover:border-gray-500"
            }`}
            onDrop={handleDrop}
            onDragOver={(e) => {
              e.preventDefault();
              setDragOver(true);
            }}
            onDragLeave={() => setDragOver(false)}
          >
            <input
              ref={fileInputRef}
              type="file"
              accept={getAcceptedFileTypes()}
              onChange={handleFileInputChange}
              className="hidden"
            />

            {selectedFile ? (
              <div className="space-y-2">
                <CheckCircle className="w-8 h-8 text-green-500 mx-auto" />
                <p className="text-white font-medium">{selectedFile.name}</p>
                <p className="text-gray-400 text-sm">
                  {(selectedFile.size / 1024 / 1024).toFixed(2)} MB
                </p>
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={clearSelection}
                  className="mt-2"
                >
                  <X className="w-4 h-4 mr-1" />
                  Cambiar archivo
                </Button>
              </div>
            ) : (
              <div className="space-y-2">
                <Upload className="w-8 h-8 text-gray-400 mx-auto" />
                <p className="text-white">
                  Arrastra un archivo aquí o{" "}
                  <button
                    onClick={() => fileInputRef.current?.click()}
                    className="text-blue-400 hover:text-blue-300 underline"
                  >
                    selecciona uno
                  </button>
                </p>
                <p className="text-gray-500 text-sm">
                  Máximo {maxFileSize}MB
                </p>
              </div>
            )}
          </div>
        )}

        {/* URL Input for Links */}
        {selectedType === EvidenceType.LINK && onUrlSubmit && (
          <div className="mb-4">
            <label className="text-sm font-medium text-gray-400 mb-2 block">
              URL del enlace
            </label>
            <input
              type="url"
              value={urlInput}
              onChange={(e) => setUrlInput(e.target.value)}
              placeholder="https://ejemplo.com"
              className="w-full px-3 py-2 bg-gray-900 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        )}

        {/* Error Display */}
        {error && (
          <div className="mb-4 p-3 bg-red-900/20 border border-red-700 rounded-lg flex items-center space-x-2">
            <AlertCircle className="w-5 h-5 text-red-400" />
            <span className="text-red-300 text-sm">{error}</span>
          </div>
        )}

        {/* Submit Button */}
        <div className="flex justify-end">
          <Button
            variant="primary"
            onClick={handleSubmit}
            disabled={!selectedFile && !urlInput}
          >
            {selectedFile ? "Subir Archivo" : "Enviar Enlace"}
          </Button>
        </div>
      </div>
    </Card>
  );
};

export default MediaUpload;
