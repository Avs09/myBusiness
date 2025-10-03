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
import autoTable from 'jspdf-autotable'
import { saveAs } from 'file-saver'
import { useForm } from 'react-hook-form'
import KpiTile from '@/components/KpiTile'
import {
  Box as BoxIcon,
  DollarSign as DollarIcon,
  TrendingUp as TrendIcon,
} from 'lucide-react'
import { formatCOP } from '@/utils/currency'
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
import { getBusiness } from '@/api/business'
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
import InvoicePreview from '@/components/reports/InvoicePreview'

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

  // Invoice preview modal
  const [showInvoicePreview, setShowInvoicePreview] = useState(false)
  const [fullData, setFullData] = useState<InventoryReportRowDto[]>([])
  const [loadingPreview, setLoadingPreview] = useState(false)

  // Business information for invoice
  const [businessInfo, setBusinessInfo] = useState<any>(null)
  const [exportingPDF, setExportingPDF] = useState(false)

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
      setEvolution(await fetchStockEvolution(30))
      setByType(await fetchMovementTypeCounts(30))
      setTopProducts(await fetchTopProducts(30, 5))
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

  // Export PDF (from preview modal)
  const exportPDF = async () => {
    setExportingPDF(true)
    try {
      console.log('Iniciando exportación PDF...')
      const doc = new jsPDF('p', 'pt', 'a4')

      // Helpers
      const pageWidth = doc.internal.pageSize.getWidth()
      const marginX = 40
      const lineGap = 16

      const loadLogo = async (src: string) => {
        try {
          const res = await fetch(src)
          const blob = await res.blob()
          return await new Promise<string>((resolve) => {
            const reader = new FileReader()
            reader.onload = () => resolve(String(reader.result))
            reader.readAsDataURL(blob)
          })
        } catch {
          return ''
        }
      }

      let cursorY = 40

      // Header (logo + título + fecha)
      let logoDataUrl = null
      if (businessInfo?.logoUrl) {
        logoDataUrl = await loadLogo(businessInfo.logoUrl)
      }
      if (!logoDataUrl) {
        // Fallback to default logo
        logoDataUrl = await loadLogo('/logo.png')
      }
      if (logoDataUrl) {
        const imgW = 100
        const imgH = 32
        doc.addImage(logoDataUrl, 'PNG', marginX, cursorY, imgW, imgH)
      }
      doc.setFontSize(16)
      doc.setFont('helvetica', 'bold')
      const title = 'Factura de Inventario'
      const titleW = doc.getTextWidth(title)
      doc.text(title, pageWidth - marginX - titleW, cursorY + 18)

      doc.setFontSize(10)
      doc.setFont('helvetica', 'normal')
      const now = new Date()
      const fecha = now.toLocaleString()
      doc.text(`Fecha: ${fecha}`, pageWidth - marginX - doc.getTextWidth(`Fecha: ${fecha}`), cursorY + 36)

      cursorY += 52

      // Datos de emisor y destinatario (opcionales: empresa y usuario)
      doc.setFontSize(11)
      doc.setFont('helvetica', 'bold')
      doc.text('Emisor:', marginX, cursorY)
      doc.setFont('helvetica', 'normal')
      doc.text('MyBusiness', marginX, cursorY + lineGap)
      doc.text('https://mybusiness.local', marginX, cursorY + lineGap * 2)

      doc.setFont('helvetica', 'bold')
      doc.text('Generado para:', pageWidth / 2, cursorY)
      doc.setFont('helvetica', 'normal')

      // Mostrar información del negocio y usuario de manera profesional
      const generatedFor = []

      // Agregar nombre del negocio (siempre obligatorio)
      if (businessInfo?.name) {
        generatedFor.push(businessInfo.name)
      }

      // Agregar nombre del usuario si está definido
      // Nota: Actualmente no tenemos nombres de usuario, pero preparado para cuando se implemente
      // if (userName) {
      //   generatedFor.push(userName)
      // }

      // Si no hay información específica, mostrar genérico
      if (generatedFor.length === 0) {
        generatedFor.push('Administrador del Sistema')
        generatedFor.push('MyBusiness - Sistema de Inventario')
      }

      generatedFor.forEach((info, index) => {
        doc.text(info, pageWidth / 2, cursorY + lineGap * (index + 1))
      })

      cursorY += lineGap * (generatedFor.length + 1) + 20

      // Resumen (KPIs)
      if (summary) {
        doc.setFont('helvetica', 'bold')
        doc.text('Resumen:', marginX, cursorY)
        doc.setFont('helvetica', 'normal')
        const resumen = [
          `Total SKUs: ${summary.totalSkus}`,
          `Valor total inventario: ${formatCOP(summary.totalValue)}`,
          `Días de stock (estimación): ${summary.daysOfStock}`
        ]
        resumen.forEach((t, i) => {
          doc.text(t, marginX, cursorY + lineGap * (i + 1))
        })
        cursorY += lineGap * (resumen.length + 1)
      }

      // Tabla de detalles (usar dataset completo ya cargado, limitado a 500 filas para evitar PDFs muy grandes)
      const maxRows = 500
      const dataToExport = fullData.slice(0, maxRows)
      const head = [['Producto', 'Categoría', 'Unidad', 'Stock Actual', 'Último Movimiento']]
      const body = dataToExport.map((row) => [
        row.productName,
        row.categoryName,
        row.unitName,
        String(row.currentStock ?? ''),
        row.lastMovementDate ? new Date(row.lastMovementDate).toLocaleString() : ''
      ])

      console.log('Generando tabla con', body.length, 'filas (limitado a', maxRows, 'para evitar PDFs muy grandes)...')
      autoTable(doc, {
        startY: cursorY,
        margin: { left: marginX, right: marginX },
        head,
        body,
        styles: { fontSize: 9, cellPadding: 6 },
        headStyles: { fillColor: [41, 128, 185], textColor: 255, fontStyle: 'bold' },
        didDrawPage: (data: any) => {
          // Footer con paginación
          const pageCount = doc.getNumberOfPages()
          const str = `Página ${data.pageNumber} de ${pageCount}`
          doc.setFontSize(9)
          doc.setTextColor(140)
          doc.text(str, pageWidth - marginX - doc.getTextWidth(str), doc.internal.pageSize.getHeight() - 20)
        }
      })

      // Totales al final (si hay)
      if (summary) {
        const finalY = (doc as any).lastAutoTable.finalY || cursorY + 20
        doc.setFont('helvetica', 'bold')
        doc.text('Totales:', marginX, finalY + 28)
        doc.setFont('helvetica', 'normal')
        doc.text(`Valor total inventario: ${formatCOP(summary.totalValue)}`, marginX, finalY + 28 + lineGap)
        doc.text(`Total SKUs: ${summary.totalSkus}`, marginX, finalY + 28 + lineGap * 2)
      }

      console.log('Guardando PDF...')
      const pdfBlob = doc.output('blob')
      saveAs(pdfBlob, 'factura-inventario.pdf')
      toast.success('PDF exportado correctamente')
      setShowInvoicePreview(false)
    } catch (err: any) {
      console.error('Error exportando PDF:', err)
      toast.error('Error generando PDF: ' + (err.message || 'Desconocido'))
    } finally {
      setExportingPDF(false)
    }
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

    // Fetch business information for invoice
    const fetchBusinessInfo = async () => {
      try {
        const business = await getBusiness()
        setBusinessInfo(business)
      } catch (error) {
        console.error('Error fetching business info:', error)
      }
    }
    fetchBusinessInfo()
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
            value={formatCOP(summary.totalValue)}
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
        <Button onClick={async () => {
          setLoadingPreview(true)
          try {
            const all = await getInventoryReport(filters)
            setFullData(all)
            setShowInvoicePreview(true)
          } catch (err: any) {
            console.error(err)
            toast.error('Error cargando datos para previsualización')
          } finally {
            setLoadingPreview(false)
          }
        }} disabled={loadingPreview}>
          {loadingPreview ? 'Cargando...' : 'Exportar'}
        </Button>
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
                  <td className="p-2 border">{formatCOP(s.totalValue)}</td>
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

      {/* Modal Previsualización Factura */}
      {showInvoicePreview && (
        <Modal onClose={() => setShowInvoicePreview(false)} className="max-w-4xl">
          <InvoicePreview summary={summary} rows={fullData} businessInfo={businessInfo} />
          <div className="mt-4 flex justify-end space-x-2">
            <Button variant="secondary" onClick={() => setShowInvoicePreview(false)}>
              Cerrar
            </Button>
            <Button onClick={exportPDF} disabled={exportingPDF}>
              {exportingPDF ? 'Exportando...' : 'Confirmar y Exportar PDF'}
            </Button>
          </div>
        </Modal>
      )}
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
