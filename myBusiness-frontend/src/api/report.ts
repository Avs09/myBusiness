import { axiosWithAuth } from './axiosClient'
import type {
  InventoryReportRowDto,
  ReportFilter,
  ReportSummaryDto,
  ScheduleDto,
  SnapshotDto,
} from '@/schemas/report'

/**
 * 1) Obtiene el listado completo de filas de inventario según filtros.
 *    GET /reports/inventory?...
 */
export async function getInventoryReport(
  filter: ReportFilter
): Promise<InventoryReportRowDto[]> {
  const client = axiosWithAuth()
  const params: Record<string, any> = {}
  if (filter.productId !== undefined) params.productId = filter.productId
  if (filter.categoryId !== undefined) params.categoryId = filter.categoryId
  if (filter.unitId !== undefined) params.unitId = filter.unitId
  if (filter.dateFrom) params.dateFrom = filter.dateFrom
  if (filter.dateTo) params.dateTo = filter.dateTo
  if (filter.movementType) params.movementType = filter.movementType
  if (filter.thresholdBelow !== undefined) params.thresholdBelow = filter.thresholdBelow
  const resp = await client.get<InventoryReportRowDto[]>('/reports/inventory', {
    params,
  })
  return resp.data
}

/**
 * 2) Obtiene el resumen ejecutivo (KPIs) para el reporte, según filtros.
 *    GET /reports/summary?...
 */
export async function getReportSummary(
  filter: ReportFilter
): Promise<ReportSummaryDto> {
  const client = axiosWithAuth()
  const params: Record<string, any> = {}
  if (filter.productId !== undefined) params.productId = filter.productId
  if (filter.categoryId !== undefined) params.categoryId = filter.categoryId
  if (filter.unitId !== undefined) params.unitId = filter.unitId
  if (filter.dateFrom) params.dateFrom = filter.dateFrom
  if (filter.dateTo) params.dateTo = filter.dateTo
  if (filter.movementType) params.movementType = filter.movementType
  if (filter.thresholdBelow !== undefined) params.thresholdBelow = filter.thresholdBelow
  const resp = await client.get<ReportSummaryDto>('/reports/summary', {
    params,
  })
  return resp.data
}

/**
 * 3) Obtiene el snapshot histórico de inventario entre dos fechas.
 *    GET /reports/inventory-snapshot?dateFrom=YYYY-MM-DD&dateTo=YYYY-MM-DD
 */
export async function getInventorySnapshot(
  dateFrom: string,
  dateTo: string
): Promise<SnapshotDto[]> {
  const client = axiosWithAuth()
  const resp = await client.get<SnapshotDto[]>('/reports/inventory-snapshot', {
    params: { dateFrom, dateTo },
  })
  return resp.data
}

/**
 * 4) Programa el envío periódico de un reporte.
 *    POST /reports/schedule
 */
export async function scheduleReport(
  dto: ScheduleDto
): Promise<void> {
  const client = axiosWithAuth()
  await client.post<void>('/reports/schedule', dto)
}
