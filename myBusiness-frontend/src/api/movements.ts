// src/api/movements.ts
import axios from 'axios'
import { axiosWithAuth } from './axiosClient'
import type { MovementInputDto, MovementOutputDto } from '@/schemas/movement'

// Reexportar los tipos para que puedan importarse desde "@/api/movements"
export type { MovementInputDto, MovementOutputDto } from '@/schemas/movement'


const BASE_URL = import.meta.env.VITE_API_URL as string

// -------------- Paginación de Movimientos --------------
export interface MovementsPageResponse {
  content: MovementOutputDto[]
  number: number        // página actual (0-based)
  size: number          // tamaño de página
  totalElements: number
  totalPages: number
}

/**
 * Obtiene listado paginado de movimientos, con filtros opcionales:
 * page, size, productId, dateFrom, dateTo, movementType, search (texto en motivo o nombre producto).
 * GET /api/movements
 */
export async function fetchMovementsPaginated(
  page: number,
  size: number,
  filters: {
    productId?: number;
    dateFrom?: string; // “YYYY-MM-DD”
    dateTo?: string;   // “YYYY-MM-DD”
    movementType?: 'ENTRY' | 'EXIT' | 'ADJUSTMENT';
    search?: string;
  },
  headers: Record<string, string>
): Promise<MovementsPageResponse> {
  const client = axiosWithAuth()
  const params: Record<string, any> = { page, size }
  if (filters.productId) params.productId = filters.productId
  if (filters.dateFrom) params.dateFrom = filters.dateFrom
  if (filters.dateTo) params.dateTo = filters.dateTo
  if (filters.movementType) params.movementType = filters.movementType
  if (filters.search) params.search = filters.search
  const resp = await client.get<MovementsPageResponse>(`${BASE_URL}/movements`, {
    headers,
    params,
  })
  return resp.data
}

/**
 * Crea un nuevo movimiento.
 * POST /api/movements
 */
export async function createMovement(
  dto: MovementInputDto,
  headers: Record<string, string>
): Promise<MovementOutputDto> {
  const client = axiosWithAuth()
  const resp = await client.post<MovementOutputDto>(`${BASE_URL}/movements`, dto, { headers })
  return resp.data
}

/**
 * Obtiene detalle de un movimiento por id.
 * GET /api/movements/{id}
 */
export async function fetchMovementById(
  id: number,
  headers: Record<string, string>
): Promise<MovementOutputDto> {
  const client = axiosWithAuth()
  const resp = await client.get<MovementOutputDto>(`${BASE_URL}/movements/${id}`, { headers })
  return resp.data
}

/**
 * Edita un movimiento existente.
 * PUT /api/movements/{id}
 */
export async function updateMovement(
  id: number,
  dto: MovementInputDto,
  headers: Record<string, string>
): Promise<MovementOutputDto> {
  const client = axiosWithAuth()
  const resp = await client.put<MovementOutputDto>(`${BASE_URL}/movements/${id}`, dto, { headers })
  return resp.data
}

/**
 * Elimina un movimiento.
 * DELETE /api/movements/{id}
 */
export async function deleteMovement(
  id: number,
  headers: Record<string, string>
): Promise<void> {
  const client = axiosWithAuth()
  await client.delete(`${BASE_URL}/movements/${id}`, { headers })
}

// -------------- Endpoints existentes (tendencias, recuentos, etc.) --------------

/**
 * Tendencia diaria de movimientos (últimos `days` días).
 * GET /api/movements/daily-trend?days=N
 */
export interface DailyMovementCountDto {
  date: string   // "YYYY-MM-DD"
  count: number
}
export async function fetchDailyMovementTrend(
  days: number,
  headers: Record<string, string>
): Promise<DailyMovementCountDto[]> {
  const url = `${BASE_URL}/movements/daily-trend?days=${days}`
  const resp = await axios.get<DailyMovementCountDto[]>(url, { headers })
  return resp.data
}

/**
 * Conteo por tipo de movimiento (últimos `days` días).
 * GET /api/movements/type-counts?days=N
 */
export interface MovementTypeCountDto {
  movementType: string  // e.g. "ENTRY", "EXIT", "ADJUSTMENT"
  count: number
}
export async function fetchMovementTypeCounts(
  days: number,
  headers: Record<string, string>
): Promise<MovementTypeCountDto[]> {
  const url = `${BASE_URL}/movements/type-counts?days=${days}`
  const resp = await axios.get<MovementTypeCountDto[]>(url, { headers })
  return resp.data
}

/**
 * Últimos `limit` movimientos.
 * GET /api/movements/recent?limit=M
 */
export async function fetchRecentMovements(
  limit: number,
  headers: Record<string, string>
): Promise<MovementOutputDto[]> {
  const url = `${BASE_URL}/movements/recent?limit=${limit}`
  const resp = await axios.get<MovementOutputDto[]>(url, { headers })
  return resp.data
}

/**
 * Conteo de movimientos en últimas 24 horas.
 * GET /api/movements/last-24h
 * Devuelve un número con la cantidad.
 */
export async function fetchMovementsLast24hCount(
  headers: Record<string, string>
): Promise<number> {
  const url = `${BASE_URL}/movements/last-24h`
  const resp = await axios.get<number>(url, { headers })
  return resp.data
}

/**
 * Evolución diaria de inventario (últimos `days` días).
 * GET /api/movements/daily-inventory?days=N
 */
export interface StockEvolutionDto {
  date: string   // "YYYY-MM-DD"
  totalStock: number
}
export async function fetchStockEvolution(
  days: number,
  headers: Record<string, string>
): Promise<StockEvolutionDto[]> {
  const url = `${BASE_URL}/movements/daily-inventory?days=${days}`
  const resp = await axios.get<StockEvolutionDto[]>(url, { headers })
  return resp.data
}

/**
 * Top N productos más movidos (salidas) en últimos `days` días.
 * GET /api/movements/top-products?days=N&limit=M
 */
export interface TopProductDto {
  productId: number
  productName: string
  totalOut: number
}
export async function fetchTopProducts(
  days: number,
  limit: number,
  headers: Record<string, string>
): Promise<TopProductDto[]> {
  const url = `${BASE_URL}/movements/top-products?days=${days}&limit=${limit}`
  const resp = await axios.get<TopProductDto[]>(url, { headers })
  return resp.data
}

/**
 * Snapshot de inventario entre dos fechas.
 * GET /api/reports/inventory-snapshot?dateFrom=...&dateTo=...
 */
export interface SnapshotDto {
  date: string           // "YYYY-MM-DD"
  totalProducts: number  // SKUs activos en esa fecha
  totalValue: string     // BigDecimal→string
}
export async function fetchSnapshotInventory(
  dateFrom: string,
  dateTo: string,
  headers: Record<string, string>
): Promise<SnapshotDto[]> {
  const url = `${BASE_URL}/reports/inventory-snapshot`
  const resp = await axios.get<SnapshotDto[]>(url, {
    headers,
    params: { dateFrom, dateTo },
  })
  return resp.data
}
