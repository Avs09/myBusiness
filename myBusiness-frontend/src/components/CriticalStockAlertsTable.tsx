// src/components/CriticalStockAlertsTable.tsx
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import { fetchCriticalAlerts, markAlertRead, CriticalAlertOutputDto } from '@/api/alerts'
import { XCircle } from 'lucide-react'

export default function CriticalStockAlertsTable() {
  const { getAuthHeader } = useAuth()
  const [alerts, setAlerts] = useState<CriticalAlertOutputDto[]>([])
  const [loading, setLoading] = useState(true)

  const loadAlerts = () => {
    setLoading(true)
    const headers = getAuthHeader() as Record<string, string>
    fetchCriticalAlerts(headers)
      .then(data => setAlerts(data))
      .catch((err: any) => {
        console.error('Error cargando alertas de stock crítico:', err)
        toast.error(err.response?.data?.message || 'No se pudieron cargar las alertas.')
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadAlerts()

  }, [])

  if (loading) {
    return <p className="text-gray-600 mt-4">Cargando alertas de stock crítico...</p>
  }

  if (alerts.length === 0) {
    return <p className="text-gray-600 mt-4">No hay alertas de stock crítico.</p>
  }

  const tipoEnEspanol = (type: string) => {
    if (type === 'UNDERSTOCK') return 'Stock Bajo'
    if (type === 'OVERSTOCK') return 'Stock Alto'
    return type
  }

  return (
    <div className="overflow-x-auto mt-4">
      <table className="w-full bg-white shadow rounded-lg overflow-hidden border-collapse">
        <thead className="bg-red-100">
          <tr>
            <th className="p-2 border text-left">Producto</th>
            <th className="p-2 border text-left">Stock Actual</th>
            <th className="p-2 border text-left">Umbral Mínimo</th>
            <th className="p-2 border text-left">Umbral Máximo</th>
            <th className="p-2 border text-left">Tipo de Alerta</th>
            <th className="p-2 border text-left">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {alerts.map((a) => (
            <tr key={a.id} className="hover:bg-gray-50">
              <td className="p-2 border">{a.productName}</td>
              <td className="p-2 border">{a.currentStock}</td>
              <td className="p-2 border">{a.thresholdMin}</td>
              <td className="p-2 border">{a.thresholdMax}</td>
              <td className="p-2 border">{tipoEnEspanol(a.alertType)}</td>
              <td className="p-2 border space-x-2">
                <button
                  onClick={async () => {
                    if (!confirm('Descartar alerta?')) return;
                    try {
                      const headers = getAuthHeader() as Record<string,string>
                      await markAlertRead(a.id, headers)
                      toast.success('Alerta descartada')
                      loadAlerts()
                    } catch {
                      toast.error('Error descartando alerta')
                    }
                  }}
                  className="text-yellow-600 hover:underline flex items-center"
                >
                  <XCircle className="w-4 h-4 mr-1" /> Descartar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
