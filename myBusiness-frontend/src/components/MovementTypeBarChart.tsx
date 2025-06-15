import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import {
  fetchMovementTypeCounts,
  MovementTypeCountDto,
} from '@/api/movements'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'

export default function MovementTypeBarChart() {
  const { getAuthHeader } = useAuth()
  const [data, setData] = useState<MovementTypeCountDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const headers = getAuthHeader()
    fetchMovementTypeCounts(7, headers)
      .then((arr) => {
        setData(arr)
      })
      .catch((err: any) => {
        console.error('Error cargando conteo por tipo:', err)
        toast.error(err.response?.data?.message || 'No se pudo cargar tipo de movimientos.')
      })
      .finally(() => setLoading(false))

  }, [])

  if (loading) {
    return <p className="text-gray-600">Cargando gráfico por tipo...</p>
  }
  if (data.length === 0) {
    return <p className="text-gray-600">No hay movimientos en los últimos días.</p>
  }

  return (
    <div className="w-full h-64 mt-4 bg-white shadow rounded-lg p-4">
      <h3 className="text-lg font-semibold mb-2">Movimientos por Tipo (últimos 7 días)</h3>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="movementType" tick={{ fontSize: 12 }} />
          <YAxis allowDecimals={false} />
          <Tooltip
            formatter={(value: any) => [value, 'Cantidad']}
            labelFormatter={(label: any) => `Tipo: ${label}`}
          />
          <Bar dataKey="count" fill="#10b981" /> {/* verde */}
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
