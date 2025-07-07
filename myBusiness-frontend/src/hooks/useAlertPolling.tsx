import { useEffect, useRef } from 'react'
import axios from 'axios' // ya lo tienes, o puedes usar fetch desde alerts.ts si prefieres
import { useAuth } from '@/hooks/useAuth'
import toast from 'react-hot-toast'
import { AlertCircle } from 'lucide-react'

function AlertToast({ productName, alertType }: { productName: string; alertType: string }) {
  return (
    <div className="flex items-center space-x-2">
      <AlertCircle className="w-6 h-6 text-red-600 animate-pulse" />
      <div>
        <p className="font-semibold">Alerta: {productName}</p>
        <p className="text-sm">Tipo: {alertType === 'UNDERSTOCK' ? 'Stock bajo' : 'Stock alto'}</p>
      </div>
    </div>
  )
}

export function useAlertPolling(intervalMs: number = 5000) {
  const { getAuthHeader, user } = useAuth()
  const seenIdsRef = useRef<Set<number>>(new Set())

  useEffect(() => {
    if (!user) return
    let mounted = true

    const fetchAlerts = async () => {
      try {
        const headers = getAuthHeader() as Record<string, string>
        const alerts = await fetchUnreadAlerts(headers)
        if (!mounted) return
        alerts.forEach(alert => {
          const id: number = alert.id
          if (!seenIdsRef.current.has(id)) {
            toast.custom(t => (
              <div className={`
                max-w-sm w-full bg-white border-l-4 border-red-500 p-4 shadow-lg transform transition-all
                ${t.visible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-2'}
              `}>
                <AlertToast productName={alert.productName} alertType={alert.alertType} />
              </div>
            ), { duration: 10000 })
            seenIdsRef.current.add(id)
          }
        })
      } catch (err) {
        console.error('Error polling alertas:', err)
      }
    }

    ;(async () => {
      try {
        const headers = getAuthHeader() as Record<string, string>
        const initialAlerts = await fetchUnreadAlerts(headers)
        initialAlerts.forEach(alert => seenIdsRef.current.add(alert.id))
      } catch {
        // Ignorar errores iniciales
      }
      fetchAlerts()
      const timer = setInterval(fetchAlerts, intervalMs)
      return () => {
        mounted = false
        clearInterval(timer)
        seenIdsRef.current.clear()
      }
    })()
  }, [user, getAuthHeader, intervalMs])
}
