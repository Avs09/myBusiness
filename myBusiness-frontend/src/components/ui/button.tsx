// src/components/ui/button.tsx
import React from 'react'

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  /**
   * Variedad del botón:
   * - primary: azul
   * - secondary: verde
   * - destructive: rojo
   */
  variant?: 'primary' | 'secondary' | 'destructive'
  /**
   * Tamaño del botón:
   * - sm: pequeño
   * - md: mediano (por defecto)
   * - lg: grande
   */
  size?: 'sm' | 'md' | 'lg'
  className?: string
}

const Button: React.FC<React.PropsWithChildren<ButtonProps>> = ({
  variant = 'primary',
  size = 'md',
  className = '',
  children,
  ...props
}) => {
  const base = 'rounded font-medium transition focus:outline-none focus:ring-2 focus:ring-offset-2'
  // Variantes de color
  const variantStyles = {
    primary: 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500',
    secondary: 'bg-green-500 text-white hover:bg-green-600 focus:ring-green-400',
    destructive: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
  }
  // Tamaños
  const sizeStyles = {
    sm: 'px-2 py-1 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  }

  return (
    <button
      className={`${base} ${variantStyles[variant]} ${sizeStyles[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}

export default Button
