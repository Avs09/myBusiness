// src/pages/Reports.tsx
import React, { useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import ReportFilters from '@/components/ReportFilters'
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  ColumnDef,
  flexRender,
} from '@tanstack/react-table'
import Button from '@/components/ui/button'
import Papa from 'papaparse'
import * as XLSX from 'xlsx'
import jsPDF from 'jspdf'
import 'jspdf-autotable'
import { useForm } from 'react-hook-form'
import KpiTile from '@/components/KpiTile'
import {
  Box as BoxIcon,
  DollarSign as DollarIcon,
  TrendingUp as TrendIcon,
} from 'lucide-react'
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'

import {
  getInventoryReport,
  getReportSummary,
  getInventorySnapshot,
  scheduleReport,
} from '@/api/report'
import {
  fetchStockEvolution,
  StockEvolutionDto,
  fetchMovementTypeCounts,
  MovementTypeCountDto,
  fetchTopProducts,
  TopProductDto,
} from '@/api/movements'

import type {
  InventoryReportRowDto,
  ReportFilter,
  ReportSummaryDto,
  SnapshotDto,
  ScheduleDto,
} from '@/schemas/report'
import Modal from '@/components/ui/modal'

interface PageInfo {
  pageIndex: number
  pageSize: number
  totalPages: number
  totalElements: number
}

interface ScheduleFormValues {
  email: string
  frequency: 'DAILY' | 'WEEKLY'
}

export default function Reports() {
  const { getAuthHeader } = useAuth()
  const headers = getAuthHeader() as Record<string, string>

  // ------------- Estado general -------------
  // Filtros dinámicos
  const [filters, setFilters] = useState<ReportFilter>({})

  // Tabla de resultados
  const [data, setData] = useState<InventoryReportRowDto[]>([])
  const [pageInfo, setPageInfo] = useState<PageInfo>({
    pageIndex: 0,
    pageSize: 20,
    totalPages: 0,
    totalElements: 0,
  })
  const [loading, setLoading] = useState(false)

  // KPIs resumen
  const [summary, setSummary] = useState<ReportSummaryDto | null>(null)

  // Export menu
  const [exportMenuOpen, setExportMenuOpen] = useState(false)

  // Programar reporte
  const [showScheduleModal, setShowScheduleModal] = useState(false)
  const {
    register,
    handleSubmit,
    formState: { errors: schedErrors, isSubmitting: schedSubmitting },
  } = useForm<ScheduleFormValues>({ defaultValues: { frequency: 'DAILY', email: '' } })

  // Gráficos ligeros
  const [chartsLoading, setChartsLoading] = useState(true)
  const [evolution, setEvolution] = useState<StockEvolutionDto[]>([])
  const [byType, setByType] = useState<MovementTypeCountDto[]>([])
  const [topProducts, setTopProducts] = useState<TopProductDto[]>([])

  // Histórico comparativo
  const [historicRange, setHistoricRange] = useState<{ from: string; to: string }>({ from: '', to: '' })
  const [snapshotHistoric, setSnapshotHistoric] = useState<SnapshotDto[]>([])
  const [historicLoading, setHistoricLoading] = useState(false)

  // ------------- Columnas de tabla -------------
  const columns = useMemo<ColumnDef<InventoryReportRowDto>[]>(
    () => [
      { accessorKey: 'productName', header: 'Producto' },
      { accessorKey: 'categoryName', header: 'Categoría' },
      { accessorKey: 'unitName', header: 'Unidad' },
      { accessorKey: 'currentStock', header: 'Stock Actual' },
      {
        accessorKey: 'lastMovementDate',
        header: 'Último Movimiento',
        cell: ({ getValue }) => new Date(getValue<string>()).toLocaleString(),
      },
    ],
    []
  )
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
  })

  // ------------- Funciones -------------

  // 1) Cargar KPIs resumen
  const fetchSummary = async () => {
    try {
      const sum = await getReportSummary(filters)
      setSummary(sum)
    } catch (err: any) {
      console.error(err)
      toast.error(err.response?.data?.message || 'No se pudo cargar resumen de KPIs')
    }
  }

  // 2) Tabla + paginación
  const fetchPage = async (page: number) => {
    setLoading(true)
    try {
      const all = await getInventoryReport(filters)
      const start = page * pageInfo.pageSize
      const slice = all.slice(start, start + pageInfo.pageSize)
      setData(slice)
      setPageInfo({
        pageIndex: page,
        pageSize: pageInfo.pageSize,
        totalElements: all.length,
        totalPages: Math.max(1, Math.ceil(all.length / pageInfo.pageSize)),
      })
    } catch (err: any) {
      console.error(err)
      toast.error(err.response?.data?.message || 'Error cargando reporte')
    } finally {
      setLoading(false)
    }
  }

  // 3) Gráficos ligeros
  const fetchCharts = async () => {
    setChartsLoading(true)
    try {
      setEvolution(await fetchStockEvolution(30, headers))
      setByType(await fetchMovementTypeCounts(30, headers))
      setTopProducts(await fetchTopProducts(30, 5, headers))
    } catch (err) {
      console.error(err)
      toast.error('Error cargando datos de gráficos')
    } finally {
      setChartsLoading(false)
    }
  }

  // 4) Histórico comparativo
  const fetchHistoric = async () => {
    if (!historicRange.from || !historicRange.to) {
      toast.error('Selecciona rango de fechas para histórico')
      return
    }
    setHistoricLoading(true)
    try {
      const snap = await getInventorySnapshot(historicRange.from, historicRange.to)
      setSnapshotHistoric(snap)
    } catch (err) {
      console.error(err)
      toast.error('Error cargando histórico')
    } finally {
      setHistoricLoading(false)
    }
  }

  // 5) Exportaciones
  const exportCSV = () => {
    const csv = Papa.unparse(data)
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'reporte-inventario.csv'
    a.click()
    URL.revokeObjectURL(url)
    setExportMenuOpen(false)
  }
  const exportExcel = () => {
    const ws = XLSX.utils.json_to_sheet(data)
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, 'Reporte')
    const buf = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([buf], { type: 'application/octet-stream' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'reporte-inventario.xlsx'
    a.click()
    URL.revokeObjectURL(url)
    setExportMenuOpen(false)
  }
  const exportPDF = () => {
    const doc = new jsPDF()
    doc.text('Reporte de Inventario', 14, 16)
    const head = [columns.map((col) => String(col.header))]
    const body = data.map((row) => [
      row.productName,
      row.categoryName,
      row.unitName,
      String(row.currentStock),
      new Date(row.lastMovementDate).toLocaleString(),
    ])
    ;(doc as any).autoTable({
      startY: 20,
      head,
      body,
      styles: { fontSize: 8 },
      headStyles: { fillColor: [41, 128, 185] },
    })
    doc.save('reporte-inventario.pdf')
    setExportMenuOpen(false)
  }
  const handlePrint = () => {
    window.print()
    setExportMenuOpen(false)
  }

  // 6) Programar reporte
  const onSchedule = async (vals: ScheduleFormValues) => {
    try {
      const dto: ScheduleDto = { ...filters, email: vals.email, frequency: vals.frequency }
      await scheduleReport(dto)
      toast.success('Reporte programado correctamente')
      setShowScheduleModal(false)
    } catch (err: any) {
      console.error(err)
      toast.error(err.response?.data?.message || 'No se pudo programar el reporte')
    }
  }

  // ------------- useEffect inicial y al cambiar filtros -------------
  useEffect(() => {
    fetchSummary()
    fetchPage(0)
    fetchCharts()
  }, [filters])

  // ------------- Render -------------
  return (
    <div className="p-6 space-y-6">
      <h2 className="text-2xl font-semibold">Reportes de Inventario</h2>

      {/* KPIs clave */}
      {summary && (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <KpiTile
            label="Total SKUs"
            value={summary.totalSkus.toString()}
            icon={<BoxIcon />}
            colorBg="bg-blue-50"
          />
          <KpiTile
            label="Valor total inventario"
            value={`$ ${summary.totalValue}`}
            icon={<DollarIcon />}
            colorBg="bg-green-50"
          />
          <KpiTile
            label="Días de stock"
            value={summary.daysOfStock.toString()}
            icon={<TrendIcon />}
            colorBg="bg-yellow-50"
          />
        </div>
      )}

      {/* Filtros dinámicos */}
      <ReportFilters filters={filters} onChange={setFilters} onApply={() => fetchPage(0)} />

      {/* Export & Programar */}
      <div className="flex items-center space-x-2">
        <div className="relative inline-block">
          <Button onClick={() => setExportMenuOpen((o) => !o)}>Exportar ▾</Button>
          {exportMenuOpen && (
            <div className="absolute left-0 mt-2 w-40 bg-white border shadow-lg z-10">
              <button
                onClick={exportCSV}
                className="block w-full text-left px-4 py-2 hover:bg-gray-100"
              >
                CSV
              </button>
              <button
                onClick={exportExcel}
                className="block w-full text-left px-4 py-2 hover:bg-gray-100"
              >
                Excel
              </button>
              <button
                onClick={exportPDF}
                className="block w-full text-left px-4 py-2 hover:bg-gray-100"
              >
                PDF
              </button>
              <button
                onClick={handlePrint}
                className="block w-full text-left px-4 py-2 hover:bg-gray-100"
              >
                Imprimir
              </button>
            </div>
          )}
        </div>
        <Button variant="secondary" onClick={() => setShowScheduleModal(true)}>
          Programar Reporte
        </Button>
      </div>

      {/* Tabla de resultados */}
      <div className="overflow-x-auto bg-white shadow rounded-lg">
        <table className="min-w-full table-auto border-collapse">
          <thead className="bg-gray-100">
            {table.getHeaderGroups().map((hg) => (
              <tr key={hg.id}>
                {hg.headers.map((h) => (
                  <th
                    key={h.id}
                    className="p-2 border text-left font-medium"
                  >
                    {flexRender(h.column.columnDef.header, h.getContext())}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length} className="p-4 text-center">
                  Cargando...
                </td>
              </tr>
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="p-4 text-center">
                  No hay registros.
                </td>
              </tr>
            ) : (
              table.getRowModel().rows.map((r) => (
                <tr key={r.id} className="hover:bg-gray-50">
                  {r.getVisibleCells().map((c) => (
                    <td key={c.id} className="p-2 border">
                      {flexRender(c.column.columnDef.cell, c.getContext())}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Paginación */}
      <div className="flex justify-between items-center">
        <span>
          Página {pageInfo.pageIndex + 1} de {pageInfo.totalPages} ({pageInfo.totalElements} registros)
        </span>
        <div className="space-x-2">
          <Button disabled={pageInfo.pageIndex === 0 || loading} onClick={() => fetchPage(pageInfo.pageIndex - 1)}>
            Anterior
          </Button>
          <Button
            disabled={pageInfo.pageIndex + 1 >= pageInfo.totalPages || loading}
            onClick={() => fetchPage(pageInfo.pageIndex + 1)}
          >
            Siguiente
          </Button>
        </div>
      </div>

      {/* Histórico comparativo */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold">Histórico de Inventario</h3>
        <div className="flex space-x-2">
          <input
            type="date"
            value={historicRange.from}
            onChange={(e) => setHistoricRange({ ...historicRange, from: e.target.value })}
            className="border rounded px-2 py-1"
          />
          <input
            type="date"
            value={historicRange.to}
            onChange={(e) => setHistoricRange({ ...historicRange, to: e.target.value })}
            className="border rounded px-2 py-1"
          />
          <Button onClick={fetchHistoric} disabled={historicLoading}>
            {historicLoading ? 'Cargando...' : 'Cargar Histórico'}
          </Button>
        </div>
        <div className="overflow-x-auto bg-white shadow rounded-lg">
          <table className="min-w-full table-auto border-collapse">
            <thead className="bg-gray-100">
              <tr>
                <th className="p-2 border">Fecha</th>
                <th className="p-2 border">Total SKUs</th>
                <th className="p-2 border">Valor Inventario</th>
              </tr>
            </thead>
            <tbody>
              {snapshotHistoric.map((s, i) => (
                <tr key={i} className="hover:bg-gray-50">
                  <td className="p-2 border">{s.date}</td>
                  <td className="p-2 border">{s.totalProducts}</td>
                  <td className="p-2 border">${s.totalValue}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Gráficos ligeros */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white shadow rounded-lg p-4">
          <h3 className="font-semibold mb-2">Evolución Inventario (30d)</h3>
          {chartsLoading ? (
            <p>Cargando gráfico...</p>
          ) : (
            <ResponsiveContainer width="100%" height={200}>
              <LineChart data={evolution}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" tick={{ fontSize: 12 }} />
                <YAxis allowDecimals={false} />
                <Tooltip formatter={(v) => [v, 'Stock']} labelFormatter={(l) => `Fecha: ${l}`} />
                <Line type="monotone" dataKey="totalStock" stroke="#2563eb" dot={{ r: 3 }} />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
        <div className="bg-white shadow rounded-lg p-4">
          <h3 className="font-semibold mb-2">Movimientos por Tipo (30d)</h3>
          {chartsLoading ? (
            <p>Cargando gráfico...</p>
          ) : (
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={byType}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="movementType" tick={{ fontSize: 12 }} />
                <YAxis allowDecimals={false} />
                <Tooltip formatter={(v) => [v, 'Cantidad']} labelFormatter={(l) => `Tipo: ${l}`} />
                <Bar dataKey="count" fill="#10b981" />
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>
        <div className="bg-white shadow rounded-lg p-4">
          <h3 className="font-semibold mb-2">Top 5 Productos (30d)</h3>
          {chartsLoading ? (
            <p>Cargando datos...</p>
          ) : (
            <ol className="list-decimal list-inside space-y-1">
              {topProducts.map((p) => (
                <li key={p.productId} className="flex justify-between">
                  <span>{p.productName}</span>
                  <span className="font-semibold">{p.totalOut}</span>
                </li>
              ))}
            </ol>
          )}
        </div>
      </div>

      {/* Modal Programar Reporte */}
      {showScheduleModal && (
        <Modal onClose={() => setShowScheduleModal(false)}>
          <h3 className="text-lg font-semibold mb-4">Programar Reporte</h3>
          <form onSubmit={handleSubmit(onSchedule)} className="space-y-4">
            <div>
              <label className="block text-sm mb-1">Email</label>
              <input
                type="email"
                {...register('email', { required: 'Email obligatorio' })}
                className="w-full border rounded px-3 py-2"
              />
              {schedErrors.email && (
                <p className="text-red-600 text-xs">{schedErrors.email.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm mb-1">Frecuencia</label>
              <select
                {...register('frequency')}
                className="w-full border rounded px-3 py-2"
              >
                <option value="DAILY">Diario</option>
                <option value="WEEKLY">Semanal</option>
              </select>
            </div>
            <div className="flex justify-end space-x-2">
              <Button
                variant="secondary"
                type="button"
                onClick={() => setShowScheduleModal(false)}
              >
                Cancelar
              </Button>
              <Button type="submit" disabled={schedSubmitting}>
                Guardar
              </Button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}
