// src/components/TopProductsList.tsx
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import { fetchTopProducts, TopProductDto } from '@/api/movements'

export default function TopProductsList() {
  const { getAuthHeader } = useAuth()
  const [data, setData] = useState<TopProductDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const headers = getAuthHeader()
    fetchTopProducts(30, 5)
      .then((arr) => setData(arr))
      .catch((err: any) => {
        console.error('Error cargando top productos:', err)
        toast.error(err.response?.data?.message || 'No se pudo cargar top productos.')
      })
      .finally(() => setLoading(false))

  }, [])

  if (loading) {
    return <p className="text-gray-600 mt-4">Cargando top productos...</p>
  }
  if (data.length === 0) {
    return <p className="text-gray-600 mt-4">No hay datos de top productos.</p>
  }

  return (
    <div className="w-full bg-white shadow rounded-lg p-4 mt-4">
      <h3 className="text-lg font-semibold mb-2">Top 5 Productos (último mes)</h3>
      <ol className="list-decimal list-inside space-y-1">
        {data.map((p) => (
          <li key={p.productId} className="flex justify-between">
            <span>{p.productName}</span>
            <span className="font-semibold">{p.totalOut}</span>
          </li>
        ))}
      </ol>
    </div>
  )
}
