import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAdaptiveNavigation } from '../contexts/AdaptiveNavigationContext';
import { useAuth } from '../hooks/useAuth';
import './SmartRecommendations.css';

interface SmartRecommendationsProps {
  maxRecommendations?: number;
  showCategories?: boolean;
  compact?: boolean;
  autoHide?: boolean;
}

const SmartRecommendations: React.FC<SmartRecommendationsProps> = ({
  maxRecommendations = 5,
  showCategories = true,
  compact = false,
  autoHide = false
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();
  const { recommendations, isRouteRecommended, getOptimalNextRoute } = useAdaptiveNavigation();
  
  const [isVisible, setIsVisible] = useState(!autoHide);
  const [dismissed, setDismissed] = useState<string[]>([]);

  // Auto-hide logic
  useEffect(() => {
    if (autoHide) {
      const timer = setTimeout(() => {
        setIsVisible(false);
      }, 10000); // Hide after 10 seconds

      return () => clearTimeout(timer);
    }
  }, [autoHide, location.pathname]);

  // Filter and limit recommendations
  const visibleRecommendations = recommendations
    .filter(rec => !dismissed.includes(rec.path))
    .filter(rec => rec.path !== location.pathname) // Don't recommend current page
    .slice(0, maxRecommendations);

  const handleRecommendationClick = (path: string) => {
    navigate(path);
    if (autoHide) {
      setIsVisible(false);
    }
  };

  const handleDismiss = (path: string, event: React.MouseEvent) => {
    event.stopPropagation();
    setDismissed(prev => [...prev, path]);
  };

  const handleDismissAll = () => {
    setIsVisible(false);
  };

  const getCategoryIcon = (category: string): string => {
    const icons = {
      'frecuent': '🔥',
      'role-based': '👤',
      'contextual': '🕐',
      'trending': '📈'
    };
    return icons[category as keyof typeof icons] || '💡';
  };

  const getCategoryLabel = (category: string): string => {
    const labels = {
      'frecuent': 'Frecuentes',
      'role-based': 'Para tu rol',
      'contextual': 'Contextual',
      'trending': 'Tendencia'
    };
    return labels[category as keyof typeof labels] || 'Recomendado';
  };

  if (!user || !isVisible || visibleRecommendations.length === 0) {
    return null;
  }

  return (
    <div className={`smart-recommendations ${compact ? 'compact' : ''}`}>
      <div className="recommendations-header">
        <h3>
          <span className="icon">🎯</span>
          Recomendado para ti
        </h3>
        {!compact && (
          <button 
            className="dismiss-all-btn"
            onClick={handleDismissAll}
            aria-label="Cerrar recomendaciones"
          >
            ✕
          </button>
        )}
      </div>

      <div className="recommendations-list">
        {visibleRecommendations.map((recommendation, index) => (
          <div
            key={recommendation.path}
            className={`recommendation-item priority-${Math.min(recommendation.priority, 10)}`}
            onClick={() => handleRecommendationClick(recommendation.path)}
            role="button"
            tabIndex={0}
            onKeyPress={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                handleRecommendationClick(recommendation.path);
              }
            }}
          >
            <div className="recommendation-content">
              <div className="recommendation-title">
                {showCategories && (
                  <span 
                    className={`category-badge ${recommendation.category}`}
                    title={getCategoryLabel(recommendation.category)}
                  >
                    {getCategoryIcon(recommendation.category)}
                  </span>
                )}
                <span className="title">{recommendation.title}</span>
              </div>
              
              {!compact && (
                <div className="recommendation-reason">
                  {recommendation.reason}
                </div>
              )}
            </div>

            <div className="recommendation-actions">
              <span className="priority-indicator">
                {recommendation.priority > 7 && '🌟'}
              </span>
              <button
                className="dismiss-btn"
                onClick={(e) => handleDismiss(recommendation.path, e)}
                aria-label="Descartar recomendación"
                title="No mostrar esta recomendación"
              >
                ✕
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Quick action for optimal next route */}
      {!compact && (
        <div className="quick-action">
          <button
            className="optimal-route-btn"
            onClick={() => {
              const optimalRoute = getOptimalNextRoute();
              if (optimalRoute) {
                navigate(optimalRoute);
              }
            }}
            disabled={!getOptimalNextRoute()}
          >
            <span className="icon">⚡</span>
            Ir a la mejor opción
          </button>
        </div>
      )}
    </div>
  );
};

export default SmartRecommendations;
