// src/api/reports.ts
import { axiosWithAuth } from './axiosClient';
import type {
  InventoryReportRowDto,
  ReportFilter,
  ReportSummaryDto,
  ScheduleDto,
  SnapshotDto,
} from '@/schemas/report';

/**
 * 1) Listado completo de filas de inventario según filtros.
 *    GET /api/reports/inventory?...
 */
export async function getInventoryReport(
  filter: ReportFilter
): Promise<InventoryReportRowDto[]> {
  const client = axiosWithAuth();
  const params: Record<string, any> = {};
  if (filter.productId !== undefined) params.productId = filter.productId;
  if (filter.categoryId !== undefined) params.categoryId = filter.categoryId;
  if (filter.unitId !== undefined) params.unitId = filter.unitId;
  if (filter.dateFrom) params.dateFrom = filter.dateFrom;
  if (filter.dateTo) params.dateTo = filter.dateTo;
  if (filter.movementType) params.movementType = filter.movementType;
  if (filter.thresholdBelow !== undefined) params.thresholdBelow = filter.thresholdBelow;
  const resp = await client.get<InventoryReportRowDto[]>('/reports/inventory', {
    params,
  });
  return resp.data;
}

/**
 * 2) Resumen ejecutivo (KPIs) para el reporte, según filtros.
 *    GET /api/reports/summary?...
 */
export async function getReportSummary(
  filter: ReportFilter
): Promise<ReportSummaryDto> {
  const client = axiosWithAuth();
  const params: Record<string, any> = {};
  if (filter.productId !== undefined) params.productId = filter.productId;
  if (filter.categoryId !== undefined) params.categoryId = filter.categoryId;
  if (filter.unitId !== undefined) params.unitId = filter.unitId;
  if (filter.dateFrom) params.dateFrom = filter.dateFrom;
  if (filter.dateTo) params.dateTo = filter.dateTo;
  if (filter.movementType) params.movementType = filter.movementType;
  if (filter.thresholdBelow !== undefined) params.thresholdBelow = filter.thresholdBelow;
  const resp = await client.get<ReportSummaryDto>('/reports/summary', {
    params,
  });
  return resp.data;
}

/**
 * 3) Snapshot histórico de inventario entre dos fechas.
 *    GET /api/reports/inventory-snapshot?dateFrom=YYYY-MM-DD&dateTo=YYYY-MM-DD
 */
export async function getInventorySnapshot(
  dateFrom: string,
  dateTo: string
): Promise<SnapshotDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<SnapshotDto[]>('/reports/inventory-snapshot', {
    params: { dateFrom, dateTo },
  });
  return resp.data;
}

/**
 * 4) Programa envío periódico de un reporte.
 *    POST /api/reports/schedule
 */
export async function scheduleReport(
  dto: ScheduleDto
): Promise<void> {
  const client = axiosWithAuth();
  await client.post('/reports/schedule', dto);
}
