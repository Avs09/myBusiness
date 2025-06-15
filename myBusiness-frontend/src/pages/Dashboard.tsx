// src/pages/Dashboard.tsx
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'

import { fetchDashboardMetrics } from '@/api/dashboard'
import type { DashboardMetricsDto } from '@/schemas/dashboard'

import {
  fetchMovementsLast24hCount,
} from '@/api/movements'

import KpiTile from '@/components/KpiTile'
import OpenAlertsTable from '@/components/OpenAlertsTable'
import RecentMovementsTable from '@/components/RecentMovementsTable'
import DailyMovementsChart from '@/components/DailyMovementsChart'
import MovementTypeBarChart from '@/components/MovementTypeBarChart'
import StockEvolutionChart from '@/components/StockEvolutionChart'
import TopProductsList from '@/components/TopProductsList'
import CriticalStockAlertsTable from '@/components/CriticalStockAlertsTable'
import CategorySummaryCards from '@/components/CategorySummaryCards'

import {
  Box as BoxIcon,
  DollarSign as DollarIcon,
  AlertCircle as AlertIcon,
  TrendingUp as TrendIcon,
} from 'lucide-react'

export default function Dashboard() {
  const { getAuthHeader } = useAuth()

  const [metrics, setMetrics] = useState<DashboardMetricsDto | null>(null)
  const [loading, setLoading] = useState(true)

  const [mov24h, setMov24h] = useState<number | null>(null)
  const [loading24h, setLoading24h] = useState(true)

  useEffect(() => {
    const rawHeaders = getAuthHeader()
    const headers = rawHeaders as Record<string, string>

    setLoading(true)
    fetchDashboardMetrics(headers)
      .then((data) => {
        setMetrics(data)
      })
      .catch((err: any) => {
        console.error('Error cargando métricas:', err)
        toast.error(err.response?.data?.message || 'No se pudieron cargar métricas.')
      })
      .finally(() => {
        setLoading(false)
      })

    setLoading24h(true)
    fetchMovementsLast24hCount(headers)
      .then((count) => {
        setMov24h(count)
      })
      .catch((err) => {
        console.error('Error calculando mov 24h:', err)
        toast.error('No se pudo calcular movimientos últimas 24h')
        setMov24h(null)
      })
      .finally(() => setLoading24h(false))
    
  }, [])

  if (loading) {
    return <p className="text-center mt-8">Cargando dashboard...</p>
  }
  if (!metrics) {
    return (
      <p className="text-center mt-8 text-red-500">
        Error al cargar métricas.
      </p>
    )
  }

  // Formatear valor total inventario como COP
  const formatoCOP = (valueStr: string) => {
    let num = Number(valueStr)
    if (isNaN(num)) return valueStr
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0,
    }).format(num)
  }

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-3xl font-bold mb-4">Dashboard</h1>

      <section>
        <h2 className="text-2xl font-semibold mb-2">Inventario por Categoría</h2>
        <CategorySummaryCards />
      </section>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiTile
          label="Total de productos"
          value={metrics.totalProducts.toString()}
          icon={<BoxIcon />}
          colorBg="bg-blue-50"
        />
        <KpiTile
          label="Valor total inventario"
          value={formatoCOP(metrics.totalInventoryValue)}
          icon={<DollarIcon />}
          colorBg="bg-green-50"
        />
        <KpiTile
          label="Alertas abiertas"
          value={metrics.totalOpenAlerts.toString()}
          icon={<AlertIcon />}
          colorBg="bg-red-50"
        />
        <KpiTile
          label="Movimientos últimos 7 días"
          value={metrics.movementsLast7Days.toString()}
          icon={<TrendIcon />}
          colorBg="bg-yellow-50"
        />
        <KpiTile
          label="Movimientos últimas 24h"
          value={loading24h
            ? '...'
            : mov24h !== null
              ? mov24h.toString()
              : '-'
          }
          icon={<TrendIcon />}
          colorBg="bg-purple-50"
        />
      </div>

      <section className="mt-8">
        <h2 className="text-2xl font-semibold mb-2">Alertas Abiertas</h2>
        <OpenAlertsTable />
      </section>

      <section className="mt-8">
        <h2 className="text-2xl font-semibold mb-2">Alertas de stock crítico</h2>
        <CriticalStockAlertsTable />
      </section>

      <section>
        <h2 className="text-xl font-semibold mb-2">Movimientos Recientes</h2>
        <RecentMovementsTable />
      </section>

      <section>
        <DailyMovementsChart />
      </section>

      <section>
        <MovementTypeBarChart />
      </section>

      <section>
        <StockEvolutionChart />
      </section>

      <section>
        <TopProductsList />
      </section>
    </div>
  )
}
