// src/components/CategorySummaryCards.tsx
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import { fetchCategorySummaries, CategorySummaryDto } from '@/api/categories'

export default function CategorySummaryCards() {
  const { getAuthHeader } = useAuth()
  const [data, setData] = useState<CategorySummaryDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const headers = getAuthHeader()
    fetchCategorySummaries(headers)
      .then((arr) => setData(arr))
      .catch((err: any) => {
        console.error('Error cargando resumen por categorías:', err)
        toast.error(err.response?.data?.message || 'No se pudo cargar resumen por categorías.')
      })
      .finally(() => setLoading(false))
 
  }, [])

  if (loading) {
    return <p className="text-gray-600 mt-4">Cargando resumen de categorías...</p>
  }
  if (data.length === 0) {
    return <p className="text-gray-600 mt-4">No hay datos de categorías.</p>
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 mt-4">
      {data.map((c) => (
        <div
          key={c.categoryId}
          className="bg-white shadow rounded-lg p-4 flex flex-col items-center"
        >
          <p className="text-gray-500">{c.categoryName}</p>
          <p className="text-2xl font-semibold">{c.totalStock}</p>
        </div>
      ))}
    </div>
  )
}
