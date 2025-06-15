// src/layouts/PublicLayout.tsx
import React from 'react'
import { Outlet } from 'react-router-dom'

export default function PublicLayout() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-white">
      <Outlet />
    </div>
  )
}
