// src/components/StockEvolutionChart.tsx
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import { fetchStockEvolution, StockEvolutionDto } from '@/api/movements'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'

export default function StockEvolutionChart() {
  const { getAuthHeader } = useAuth()
  const [data, setData] = useState<StockEvolutionDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const headers = getAuthHeader()
    fetchStockEvolution(30, headers)
      .then((arr) => {
        setData(arr)
      })
      .catch((err: any) => {
        console.error('Error cargando evolución de stock:', err)
        toast.error(err.response?.data?.message || 'No se pudo cargar evolución de stock.')
      })
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return <p className="text-gray-600 mt-4">Cargando evolución de stock...</p>
  }
  if (data.length === 0) {
    return <p className="text-gray-600 mt-4">No hay datos de stock para mostrar.</p>
  }

  return (
    <div className="w-full h-64 mt-4 bg-white shadow rounded-lg p-4">
      <h3 className="text-lg font-semibold mb-2">Evolución de Inventario (últimos 30 días)</h3>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" tick={{ fontSize: 12 }} />
          <YAxis allowDecimals={false} />
          <Tooltip
            formatter={(value: any) => [value, 'Total Stock']}
            labelFormatter={(label: any) => `Fecha: ${label}`}
          />
          <Line
            type="monotone"
            dataKey="totalStock"
            stroke="#2563eb"       
            strokeWidth={2}
            dot={{ r: 3 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}
