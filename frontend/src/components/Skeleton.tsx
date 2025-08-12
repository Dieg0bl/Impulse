import React from 'react';

interface SkeletonProps {
  width?: string | number;
  height?: string | number;
  circle?: boolean;
  className?: string;
}

const Skeleton: React.FC<SkeletonProps> = ({ width = '100%', height = 20, circle = false, className = '' }) => {
  const style: React.CSSProperties = {
    width,
    height,
    borderRadius: circle ? '50%' : 4,
    background: 'linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%)',
    backgroundSize: '200% 100%',
    animation: 'skeleton-loading 1.2s infinite linear',
    display: 'inline-block',
  };
  return <span className={`skeleton ${className}`} style={style} />;
};

export default Skeleton;
