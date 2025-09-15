import React from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { Button } from "./ui/Button";
import { CoachResponseDto } from "../types/dtos";
import { CoachStatus } from "../types/enums";
import { Star, Calendar, Award, DollarSign, MessageCircle } from "lucide-react";

interface CoachProfileProps {
  coach: CoachResponseDto;
  onBookSession?: () => void;
  onMessage?: () => void;
  variant?: "compact" | "full";
}

const CoachProfile: React.FC<CoachProfileProps> = ({
  coach,
  onBookSession,
  onMessage,
  variant = "compact"
}) => {
  const getStatusBadge = (status: CoachStatus) => {
    const variants = {
      [CoachStatus.ACTIVE]: "success",
      [CoachStatus.INACTIVE]: "warning",
      [CoachStatus.SUSPENDED]: "error",
      [CoachStatus.PENDING_APPROVAL]: "secondary"
    } as const;

    return <Badge variant={variants[status]}>{status}</Badge>;
  };

  if (variant === "compact") {
    return (
      <Card className="bg-gray-800/50 border-gray-700 hover:border-blue-500 transition-all">
        <div className="p-4">
          <div className="flex items-start justify-between mb-3">
            <div className="flex items-center space-x-3">
              <div className="w-12 h-12 bg-gradient-to-r from-green-500 to-blue-600 rounded-full flex items-center justify-center">
                <Award className="w-6 h-6 text-white" />
              </div>
              <div>
                <h3 className="font-semibold text-white">
                  {coach.firstName} {coach.lastName}
                </h3>
                <p className="text-sm text-gray-400">@{coach.username}</p>
              </div>
            </div>
            {getStatusBadge(coach.status)}
          </div>

          <div className="space-y-2 mb-4">
            <div className="flex flex-wrap gap-1">
              {coach.specialties.slice(0, 2).map((specialty) => (
                <Badge key={specialty} variant="secondary" className="text-xs">
                  {specialty}
                </Badge>
              ))}
              {coach.specialties.length > 2 && (
                <Badge variant="secondary" className="text-xs">
                  +{coach.specialties.length - 2}
                </Badge>
              )}
            </div>

            <div className="flex items-center justify-between text-sm">
              <div className="flex items-center space-x-1">
                <Star className="w-4 h-4 text-yellow-500" />
                <span className="text-gray-300">{coach.rating.toFixed(1)}</span>
              </div>
              {coach.hourlyRate && (
                <div className="flex items-center space-x-1">
                  <DollarSign className="w-4 h-4 text-green-500" />
                  <span className="text-gray-300">${coach.hourlyRate}</span>
                </div>
              )}
            </div>
          </div>

          <div className="flex space-x-2">
            {onMessage && (
              <Button
                variant="secondary"
                size="sm"
                onClick={onMessage}
                className="flex-1"
              >
                <MessageCircle className="w-4 h-4 mr-1" />
                Mensaje
              </Button>
            )}
            {onBookSession && (
              <Button
                variant="primary"
                size="sm"
                onClick={onBookSession}
                className="flex-1"
                disabled={coach.status !== CoachStatus.ACTIVE}
              >
                <Calendar className="w-4 h-4 mr-1" />
                Reservar
              </Button>
            )}
          </div>
        </div>
      </Card>
    );
  }

  // Full variant for detailed view
  return (
    <Card className="bg-gray-800/50 border-gray-700">
      <div className="p-6">
        <div className="flex items-start justify-between mb-6">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-blue-600 rounded-full flex items-center justify-center">
              <Award className="w-8 h-8 text-white" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-white">
                {coach.firstName} {coach.lastName}
              </h2>
              <p className="text-gray-400">@{coach.username}</p>
              <div className="flex items-center space-x-2 mt-1">
                <Star className="w-4 h-4 text-yellow-500" />
                <span className="text-gray-300">{coach.rating.toFixed(1)}</span>
                <span className="text-gray-500">({coach.reviewCount} reseñas)</span>
              </div>
            </div>
          </div>
          {getStatusBadge(coach.status)}
        </div>

        {coach.bio && (
          <div className="mb-6">
            <h3 className="text-lg font-semibold text-white mb-2">Acerca de</h3>
            <p className="text-gray-300">{coach.bio}</p>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
          <div>
            <h4 className="font-semibold text-white mb-2">Especialidades</h4>
            <div className="flex flex-wrap gap-2">
              {coach.specialties.map((specialty) => (
                <Badge key={specialty} variant="secondary">
                  {specialty}
                </Badge>
              ))}
            </div>
          </div>

          <div>
            <h4 className="font-semibold text-white mb-2">Estadísticas</h4>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-400">Clientes:</span>
                <span className="text-white">{coach.clientCount}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Experiencia:</span>
                <span className="text-white">{coach.yearsExperience || 'N/A'} años</span>
              </div>
              {coach.hourlyRate && (
                <div className="flex justify-between">
                  <span className="text-gray-400">Tarifa:</span>
                  <span className="text-white">${coach.hourlyRate}/{coach.currency}</span>
                </div>
              )}
            </div>
          </div>
        </div>

        {coach.certifications.length > 0 && (
          <div className="mb-6">
            <h4 className="font-semibold text-white mb-2">Certificaciones</h4>
            <div className="flex flex-wrap gap-2">
              {coach.certifications.map((cert) => (
                <Badge key={cert} variant="info">
                  {cert}
                </Badge>
              ))}
            </div>
          </div>
        )}

        <div className="flex space-x-4">
          {onMessage && (
            <Button
              variant="secondary"
              onClick={onMessage}
              className="flex-1"
            >
              <MessageCircle className="w-5 h-5 mr-2" />
              Enviar Mensaje
            </Button>
          )}
          {onBookSession && (
            <Button
              variant="primary"
              onClick={onBookSession}
              className="flex-1"
              disabled={coach.status !== CoachStatus.ACTIVE}
            >
              <Calendar className="w-5 h-5 mr-2" />
              Reservar Sesión
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
};

export default CoachProfile;
