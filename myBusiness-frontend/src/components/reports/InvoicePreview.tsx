// src/components/reports/InvoicePreview.tsx
import React from 'react'
import { useAuth } from '@/hooks/useAuth'
import { formatCOP } from '@/utils/currency'
import type { ReportSummaryDto, InventoryReportRowDto } from '@/schemas/report'

type Props = {
   summary: ReportSummaryDto | null
   rows: InventoryReportRowDto[]
   businessInfo?: any
 }

export default function InvoicePreview({ summary, rows, businessInfo }: Props) {
  const { user } = useAuth()
  const today = new Date().toLocaleString()

  return (
    <div className="w-[880px] max-w-full bg-white rounded-lg border shadow-lg">
      {/* Header */}
      <div className="flex items-center justify-between px-6 py-4 border-b">
        <div className="flex items-center space-x-3">
           {businessInfo?.logoUrl ? (
             <img src={businessInfo.logoUrl} alt="logo" className="h-8 w-auto" />
           ) : (
             <img src="/logo.png" alt="logo" className="h-8 w-auto" />
           )}
           <div className="text-xs text-gray-500 leading-tight">
             <div className="font-semibold text-gray-700">{businessInfo?.name || 'MyBusiness'}</div>
             <div>https://mybusiness.local</div>
           </div>
         </div>
        <div className="text-right">
          <div className="text-xl font-bold tracking-wide">Factura de Inventario</div>
          <div className="text-xs text-gray-500">Fecha: {today}</div>
        </div>
      </div>

      {/* Parties */}
      <div className="grid grid-cols-2 gap-6 px-6 py-4 border-b">
        <div>
          <div className="text-sm font-semibold text-gray-700 mb-1">Emisor</div>
          <div className="text-xs text-gray-600">MyBusiness</div>
          <div className="text-xs text-gray-600">Bogotá, Colombia</div>
          <div className="text-xs text-gray-600">NIT: 900.000.000-0</div>
        </div>
        <div>
          <div className="text-sm font-semibold text-gray-700 mb-1">Generado para</div>
          {businessInfo?.name ? (
            <div className="text-xs text-gray-600">{businessInfo.name}</div>
          ) : (
            <>
              <div className="text-xs text-gray-600">Administrador del Sistema</div>
              <div className="text-xs text-gray-600">MyBusiness - Sistema de Inventario</div>
            </>
          )}
          <div className="text-xs text-gray-600">{user?.email ?? 'usuario@mybusiness.com'}</div>
        </div>
      </div>

      {/* Summary */}
      {summary && (
        <div className="grid grid-cols-3 gap-4 px-6 py-4 border-b">
          <div className="bg-gray-50 rounded-md p-3">
            <div className="text-[11px] text-gray-500">Total SKUs</div>
            <div className="text-lg font-semibold text-gray-800">{summary.totalSkus}</div>
          </div>
          <div className="bg-gray-50 rounded-md p-3">
            <div className="text-[11px] text-gray-500">Valor total inventario</div>
            <div className="text-lg font-semibold text-gray-800">{formatCOP(summary.totalValue)}</div>
          </div>
          <div className="bg-gray-50 rounded-md p-3">
            <div className="text-[11px] text-gray-500">Días de stock (estimado)</div>
            <div className="text-lg font-semibold text-gray-800">{summary.daysOfStock}</div>
          </div>
        </div>
      )}

      {/* Table */}
      <div className="px-6 py-4">
        <div className="text-sm font-semibold text-gray-700 mb-2">Detalle de inventario</div>
        <div className="max-h-[50vh] overflow-auto rounded-md border">
          <table className="w-full text-xs">
            <thead className="bg-gray-100 text-gray-700 sticky top-0">
              <tr>
                <th className="px-3 py-2 text-left border-b">Producto</th>
                <th className="px-3 py-2 text-left border-b">Categoría</th>
                <th className="px-3 py-2 text-left border-b">Unidad</th>
                <th className="px-3 py-2 text-left border-b">Stock Actual</th>
                <th className="px-3 py-2 text-left border-b">Último Movimiento</th>
              </tr>
            </thead>
            <tbody>
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-3 py-8 text-center text-gray-500">
                    No hay datos para mostrar.
                  </td>
                </tr>
              ) : (
                rows.map((r) => (
                  <tr key={`${r.productId}-${r.lastMovementDate}`} className="hover:bg-gray-50">
                    <td className="px-3 py-2 border-b">{r.productName}</td>
                    <td className="px-3 py-2 border-b">{r.categoryName}</td>
                    <td className="px-3 py-2 border-b">{r.unitName}</td>
                    <td className="px-3 py-2 border-b">{r.currentStock}</td>
                    <td className="px-3 py-2 border-b">
                      {r.lastMovementDate ? new Date(r.lastMovementDate).toLocaleString() : '-'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Footer */}
      <div className="px-6 py-4 border-t text-[10px] text-gray-500">
        Este documento es generado automáticamente por el sistema de inventario MyBusiness.
      </div>
    </div>
  )
}