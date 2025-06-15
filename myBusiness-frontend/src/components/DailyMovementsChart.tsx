import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import {
  fetchDailyMovementTrend,
  DailyMovementCountDto,
} from '@/api/movements'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'

export default function DailyMovementsChart() {
  const { getAuthHeader } = useAuth()
  const [data, setData] = useState<DailyMovementCountDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const headers = getAuthHeader()
    fetchDailyMovementTrend(7, headers)
      .then((arr) => {
    
        setData(arr)
      })
      .catch((err: any) => {
        console.error('Error cargando tendencia diaria:', err)
        toast.error(err.response?.data?.message || 'No se pudo cargar tendencia diaria.')
      })
      .finally(() => setLoading(false))

  }, [])

  if (loading) {
    return <p className="text-gray-600">Cargando gráfica de movimientos...</p>
  }
  if (data.length === 0) {
    return <p className="text-gray-600">No hay datos de movimientos en los últimos días.</p>
  }

  return (
    <div className="w-full h-64 mt-4 bg-white shadow rounded-lg p-4">
      <h3 className="text-lg font-semibold mb-2">Movimientos Diarios (últimos 7 días)</h3>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" tick={{ fontSize: 12 }} />
          <YAxis allowDecimals={false} />
          <Tooltip
            formatter={(value: any) => [value, 'Movimientos']}
            labelFormatter={(label: any) => `Fecha: ${label}`}
          />
          <Line
            type="monotone"
            dataKey="count"
            stroke="#3b82f6"        
            strokeWidth={2}
            dot={{ r: 4 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}
