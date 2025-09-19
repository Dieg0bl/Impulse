import React, { useState } from "react";
import { Badge } from "./ui/Badge";
import { AppCard } from "../ui/AppCard";
import { AppButton } from "../ui/AppButton";
import { ValidationResponseDto } from "../types/dtos";
import {
  ValidationStatus,
  ValidationDecision,
  ValidationMethod,
  ValidationPriority
} from "../types/enums";
import {
  CheckCircle,
  XCircle,
  Clock,
  AlertCircle,
  Info,
  User,
  Star,
  MessageSquare,
  Eye,
  ThumbsUp,
  ThumbsDown
} from "lucide-react";
interface ValidationCardProps {
  validation: ValidationResponseDto;
  onDecision?: (decision: ValidationDecision, feedback?: string) => void;
  onView?: () => void;
  variant?: "compact" | "full";
  showActions?: boolean;
}

const ValidationCard: React.FC<ValidationCardProps> = ({
  validation,
  onDecision,
  onView,
  variant = "compact",
  showActions = true
}) => {
  const [showFeedback, setShowFeedback] = useState(false);
  const [feedback, setFeedback] = useState("");

  const getStatusIcon = (status: ValidationStatus) => {
    const icons = {
      [ValidationStatus.PENDING]: <Clock className="w-4 h-4" />,
      [ValidationStatus.IN_PROGRESS]: <AlertCircle className="w-4 h-4" />,
      [ValidationStatus.COMPLETED]: <CheckCircle className="w-4 h-4" />,
      [ValidationStatus.REJECTED]: <XCircle className="w-4 h-4" />,
      [ValidationStatus.EXPIRED]: <XCircle className="w-4 h-4" />
    };
    return icons[status] || <Info className="w-4 h-4" />;
  };
  const getStatusColor = (status: ValidationStatus): "primary" | "secondary" | "success" | "warning" | "error" | "info" => {
    const colors = {
      [ValidationStatus.PENDING]: "warning" as const,
      [ValidationStatus.IN_PROGRESS]: "info" as const,
      [ValidationStatus.COMPLETED]: "success" as const,
      [ValidationStatus.REJECTED]: "error" as const,
      [ValidationStatus.EXPIRED]: "secondary" as const
    };
    return colors[status] || "secondary";
  };

  const getPriorityColor = (priority: ValidationPriority): "primary" | "secondary" | "success" | "warning" | "error" | "info" => {
    const colors = {
      [ValidationPriority.LOW]: "success" as const,
      [ValidationPriority.MEDIUM]: "warning" as const,
      [ValidationPriority.HIGH]: "error" as const,
      [ValidationPriority.URGENT]: "error" as const
    };

    return colors[priority] || "secondary";
  };

  const getMethodIcon = (method: ValidationMethod) => {
    const icons = {
      [ValidationMethod.MANUAL]: <User className="w-4 h-4" />,
      [ValidationMethod.AUTOMATIC]: <CheckCircle className="w-4 h-4" />,
      [ValidationMethod.PEER_REVIEW]: <User className="w-4 h-4" />,
      [ValidationMethod.AI_ASSISTED]: <Star className="w-4 h-4" />
    };

    return icons[method] || <User className="w-4 h-4" />;
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

  const handleDecision = (decision: ValidationDecision) => {
    if (onDecision) {
      onDecision(decision, feedback);
      setFeedback("");
    }
  };
  if (variant === "compact") {
    return (
      <AppCard
        style={{
          padding: "var(--space-4)",
          background: "rgb(var(--surface-2))",
          borderRadius: "var(--radius-lg)",
          border: "1px solid rgb(var(--surface-3))",
          boxShadow: "0 2px 8px rgba(0,0,0,0.04)"
        }}
          className="flex items-center justify-between transition-colors"
      >
        <div className="flex items-center space-x-3 flex-1">
          <div className="flex items-center space-x-2">
            {getStatusIcon(validation.status)}
            <Badge variant={getStatusColor(validation.status)} size="sm">
              {validation.status}
            </Badge>
          </div>

          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-white truncate">
              {validation.type.replace(/_/g, ' ')} - {validation.evidenceId}
            </p>
            <div className="flex items-center space-x-2 text-xs text-gray-400">
              <span className="flex items-center">
                {getMethodIcon(validation.method)}
                <span className="ml-1">{validation.method}</span>
              </span>
              <span>•</span>
              <Badge variant={getPriorityColor(validation.priority)} size="sm">
                {validation.priority}
              </Badge>
            </div>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          {onView && (
            <AppButton color="secondary" size="compact" onClick={onView}>
              <Eye className="w-4 h-4" />
            </AppButton>
          )}
          {showActions && validation.status === ValidationStatus.PENDING && (
            <>
              <AppButton
                color="primary"
                size="compact"
                onClick={() => handleDecision(ValidationDecision.APPROVED)}
              >
                <ThumbsUp className="w-4 h-4" />
              </AppButton>
              <AppButton
                color="error"
                size="compact"
                onClick={() => handleDecision(ValidationDecision.REJECTED)}
              >
                <ThumbsDown className="w-4 h-4" />
              </AppButton>
            </>
          )}
        </div>
      </AppCard>
    );
  }

  // Full variant
  return (
    <AppCard
      style={{
        padding: "var(--space-6)",
        borderRadius: "var(--radius-lg)",
        boxShadow: "0 2px 8px rgba(0,0,0,0.04)",
        background: "rgb(var(--surface-2))",
        border: "1px solid rgb(var(--surface-3))"
      }}
    >
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center space-x-3">
          <div className={`w-12 h-12 rounded-full bg-gradient-to-r from-blue-500 to-blue-600 flex items-center justify-center text-white`}>
            {getMethodIcon(validation.method)}
          </div>
          <div>
            <h3 className="text-lg font-semibold text-white">
              {validation.type.replace(/_/g, ' ')}
            </h3>
            <div className="flex items-center space-x-2 mt-1">
              <Badge variant={getStatusColor(validation.status)}>
                {validation.status}
              </Badge>
              <Badge variant={getPriorityColor(validation.priority)} size="sm">
                {validation.priority}
              </Badge>
            </div>
          </div>
        </div>

        <div className="flex space-x-2">
          {onView && (
            <AppButton color="secondary" size="compact" onClick={onView}>
              <Eye className="w-4 h-4 mr-2" />
              Ver Evidencia
            </AppButton>
          )}
        </div>
      </div>

    <div className="grid grid-cols-2 gap-4 mb-4">
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Evidencia ID
          </span>
          <p className="text-sm text-white mt-1">{validation.evidenceId}</p>
        </div>
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Método
          </span>
          <div className="flex items-center mt-1">
            {getMethodIcon(validation.method)}
            <span className="text-sm text-white ml-2">{validation.method}</span>
          </div>
        </div>
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Creado
          </span>
    </div>
        {validation.completedAt && (
          <div>
            <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
              Completado
            </span>
            <p className="text-sm text-white mt-1">{formatDate(validation.completedAt)}</p>
          </div>
        )}
      </div>

      {validation.comments && (
        <div className="mb-4 p-3 bg-gray-800/50 rounded-lg">
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Comentarios
          </span>
          <p className="text-sm text-gray-300 mt-1">{validation.comments}</p>
        </div>
      )}

      {showActions && validation.status === ValidationStatus.PENDING && (
        <div className="pt-4 border-t border-gray-700">
          <div className="flex items-center justify-between mb-3">
            <h4 className="text-sm font-medium text-white">Decisión de Validación</h4>
            <AppButton
              color="secondary"
              size="compact"
              onClick={() => setShowFeedback(!showFeedback)}
            >
              <MessageSquare className="w-4 h-4 mr-2" />
              {showFeedback ? "Ocultar" : "Agregar"} Feedback
            </AppButton>
          </div>

          {showFeedback && (
            <div className="mb-4">
              <textarea
                value={feedback}
                onChange={(e) => setFeedback(e.target.value)}
                placeholder="Agregar comentarios sobre la validación..."
                className="w-full p-3 bg-gray-800 border border-gray-600 rounded-lg text-white text-sm resize-none"
                rows={3}
              />
            </div>
          )}

          <div className="flex space-x-3">
            <AppButton
              color="primary"
              onClick={() => handleDecision(ValidationDecision.APPROVED)}
              style={{ flex: 1 }}
            >
              <ThumbsUp className="w-4 h-4 mr-2" />
              Aprobar
            </AppButton>
            <AppButton
              color="error"
              onClick={() => handleDecision(ValidationDecision.REJECTED)}
              style={{ flex: 1 }}
            >
              <ThumbsDown className="w-4 h-4 mr-2" />
              Rechazar
            </AppButton>
            <AppButton
              color="secondary"
              onClick={() => handleDecision(ValidationDecision.NEEDS_MORE_INFO)}
              style={{ flex: 1 }}
            >
              <Info className="w-4 h-4 mr-2" />
              Más Info
            </AppButton>
          </div>
        </div>
      )}

      {validation.decision && validation.status === ValidationStatus.COMPLETED && (
        <div className="pt-4 border-t border-gray-700">
          <div className="flex items-center space-x-2">
            <span className="text-sm font-medium text-gray-400">Decisión Final:</span>
            <Badge variant={validation.decision === ValidationDecision.APPROVED ? "success" : "error"}>
              {validation.decision}
            </Badge>
          </div>
        </div>
      )}
  </AppCard>
  );
};

export default ValidationCard;
