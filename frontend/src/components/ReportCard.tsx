import React, { useState } from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { Button } from "./ui/Button";
import { ReportResponseDto } from "../types/dtos";
import { ReportStatus, ReportReason, ReportPriority, ActionTaken, ContentType } from "../types/enums";
import {
  AlertTriangle,
  Flag,
  Shield,
  Clock,
  CheckCircle,
  XCircle,
  User,
  Eye,
  MessageSquare,
  FileText,
  Target,
  UserX,
  Ban
} from "lucide-react";

interface ReportCardProps {
  report: ReportResponseDto;
  onReview?: () => void;
  onResolve?: (action: ActionTaken) => void;
  onDismiss?: () => void;
  variant?: "compact" | "full";
  showActions?: boolean;
}

const ReportCard: React.FC<ReportCardProps> = ({
  report,
  onReview,
  onResolve,
  onDismiss,
  variant = "compact",
  showActions = true
}) => {
  const [showActionMenu, setShowActionMenu] = useState(false);

  const getStatusIcon = (status: ReportStatus) => {
    const icons = {
      [ReportStatus.OPEN]: <Flag className="w-4 h-4" />,
      [ReportStatus.IN_PROGRESS]: <Clock className="w-4 h-4" />,
      [ReportStatus.RESOLVED]: <CheckCircle className="w-4 h-4" />,
      [ReportStatus.DISMISSED]: <XCircle className="w-4 h-4" />,
      [ReportStatus.ESCALATED]: <AlertTriangle className="w-4 h-4" />
    };

    return icons[status] || <Flag className="w-4 h-4" />;
  };

  const getStatusColor = (status: ReportStatus): "primary" | "secondary" | "success" | "warning" | "error" | "info" => {
    const colors = {
      [ReportStatus.OPEN]: "warning" as const,
      [ReportStatus.IN_PROGRESS]: "info" as const,
      [ReportStatus.RESOLVED]: "success" as const,
      [ReportStatus.DISMISSED]: "secondary" as const,
      [ReportStatus.ESCALATED]: "error" as const
    };

    return colors[status] || "secondary";
  };

  const getPriorityColor = (priority: ReportPriority): "primary" | "secondary" | "success" | "warning" | "error" | "info" => {
    const colors = {
      [ReportPriority.LOW]: "success" as const,
      [ReportPriority.MEDIUM]: "warning" as const,
      [ReportPriority.HIGH]: "error" as const,
      [ReportPriority.CRITICAL]: "error" as const
    };

    return colors[priority] || "secondary";
  };

  const getReasonIcon = (reason: ReportReason) => {
    const icons = {
      [ReportReason.INAPPROPRIATE_CONTENT]: <AlertTriangle className="w-4 h-4" />,
      [ReportReason.SPAM]: <MessageSquare className="w-4 h-4" />,
      [ReportReason.HARASSMENT]: <UserX className="w-4 h-4" />,
      [ReportReason.FAKE_EVIDENCE]: <Shield className="w-4 h-4" />,
      [ReportReason.CHEATING]: <Target className="w-4 h-4" />,
      [ReportReason.COPYRIGHT_VIOLATION]: <FileText className="w-4 h-4" />,
      [ReportReason.OTHER]: <Flag className="w-4 h-4" />
    };

    return icons[reason] || <Flag className="w-4 h-4" />;
  };

  const getContentTypeIcon = (type: ContentType) => {
    const icons = {
      [ContentType.CHALLENGE]: <Target className="w-4 h-4" />,
      [ContentType.EVIDENCE]: <FileText className="w-4 h-4" />,
      [ContentType.COMMENT]: <MessageSquare className="w-4 h-4" />,
      [ContentType.PROFILE]: <User className="w-4 h-4" />,
      [ContentType.MESSAGE]: <MessageSquare className="w-4 h-4" />
    };

    return icons[type] || <FileText className="w-4 h-4" />;
  };

  const getActionIcon = (action: ActionTaken) => {
    const icons = {
      [ActionTaken.WARNING_ISSUED]: <AlertTriangle className="w-4 h-4" />,
      [ActionTaken.CONTENT_REMOVED]: <XCircle className="w-4 h-4" />,
      [ActionTaken.USER_SUSPENDED]: <UserX className="w-4 h-4" />,
      [ActionTaken.USER_BANNED]: <Ban className="w-4 h-4" />,
      [ActionTaken.NO_ACTION]: <CheckCircle className="w-4 h-4" />,
      [ActionTaken.EVIDENCE_REJECTED]: <XCircle className="w-4 h-4" />
    };

    return icons[action] || <CheckCircle className="w-4 h-4" />;
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

  const handleActionSelect = (action: ActionTaken) => {
    if (onResolve) {
      onResolve(action);
    }
    setShowActionMenu(false);
  };

  if (variant === "compact") {
    return (
      <div className="flex items-center justify-between p-4 bg-gray-800/30 rounded-lg border border-gray-700 hover:border-gray-600 transition-colors">
        <div className="flex items-center space-x-3 flex-1">
          <div className="w-10 h-10 rounded-full bg-gradient-to-r from-red-500 to-red-600 flex items-center justify-center text-white">
            {getReasonIcon(report.reason)}
          </div>

          <div className="flex-1 min-w-0">
            <div className="flex items-center space-x-2 mb-1">
              <p className="text-sm font-medium text-white truncate">
                {report.reason.replace(/_/g, ' ')}
              </p>
              <Badge variant={getStatusColor(report.status)} size="sm">
                {report.status}
              </Badge>
              <Badge variant={getPriorityColor(report.priority)} size="sm">
                {report.priority}
              </Badge>
            </div>
            <div className="flex items-center space-x-2 text-xs text-gray-400">
              <span className="flex items-center">
                {getContentTypeIcon(report.targetType)}
                <span className="ml-1">{report.targetType}</span>
              </span>
              <span>•</span>
              <span className="flex items-center">
                <User className="w-3 h-3 mr-1" />
                {report.reporterUsername}
              </span>
              <span>•</span>
              <span>{formatDate(report.createdAt)}</span>
            </div>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          {onReview && (
            <Button variant="secondary" size="sm" onClick={onReview}>
              <Eye className="w-4 h-4" />
            </Button>
          )}
          {showActions && report.status === ReportStatus.OPEN && (
            <div className="relative">
              <Button
                variant="primary"
                size="sm"
                onClick={() => setShowActionMenu(!showActionMenu)}
              >
                Resolver
              </Button>
              {showActionMenu && (
                <div className="absolute right-0 top-full mt-1 bg-gray-800 border border-gray-600 rounded-lg shadow-lg z-10 min-w-48">
                  <div className="py-1">
                    <button
                      onClick={() => handleActionSelect(ActionTaken.WARNING_ISSUED)}
                      className="w-full px-3 py-2 text-left text-sm text-white hover:bg-gray-700 flex items-center"
                    >
                      <AlertTriangle className="w-4 h-4 mr-2" />
                      Emitir Advertencia
                    </button>
                    <button
                      onClick={() => handleActionSelect(ActionTaken.CONTENT_REMOVED)}
                      className="w-full px-3 py-2 text-left text-sm text-white hover:bg-gray-700 flex items-center"
                    >
                      <XCircle className="w-4 h-4 mr-2" />
                      Remover Contenido
                    </button>
                    <button
                      onClick={() => handleActionSelect(ActionTaken.NO_ACTION)}
                      className="w-full px-3 py-2 text-left text-sm text-white hover:bg-gray-700 flex items-center"
                    >
                      <CheckCircle className="w-4 h-4 mr-2" />
                      Sin Acción
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    );
  }

  // Full variant
  return (
    <Card className="p-6">
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center space-x-3">
          <div className="w-12 h-12 rounded-full bg-gradient-to-r from-red-500 to-red-600 flex items-center justify-center text-white">
            {getReasonIcon(report.reason)}
          </div>
          <div>
            <h3 className="text-lg font-semibold text-white">
              Reporte: {report.reason.replace(/_/g, ' ')}
            </h3>
            <div className="flex items-center space-x-2 mt-1">
              <Badge variant={getStatusColor(report.status)}>
                {report.status}
              </Badge>
              <Badge variant={getPriorityColor(report.priority)}>
                {report.priority}
              </Badge>
            </div>
          </div>
        </div>

        <div className="flex space-x-2">
          {onReview && (
            <Button variant="secondary" size="sm" onClick={onReview}>
              <Eye className="w-4 h-4 mr-2" />
              Ver Contenido
            </Button>
          )}
        </div>
      </div>

      <div className="mb-4 p-4 bg-gray-800/50 rounded-lg">
        <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
          Descripción
        </span>
        <p className="text-gray-300 mt-2">
          {report.description || "Sin descripción adicional"}
        </p>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Reportado por
          </span>
          <p className="text-sm text-white mt-1">{report.reporterUsername}</p>
        </div>
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Tipo de Contenido
          </span>
          <div className="flex items-center mt-1">
            {getContentTypeIcon(report.targetType)}
            <span className="text-sm text-white ml-2">{report.targetType}</span>
          </div>
        </div>
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Creado
          </span>
          <p className="text-sm text-white mt-1">{formatDate(report.createdAt)}</p>
        </div>
        {report.resolvedAt && (
          <div>
            <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
              Resuelto
            </span>
            <p className="text-sm text-white mt-1">{formatDate(report.resolvedAt)}</p>
          </div>
        )}
      </div>

      {report.actionTaken && (
        <div className="mb-4 p-3 bg-gray-800/50 rounded-lg">
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Acción Tomada
          </span>
          <div className="flex items-center mt-2">
            {getActionIcon(report.actionTaken)}
            <span className="text-sm text-white ml-2">{report.actionTaken.replace(/_/g, ' ')}</span>
          </div>
        </div>
      )}

      {showActions && report.status === ReportStatus.OPEN && (
        <div className="pt-4 border-t border-gray-700">
          <h4 className="text-sm font-medium text-white mb-3">Acciones Disponibles</h4>
          <div className="grid grid-cols-2 gap-3">
            <Button
              variant="warning"
              onClick={() => handleActionSelect(ActionTaken.WARNING_ISSUED)}
              className="justify-start"
            >
              <AlertTriangle className="w-4 h-4 mr-2" />
              Emitir Advertencia
            </Button>
            <Button
              variant="error"
              onClick={() => handleActionSelect(ActionTaken.CONTENT_REMOVED)}
              className="justify-start"
            >
              <XCircle className="w-4 h-4 mr-2" />
              Remover Contenido
            </Button>
            <Button
              variant="error"
              onClick={() => handleActionSelect(ActionTaken.USER_SUSPENDED)}
              className="justify-start"
            >
              <UserX className="w-4 h-4 mr-2" />
              Suspender Usuario
            </Button>
            <Button
              variant="success"
              onClick={() => handleActionSelect(ActionTaken.NO_ACTION)}
              className="justify-start"
            >
              <CheckCircle className="w-4 h-4 mr-2" />
              Sin Acción Requerida
            </Button>
          </div>

          {onDismiss && (
            <div className="mt-3 pt-3 border-t border-gray-700">
              <Button variant="secondary" onClick={onDismiss}>
                Descartar Reporte
              </Button>
            </div>
          )}
        </div>
      )}

      {report.status !== ReportStatus.OPEN && (
        <div className="pt-4 border-t border-gray-700">
          <div className="flex items-center space-x-2">
            {getStatusIcon(report.status)}
            <span className="text-sm text-gray-400">
              Estado: {report.status}
              {report.resolvedAt && ` - Resuelto el ${formatDate(report.resolvedAt)}`}
            </span>
          </div>
        </div>
      )}
    </Card>
  );
};

export default ReportCard;
