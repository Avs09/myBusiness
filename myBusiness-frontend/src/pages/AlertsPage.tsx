import React from 'react'
import OpenAlertsTable from '@/components/OpenAlertsTable'

export default function AlertsPage() {
  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-semibold">Alertas Generadas</h1>
      <OpenAlertsTable />
    </div>
  )
}
