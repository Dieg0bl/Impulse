import React from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { Button } from "./ui/Button";
import { InvitationResponseDto } from "../types/dtos";
import { InvitationStatus } from "../types/enums";
import {
  Mail,
  Check,
  X,
  Clock,
  User,
  Calendar,
  UserPlus
} from "lucide-react";

interface InvitationCardProps {
  invitation: InvitationResponseDto;
  onAccept?: () => void;
  onDecline?: () => void;
  onView?: () => void;
  variant?: "compact" | "full";
  showActions?: boolean;
}

const InvitationCard: React.FC<InvitationCardProps> = ({
  invitation,
  onAccept,
  onDecline,
  onView,
  variant = "compact",
  showActions = true
}) => {
  const getStatusIcon = (status: InvitationStatus) => {
    const icons = {
      [InvitationStatus.PENDING]: <Clock className="w-4 h-4" />,
      [InvitationStatus.ACCEPTED]: <Check className="w-4 h-4" />,
      [InvitationStatus.DECLINED]: <X className="w-4 h-4" />,
      [InvitationStatus.EXPIRED]: <X className="w-4 h-4" />
    };

    return icons[status] || <Clock className="w-4 h-4" />;
  };

  const getStatusColor = (status: InvitationStatus): "primary" | "secondary" | "success" | "warning" | "error" | "info" => {
    const colors = {
      [InvitationStatus.PENDING]: "warning" as const,
      [InvitationStatus.ACCEPTED]: "success" as const,
      [InvitationStatus.DECLINED]: "error" as const,
      [InvitationStatus.EXPIRED]: "secondary" as const
    };

    return colors[status] || "secondary";
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

  const getTimeRemaining = (expirationDate: string) => {
    const now = new Date();
    const expiry = new Date(expirationDate);
    const diffTime = expiry.getTime() - now.getTime();

    if (diffTime <= 0) return "Expirada";

    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 1) return "Expira hoy";
    if (diffDays < 7) return `Expira en ${diffDays} días`;

    return `Expira el ${expiry.toLocaleDateString('es-ES')}`;
  };

  if (variant === "compact") {
    return (
      <div className="flex items-center justify-between p-4 bg-gray-800/30 rounded-lg border border-gray-700 hover:border-gray-600 transition-colors">
        <div className="flex items-center space-x-3 flex-1">
          <div className="w-10 h-10 rounded-full bg-gradient-to-r from-blue-500 to-blue-600 flex items-center justify-center text-white">
            <UserPlus className="w-5 h-5" />
          </div>

          <div className="flex-1 min-w-0">
            <div className="flex items-center space-x-2 mb-1">
              <p className="text-sm font-medium text-white truncate">
                {invitation.challengeTitle}
              </p>
              <Badge variant={getStatusColor(invitation.status)} size="sm">
                {invitation.status}
              </Badge>
            </div>
            <div className="flex items-center space-x-2 text-xs text-gray-400">
              <span className="flex items-center">
                <User className="w-3 h-3 mr-1" />
                de {invitation.inviterUsername}
              </span>
              <span>•</span>
              <span className="flex items-center">
                <Calendar className="w-3 h-3 mr-1" />
                {formatDate(invitation.createdAt)}
              </span>
            </div>
            {invitation.status === InvitationStatus.PENDING && invitation.expiresAt && (
              <p className="text-xs text-yellow-400 mt-1">
                {getTimeRemaining(invitation.expiresAt)}
              </p>
            )}
          </div>
        </div>

        <div className="flex items-center space-x-2">
          {showActions && invitation.status === InvitationStatus.PENDING && (
            <>
              <Button
                variant="primary"
                size="sm"
                onClick={onAccept}
              >
                <Check className="w-4 h-4" />
              </Button>
              <Button
                variant="error"
                size="sm"
                onClick={onDecline}
              >
                <X className="w-4 h-4" />
              </Button>
            </>
          )}
          {onView && (
            <Button variant="secondary" size="sm" onClick={onView}>
              Ver
            </Button>
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
          <div className="w-12 h-12 rounded-full bg-gradient-to-r from-blue-500 to-blue-600 flex items-center justify-center text-white">
            <Mail className="w-6 h-6" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-white">
              Invitación a Desafío
            </h3>
            <div className="flex items-center space-x-2 mt-1">
              <Badge variant={getStatusColor(invitation.status)}>
                {invitation.status}
              </Badge>
              {invitation.status === InvitationStatus.PENDING && invitation.expiresAt && (
                <span className="text-xs text-yellow-400">
                  {getTimeRemaining(invitation.expiresAt)}
                </span>
              )}
            </div>
          </div>
        </div>

        <div className="flex space-x-2">
          {onView && (
            <Button variant="secondary" size="sm" onClick={onView}>
              Ver Desafío
            </Button>
          )}
        </div>
      </div>

      <div className="mb-4 p-4 bg-gray-800/50 rounded-lg">
        <h4 className="text-lg font-medium text-white mb-2">
          {invitation.challengeTitle}
        </h4>
        {invitation.message && (
          <p className="text-gray-300 text-sm mb-3">
            {invitation.message}
          </p>
        )}
        <div className="flex items-center text-sm text-gray-400">
          <User className="w-4 h-4 mr-2" />
          <span>Invitado por <strong className="text-white">{invitation.inviterUsername}</strong></span>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Enviada
          </span>
          <p className="text-sm text-white mt-1">{formatDate(invitation.createdAt)}</p>
        </div>
        {invitation.expiresAt && (
          <div>
            <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
              Expira
            </span>
            <p className="text-sm text-white mt-1">{formatDate(invitation.expiresAt)}</p>
          </div>
        )}
        {invitation.respondedAt && (
          <div>
            <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
              Respondida
            </span>
            <p className="text-sm text-white mt-1">{formatDate(invitation.respondedAt)}</p>
          </div>
        )}
      </div>

      {showActions && invitation.status === InvitationStatus.PENDING && (
        <div className="pt-4 border-t border-gray-700">
          <div className="flex space-x-3">
            <Button
              variant="primary"
              onClick={onAccept}
              className="flex-1"
            >
              <Check className="w-4 h-4 mr-2" />
              Aceptar Invitación
            </Button>
            <Button
              variant="error"
              onClick={onDecline}
              className="flex-1"
            >
              <X className="w-4 h-4 mr-2" />
              Rechazar
            </Button>
          </div>
        </div>
      )}

      {invitation.status !== InvitationStatus.PENDING && (
        <div className="pt-4 border-t border-gray-700">
          <div className="flex items-center space-x-2">
            {getStatusIcon(invitation.status)}
            <span className="text-sm text-gray-400">
              {invitation.status === InvitationStatus.ACCEPTED && "Has aceptado esta invitación"}
              {invitation.status === InvitationStatus.DECLINED && "Has rechazado esta invitación"}
              {invitation.status === InvitationStatus.EXPIRED && "Esta invitación ha expirado"}
            </span>
          </div>
        </div>
      )}
    </Card>
  );
};

export default InvitationCard;
