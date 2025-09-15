import React, { useState } from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { Button } from "./ui/Button";
import { EvidenceResponseDto } from "../types/dtos";
import { EvidenceStatus, EvidenceType } from "../types/enums";
import {
  FileText,
  Image,
  Video,
  Download,
  Eye,
  Clock,
  CheckCircle,
  XCircle,
  AlertCircle
} from "lucide-react";

interface EvidenceCardProps {
  evidence: EvidenceResponseDto;
  onView?: () => void;
  onDownload?: () => void;
  onValidate?: () => void;
  showValidationActions?: boolean;
  variant?: "compact" | "full";
}

const EvidenceCard: React.FC<EvidenceCardProps> = ({
  evidence,
  onView,
  onDownload,
  onValidate,
  showValidationActions = false,
  variant = "compact"
}) => {
  const [isExpanded, setIsExpanded] = useState(false);

  const getStatusBadge = (status: EvidenceStatus) => {
    const variants = {
      [EvidenceStatus.PENDING]: "warning",
      [EvidenceStatus.APPROVED]: "success",
      [EvidenceStatus.REJECTED]: "error",
      [EvidenceStatus.UNDER_REVIEW]: "secondary"
    } as const;

    const icons = {
      [EvidenceStatus.PENDING]: <Clock className="w-3 h-3 mr-1" />,
      [EvidenceStatus.APPROVED]: <CheckCircle className="w-3 h-3 mr-1" />,
      [EvidenceStatus.REJECTED]: <XCircle className="w-3 h-3 mr-1" />,
      [EvidenceStatus.UNDER_REVIEW]: <AlertCircle className="w-3 h-3 mr-1" />
    };

    return (
      <Badge variant={variants[status]} className="flex items-center">
        {icons[status]}
        {status}
      </Badge>
    );
  };

  const getTypeIcon = (type: EvidenceType) => {
    const icons = {
      [EvidenceType.IMAGE]: <Image className="w-5 h-5" />,
      [EvidenceType.VIDEO]: <Video className="w-5 h-5" />,
      [EvidenceType.DOCUMENT]: <FileText className="w-5 h-5" />,
      [EvidenceType.TEXT]: <FileText className="w-5 h-5" />,
      [EvidenceType.AUDIO]: <FileText className="w-5 h-5" />,
      [EvidenceType.LINK]: <FileText className="w-5 h-5" />
    };

    return icons[type];
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (variant === "compact") {
    return (
      <Card className="bg-gray-800/50 border-gray-700 hover:border-blue-500 transition-all">
        <div className="p-4">
          <div className="flex items-start justify-between mb-3">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center text-white">
                {getTypeIcon(evidence.type)}
              </div>
              <div className="flex-1 min-w-0">
                <h3 className="font-medium text-white truncate">
                  {evidence.filename || `Evidencia #${evidence.id}`}
                </h3>
                <p className="text-sm text-gray-400 truncate">
                  {evidence.description || 'Sin descripción'}
                </p>
              </div>
            </div>
            {getStatusBadge(evidence.status)}
          </div>

          <div className="flex items-center justify-between">
            <span className="text-xs text-gray-500">
              {formatDate(evidence.submittedAt)}
            </span>
            <div className="flex space-x-1">
              {onView && (
                <Button variant="secondary" size="sm">
                  <Eye className="w-4 h-4" />
                </Button>
              )}
              {onDownload && evidence.fileUrl && (
                <Button variant="secondary" size="sm" onClick={onDownload}>
                  <Download className="w-4 h-4" />
                </Button>
              )}
            </div>
          </div>
        </div>
      </Card>
    );
  }

  // Full variant
  return (
    <Card className="bg-gray-800/50 border-gray-700">
      <div className="p-6">
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center space-x-4">
            <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center text-white">
              {getTypeIcon(evidence.type)}
            </div>
            <div>
              <h3 className="text-lg font-semibold text-white">
                {evidence.filename || `Evidencia #${evidence.id}`}
              </h3>
              <p className="text-sm text-gray-400">
                Tipo: {evidence.type} | Enviado: {formatDate(evidence.submittedAt)}
              </p>
            </div>
          </div>
          {getStatusBadge(evidence.status)}
        </div>

        {evidence.description && (
          <div className="mb-4">
            <p className="text-gray-300">
              {isExpanded || evidence.description.length <= 150 ?
               evidence.description :
               `${evidence.description.slice(0, 150)}...`
              }
              {evidence.description.length > 150 && (
                <button
                  onClick={() => setIsExpanded(!isExpanded)}
                  className="ml-2 text-blue-400 hover:text-blue-300 text-sm"
                >
                  {isExpanded ? 'Ver menos' : 'Ver más'}
                </button>
              )}
            </p>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <h4 className="text-sm font-medium text-gray-400 mb-1">Información</h4>
            <div className="space-y-1 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-500">Challenge ID:</span>
                <span className="text-white">{evidence.challengeId}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Usuario:</span>
                <span className="text-white">{evidence.userId}</span>
              </div>
              {evidence.validatedAt && (
                <div className="flex justify-between">
                  <span className="text-gray-500">Validado:</span>
                  <span className="text-white">{formatDate(evidence.validatedAt)}</span>
                </div>
              )}
            </div>
          </div>

          {evidence.validationScore !== undefined && (
            <div>
              <h4 className="text-sm font-medium text-gray-400 mb-1">Puntuación de Validación</h4>
              <p className="text-sm text-gray-300 bg-gray-900/50 p-2 rounded">
                {evidence.validationScore}/100
              </p>
            </div>
          )}
        </div>

        <div className="flex space-x-3">
          {onView && (
            <Button variant="secondary" onClick={onView}>
              <Eye className="w-4 h-4 mr-2" />
              Ver Detalles
            </Button>
          )}

          {onDownload && evidence.fileUrl && (
            <Button variant="secondary" onClick={onDownload}>
              <Download className="w-4 h-4 mr-2" />
              Descargar
            </Button>
          )}

          {showValidationActions && onValidate && evidence.status === EvidenceStatus.PENDING && (
            <Button variant="primary" onClick={onValidate}>
              <CheckCircle className="w-4 h-4 mr-2" />
              Validar
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
};

export default EvidenceCard;
