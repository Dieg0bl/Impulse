import React from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { Button } from "./ui/Button";
import { NotificationResponseDto } from "../types/dtos";
import { NotificationType } from "../types/enums";
import {
  Bell,
  Info,
  AlertTriangle,
  CheckCircle,
  Gift,
  Users,
  Trophy,
  Clock,
  X
} from "lucide-react";

interface LegacyNotificationAction {
  label: string;
  onClick: () => void;
}

interface NotificationCardProps {
  // Preferred shape
  notification?: NotificationResponseDto;
  // Legacy shape used in some pages
  type?: string;
  title?: string;
  message?: string;
  action?: LegacyNotificationAction;
  onMarkAsRead?: () => void;
  onDismiss?: () => void;
  variant?: "compact" | "full";
}

const NotificationCard: React.FC<NotificationCardProps> = ({
  notification: notificationProp,
  // legacy props
  type: legacyType,
  title: legacyTitle,
  message: legacyMessage,
  action: legacyAction,
  onMarkAsRead,
  onDismiss,
  variant = "compact"
}) => {
  const notification: NotificationResponseDto = notificationProp || (({
    id: 0,
    uuid: 'legacy',
    userId: 0,
    title: legacyTitle || '',
    message: legacyMessage || '',
    type: (legacyType as any) || NotificationType.SYSTEM_ALERT,
    isRead: false,
    createdAt: new Date().toISOString()
  } as unknown) as NotificationResponseDto);
  const getTypeIcon = (type: NotificationType) => {
    const icons: Record<NotificationType, JSX.Element> = {
      [NotificationType.CHALLENGE_CREATED]: <Users className="w-5 h-5" />,
      [NotificationType.CHALLENGE_STARTED]: <Bell className="w-5 h-5" />,
      [NotificationType.CHALLENGE_COMPLETED]: <Trophy className="w-5 h-5" />,
      [NotificationType.EVIDENCE_SUBMITTED]: <CheckCircle className="w-5 h-5" />,
      [NotificationType.EVIDENCE_VALIDATED]: <CheckCircle className="w-5 h-5" />,
      [NotificationType.VALIDATION_REQUESTED]: <Info className="w-5 h-5" />,
      [NotificationType.VALIDATION_COMPLETED]: <CheckCircle className="w-5 h-5" />,
      [NotificationType.ACHIEVEMENT_UNLOCKED]: <Gift className="w-5 h-5" />,
      [NotificationType.REMINDER]: <Clock className="w-5 h-5" />,
      [NotificationType.SYSTEM_ALERT]: <AlertTriangle className="w-5 h-5" />
    };

    return icons[type] || <Bell className="w-5 h-5" />;
  };

  const getTypeColor = (type: NotificationType) => {
    const colors: Record<NotificationType, string> = {
      [NotificationType.CHALLENGE_CREATED]: "from-blue-500 to-blue-600",
      [NotificationType.CHALLENGE_STARTED]: "from-green-500 to-green-600",
      [NotificationType.CHALLENGE_COMPLETED]: "from-purple-500 to-purple-600",
      [NotificationType.EVIDENCE_SUBMITTED]: "from-blue-500 to-blue-600",
      [NotificationType.EVIDENCE_VALIDATED]: "from-green-500 to-green-600",
      [NotificationType.VALIDATION_REQUESTED]: "from-yellow-500 to-yellow-600",
      [NotificationType.VALIDATION_COMPLETED]: "from-green-500 to-green-600",
      [NotificationType.ACHIEVEMENT_UNLOCKED]: "from-orange-500 to-orange-600",
      [NotificationType.REMINDER]: "from-yellow-500 to-yellow-600",
      [NotificationType.SYSTEM_ALERT]: "from-red-500 to-red-600"
    };

    return colors[type] || "from-gray-500 to-gray-600";
  };

  const formatDate = (dateString: string) => {
    const now = new Date();
    const date = new Date(dateString);
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffHours = Math.ceil(diffTime / (1000 * 60 * 60));

    if (diffHours < 1) {
      return "Hace un momento";
    } else if (diffHours < 24) {
      return `Hace ${diffHours}h`;
    } else {
      const diffDays = Math.ceil(diffHours / 24);
      return `Hace ${diffDays}d`;
    }
  };

  if (variant === "compact") {
    return (
      <div className={`flex items-start space-x-3 p-3 rounded-lg transition-colors ${
        notification.isRead ? "bg-gray-800/30" : "bg-gray-800/50 border border-gray-700"
      }`}>
        <div className={`w-10 h-10 rounded-full bg-gradient-to-r ${getTypeColor(notification.type)} flex items-center justify-center text-white flex-shrink-0`}>
          {getTypeIcon(notification.type)}
        </div>

        <div className="flex-1 min-w-0">
          <p className={`text-sm ${notification.isRead ? "text-gray-400" : "text-white font-medium"}`}>
            {notification.title}
          </p>
          <p className={`text-xs ${notification.isRead ? "text-gray-500" : "text-gray-300"} truncate`}>
            {notification.message}
          </p>
          <span className="text-xs text-gray-500">{formatDate(notification.createdAt)}</span>
        </div>

        <div className="flex items-center space-x-1">
          {!notification.isRead && onMarkAsRead && (
            <Button
              variant="secondary"
              size="sm"
              onClick={onMarkAsRead}
              className="text-xs"
            >
              Marcar leída
            </Button>
          )}
          {onDismiss && (
            <Button
              variant="secondary"
              size="sm"
              onClick={onDismiss}
            >
              <X className="w-3 h-3" />
            </Button>
          )}
        </div>
      </div>
    );
  }

  // Full variant
  return (
    <Card className={`${notification.isRead ? "bg-gray-800/30" : "bg-gray-800/50 border-blue-500/30"}`}>
      <div className="p-6">
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center space-x-3">
            <div className={`w-12 h-12 rounded-full bg-gradient-to-r ${getTypeColor(notification.type)} flex items-center justify-center text-white`}>
              {getTypeIcon(notification.type)}
            </div>
            <div>
              <h3 className={`text-lg font-semibold ${notification.isRead ? "text-gray-300" : "text-white"}`}>
                {notification.title}
              </h3>
              <div className="flex items-center space-x-2 mt-1">
                <Badge variant="secondary" className="text-xs">
                  {notification.type.replace(/_/g, ' ')}
                </Badge>
                <span className="text-xs text-gray-500 flex items-center">
                  <Clock className="w-3 h-3 mr-1" />
                  {formatDate(notification.createdAt)}
                </span>
              </div>
            </div>
          </div>

          <div className="flex space-x-2">
            {!notification.isRead && onMarkAsRead && (
              <Button
                variant="primary"
                size="sm"
                onClick={onMarkAsRead}
              >
                Marcar como leída
              </Button>
            )}
            {onDismiss && (
              <Button
                variant="secondary"
                size="sm"
                onClick={onDismiss}
              >
                <X className="w-4 h-4" />
              </Button>
            )}
          </div>
        </div>

        <div className="mb-4">
          <p className={`${notification.isRead ? "text-gray-400" : "text-gray-300"}`}>
            {notification.message}
          </p>
        </div>

        {notification.data?.actionUrl && (
          <div className="pt-4 border-t border-gray-700">
            <Button variant="primary" size="sm">
              Ver detalles
            </Button>
          </div>
        )}

        {notification.readAt && (
          <div className="mt-4 pt-3 border-t border-gray-700">
            <span className="text-xs text-gray-500">
              Leída el {new Date(notification.readAt).toLocaleDateString('es-ES', {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
              })}
            </span>
          </div>
        )}
      </div>
    </Card>
  );
};

export { NotificationCard };
export default NotificationCard;
