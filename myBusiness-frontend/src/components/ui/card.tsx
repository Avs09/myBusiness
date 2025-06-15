// src/components/ui/card.tsx
import React from 'react';

interface CardProps {
  className?: string;
}

export const Card: React.FC<React.PropsWithChildren<CardProps>> = ({
  className = '',
  children,
}) => (
  <div className={`bg-white rounded-2xl shadow-lg ${className}`}>
    {children}
  </div>
);

export const CardContent: React.FC<React.PropsWithChildren<CardProps>> = ({
  className = '',
  children,
}) => (
  <div className={`p-6 ${className}`}>
    {children}
  </div>
);
