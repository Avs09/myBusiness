// src/components/RecentMovementsTable.tsx
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import { fetchRecentMovements, MovementOutputDto } from '@/api/movements'
import Button from '@/components/ui/button'

export default function RecentMovementsTable() {
  const { getAuthHeader } = useAuth()
  const [movements, setMovements] = useState<MovementOutputDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const headers = getAuthHeader() as Record<string, string>
    fetchRecentMovements(10, headers)
      .then((data) => setMovements(data))
      .catch((err: any) => {
        console.error('Error cargando movimientos recientes:', err)
        toast.error(err.response?.data?.message || 'No se pudieron cargar movimientos.')
      })
      .finally(() => setLoading(false))

  }, [])

  if (loading) {
    return <p className="text-gray-600">Cargando movimientos recientes...</p>
  }
  if (movements.length === 0) {
    return <p>No hay movimientos recientes.</p>
  }

  return (
    <div>
      <div className="overflow-x-auto mt-4">
        <table className="w-full bg-white shadow rounded-lg overflow-hidden border-collapse">
          <thead className="bg-blue-100">
            <tr>
              <th className="p-2 border text-left">ID</th>
              <th className="p-2 border text-left">Producto</th> {/* cambiado */}
              <th className="p-2 border text-left">Tipo</th>
              <th className="p-2 border text-left">Cantidad</th>
              <th className="p-2 border text-left">Motivo</th>
              <th className="p-2 border text-left">Fecha</th>
              <th className="p-2 border text-left">Usuario</th>
            </tr>
          </thead>
          <tbody>
            {movements.map((m) => (
              <tr key={m.id} className="hover:bg-gray-50">
                <td className="p-2 border">{m.id}</td>
                <td className="p-2 border">{m.productName}</td> {/* usa nombre */}
                <td className="p-2 border">{m.movementType}</td>
                <td className="p-2 border">{m.quantity}</td>
                <td className="p-2 border">{m.reason}</td>
                <td className="p-2 border">
                  {new Date(m.movementDate).toLocaleString()}
                </td>
                <td className="p-2 border">{m.createdBy}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="flex justify-end mt-2">
        <Button
          size="sm"
          onClick={() => (window.location.href = '/movements')}
        >
          Ver todos
        </Button>
      </div>
    </div>
  )
}
