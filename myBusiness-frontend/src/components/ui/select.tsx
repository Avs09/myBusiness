import React, { forwardRef } from 'react'

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  children: React.ReactNode
  className?: string
}

const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ children, className = '', ...props }, ref) => (
    <select
      {...props}
      ref={ref}
      className={`w-full h-10 border border-gray-300 rounded px-2 focus:outline-none focus:ring-2 focus:ring-blue-500 ${
        className
      }`}
    >
      {children}
    </select>
  )
)

Select.displayName = 'Select'

export default Select
