// src/App.tsx
import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { useAlertPolling } from '@/hooks/useAlertPolling'
import LandingPage from '@/pages/LandingPage'
import Products from '@/pages/Products'
import Reports from '@/pages/Reports'
import Dashboard from '@/pages/Dashboard'
import Movements from '@/pages/Movements'
import AlertsPage from '@/pages/AlertsPage'
import ProtectedRoute from '@/components/ProtectedRoute'
import PublicLayout from '@/layouts/PublicLayout'
import PrivateLayout from '@/layouts/PrivateLayout'

export default function App() {
  // Reducir intervalo de polling a 5s para minimizar retraso en alertas
  useAlertPolling(5000)

  return (
    <>
      <Toaster position="top-right" toastOptions={{
        success: { duration: 10000 }, // aumentar duración de notificaciones
        error: { duration: 10000 },
      }} />
      <Routes>
        <Route path="/" element={<PublicLayout />}>
          <Route index element={<LandingPage />} />
        </Route>

        <Route
          element={
            <ProtectedRoute>
              <PrivateLayout />
            </ProtectedRoute>
          }
        >
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="products" element={<Products />} />
          <Route path="reports" element={<Reports />} />
          <Route path="movements" element={<Movements />} />
          <Route path="alerts" element={<AlertsPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}
