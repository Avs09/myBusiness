// src/components/KpiTile.tsx
import React from 'react'

interface KpiTileProps {
  label: string
  value: string | number
  icon?: React.ReactNode
  colorBg?: string  
}

export default function KpiTile({
  label,
  value,
  icon,
  colorBg = 'bg-white',
}: KpiTileProps) {
  return (
    <div className={`flex items-center p-4 rounded-lg shadow ${colorBg}`}>
      {icon && <div className="mr-4 text-3xl text-gray-600">{icon}</div>}
      <div>
        <p className="text-sm text-gray-500">{label}</p>
        <p className="text-2xl font-semibold text-gray-800">{value}</p>
      </div>
    </div>
  )
}
