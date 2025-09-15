import React, { useState } from "react";
import { Card } from "./ui/Card";
import { Badge } from "./ui/Badge";
import { Button } from "./ui/Button";
import { RatingResponseDto } from "../types/dtos";
import { RatingType } from "../types/enums";
import {
  Star,
  User,
  Target,
  FileText,
  MessageSquare,
  Calendar,
  Heart
} from "lucide-react";

interface RatingCardProps {
  rating: RatingResponseDto;
  onLike?: () => void;
  onReply?: () => void;
  variant?: "compact" | "full";
  showActions?: boolean;
}

const RatingCard: React.FC<RatingCardProps> = ({
  rating,
  onLike,
  onReply,
  variant = "compact",
  showActions = true
}) => {
  const [showFullComment, setShowFullComment] = useState(false);

  const getTypeIcon = (type: RatingType) => {
    const icons = {
      [RatingType.CHALLENGE]: <Target className="w-4 h-4" />,
      [RatingType.COACH]: <User className="w-4 h-4" />,
      [RatingType.USER]: <User className="w-4 h-4" />,
      [RatingType.CONTENT]: <FileText className="w-4 h-4" />
    };

    return icons[type] || <Star className="w-4 h-4" />;
  };

  const getTypeColor = (type: RatingType) => {
    const colors = {
      [RatingType.CHALLENGE]: "from-blue-500 to-blue-600",
      [RatingType.COACH]: "from-green-500 to-green-600",
      [RatingType.USER]: "from-purple-500 to-purple-600",
      [RatingType.CONTENT]: "from-orange-500 to-orange-600"
    };

    return colors[type] || "from-gray-500 to-gray-600";
  };

  const renderStars = (score: number, size: "sm" | "md" | "lg" = "sm") => {
    let starSize: string;
    if (size === "sm") {
      starSize = "w-3 h-3";
    } else if (size === "md") {
      starSize = "w-4 h-4";
    } else {
      starSize = "w-5 h-5";
    }

    return (
      <div className="flex items-center space-x-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`${starSize} ${
              star <= score
                ? "text-yellow-400 fill-current"
                : "text-gray-600"
            }`}
          />
        ))}
        <span className={`ml-2 ${size === "sm" ? "text-xs" : "text-sm"} font-medium text-white`}>
          {score.toFixed(1)}
        </span>
      </div>
    );
  };

  const formatDate = (dateString: string) => {
    const now = new Date();
    const date = new Date(dateString);
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 1) return "Hace 1 día";
    if (diffDays < 7) return `Hace ${diffDays} días`;

    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const truncateText = (text: string, maxLength: number) => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + "...";
  };

  if (variant === "compact") {
    return (
      <div className="flex items-start space-x-3 p-4 bg-gray-800/30 rounded-lg border border-gray-700 hover:border-gray-600 transition-colors">
        <div className={`w-10 h-10 rounded-full bg-gradient-to-r ${getTypeColor(rating.targetType)} flex items-center justify-center text-white flex-shrink-0`}>
          {getTypeIcon(rating.targetType)}
        </div>

        <div className="flex-1 min-w-0">
          <div className="flex items-center space-x-2 mb-1">
            <p className="text-sm font-medium text-white">
              {rating.username}
            </p>
            <Badge variant="secondary" size="sm">
              {rating.targetType}
            </Badge>
            <span className="text-xs text-gray-500">{formatDate(rating.createdAt)}</span>
          </div>

          <div className="mb-2">
            {renderStars(rating.rating, "sm")}
          </div>

          {rating.review && (
            <p className="text-sm text-gray-300">
              {truncateText(rating.review, 100)}
              {rating.review.length > 100 && (
                <button
                  onClick={() => setShowFullComment(!showFullComment)}
                  className="ml-1 text-blue-400 hover:text-blue-300 text-xs"
                >
                  {showFullComment ? "ver menos" : "ver más"}
                </button>
              )}
            </p>
          )}
        </div>

        {showActions && (
          <div className="flex items-center space-x-2">
            {onLike && (
              <Button variant="secondary" size="sm" onClick={onLike}>
                <Heart className="w-4 h-4" />
              </Button>
            )}
            {onReply && (
              <Button variant="secondary" size="sm" onClick={onReply}>
                <MessageSquare className="w-4 h-4" />
              </Button>
            )}
          </div>
        )}
      </div>
    );
  }

  // Full variant
  return (
    <Card className="p-6">
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center space-x-3">
          <div className={`w-12 h-12 rounded-full bg-gradient-to-r ${getTypeColor(rating.targetType)} flex items-center justify-center text-white`}>
            {getTypeIcon(rating.targetType)}
          </div>
          <div>
            <h3 className="text-lg font-semibold text-white">
              {rating.username}
            </h3>
            <div className="flex items-center space-x-2 mt-1">
              <Badge variant="secondary">
                {rating.targetType}
              </Badge>
              <span className="text-xs text-gray-400 flex items-center">
                <Calendar className="w-3 h-3 mr-1" />
                {formatDate(rating.createdAt)}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div className="mb-4">
        <div className="flex items-center justify-between mb-3">
          <span className="text-sm font-medium text-gray-400">Calificación</span>
          {renderStars(rating.rating, "lg")}
        </div>
      </div>

      {rating.review && (
        <div className="mb-4 p-4 bg-gray-800/50 rounded-lg">
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Comentario
          </span>
          <p className="text-gray-300 mt-2 leading-relaxed">
            {showFullComment ? rating.review : truncateText(rating.review, 200)}
            {rating.review.length > 200 && (
              <button
                onClick={() => setShowFullComment(!showFullComment)}
                className="ml-2 text-blue-400 hover:text-blue-300 text-sm font-medium"
              >
                {showFullComment ? "Ver menos" : "Ver más"}
              </button>
            )}
          </p>
        </div>
      )}

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Entidad Calificada
          </span>
          <p className="text-sm text-white mt-1">ID: {rating.targetId}</p>
        </div>
        <div>
          <span className="text-xs font-medium text-gray-400 uppercase tracking-wider">
            Tipo
          </span>
          <p className="text-sm text-white mt-1">{rating.targetType}</p>
        </div>
      </div>

      {showActions && (
        <div className="pt-4 border-t border-gray-700">
          <div className="flex space-x-3">
            {onLike && (
              <Button variant="secondary" onClick={onLike}>
                <Heart className="w-4 h-4 mr-2" />
                Me gusta
              </Button>
            )}
            {onReply && (
              <Button variant="secondary" onClick={onReply}>
                <MessageSquare className="w-4 h-4 mr-2" />
                Responder
              </Button>
            )}
          </div>
        </div>
      )}

      {rating.updatedAt && rating.updatedAt !== rating.createdAt && (
        <div className="mt-4 pt-3 border-t border-gray-700">
          <span className="text-xs text-gray-500">
            Editado el {new Date(rating.updatedAt).toLocaleDateString('es-ES', {
              year: 'numeric',
              month: 'short',
              day: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            })}
          </span>
        </div>
      )}
    </Card>
  );
};

export default RatingCard;
