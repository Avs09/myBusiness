// src/components/OpenAlertsTable.tsx
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import { fetchAlertsHistory, deleteAlert, AlertOutputDto } from '@/api/alerts'
import { Trash2 } from 'lucide-react'

export default function OpenAlertsTable() {
  const { getAuthHeader } = useAuth()
  const [alerts, setAlerts] = useState<AlertOutputDto[]>([])
  const [loading, setLoading] = useState(true)

  const loadAlerts = () => {
    setLoading(true)
    const headers = getAuthHeader() as Record<string,string>
    fetchAlertsHistory(headers)
      .then(setAlerts)
      .catch(err => {
        console.error('Error cargando alertas:', err)
        toast.error('No se pudieron cargar alertas')
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadAlerts()
    const onNewAlert = (_e: Event) => loadAlerts()
    window.addEventListener('alerts:new', onNewAlert as EventListener)
    return () => {
      window.removeEventListener('alerts:new', onNewAlert as EventListener)
    }
  }, [])

  if (loading) {
    return <p className="text-gray-600 mt-4">Cargando alertas...</p>
  }
  if (alerts.length === 0) {
    return <p className="text-gray-600 mt-4">No hay alertas.</p>
  }

  const tipoEnEspanol = (type: string) => {
    if (type === 'UNDERSTOCK') return 'Stock Bajo'
    if (type === 'OVERSTOCK')  return 'Stock Alto'
    return type
  }

  return (
    <div className="overflow-x-auto mt-4">
      <table className="w-full bg-white shadow rounded-lg overflow-hidden border-collapse">
        <thead className="bg-red-100">
          <tr>
            <th className="p-2 border text-left">ID</th>
            <th className="p-2 border text-left">Producto</th>
            <th className="p-2 border text-left">Movimiento ID</th>
            <th className="p-2 border text-left">Stock Actual</th>
            <th className="p-2 border text-left">Tipo</th>
            <th className="p-2 border text-left">Umbral Mínimo</th>
            <th className="p-2 border text-left">Umbral Máximo</th>
            <th className="p-2 border text-left">Fecha Creación</th>
            <th className="p-2 border text-left">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {alerts.map(a => (
            <tr key={a.id} className="hover:bg-gray-50">
              <td className="p-2 border">{a.id}</td>
              <td className="p-2 border">{a.productName}</td>
              <td className="p-2 border">{a.movementId ?? '-'}</td>
              <td className="p-2 border">{a.currentStock != null ? a.currentStock : '-'}</td>
              <td className="p-2 border">{tipoEnEspanol(a.alertType)}</td>
              <td className="p-2 border">{a.thresholdMin}</td>
              <td className="p-2 border">{a.thresholdMax}</td>
              <td className="p-2 border">
                {a.createdDate
                  ? new Date(a.createdDate).toLocaleString()
                  : '-'}
              </td>
              <td className="p-2 border space-x-2">
                <button
                  onClick={async () => {
                    if (!confirm('¿Eliminar alerta permanentemente?')) return
                    try {
                      const headers = getAuthHeader() as Record<string,string>
                      await deleteAlert(a.id, headers)
                      toast.success('Alerta borrada')
                      loadAlerts()
                    } catch {
                      toast.error('Error al eliminar alerta')
                    }
                  }}
                  className="text-red-600 hover:underline flex items-center"
                >
                  <Trash2 className="w-4 h-4 mr-1" /> Borrar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
