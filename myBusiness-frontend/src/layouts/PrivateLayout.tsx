// src/layouts/PrivateLayout.tsx
import React from 'react'
import { Outlet } from 'react-router-dom'
import Navbar from '@/components/Navbar'

export default function PrivateLayout() {
  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Navbar />
      <main className="flex-1 p-4">
        <Outlet />
      </main>
    </div>
  )
}
