// src/api/movements.ts
import { axiosWithAuth } from './axiosClient';
import type { MovementInputDto, MovementOutputDto } from '@/schemas/movement';

export type { MovementInputDto, MovementOutputDto } from '@/schemas/movement';

// -------------- Paginación de Movimientos --------------
export interface MovementsPageResponse {
  content: MovementOutputDto[];
  number: number;        // página actual (0-based)
  size: number;          // tamaño de página
  totalElements: number;
  totalPages: number;
}

export async function fetchMovementsPaginated(
  page: number,
  size: number,
  filters: {
    productId?: number;
    dateFrom?: string;
    dateTo?: string;
    movementType?: 'ENTRY' | 'EXIT' | 'ADJUSTMENT';
    search?: string;
  }
): Promise<MovementsPageResponse> {
  const client = axiosWithAuth();
  const params: Record<string, any> = { page, size };
  if (filters.productId) params.productId = filters.productId;
  if (filters.dateFrom) params.dateFrom = filters.dateFrom;
  if (filters.dateTo) params.dateTo = filters.dateTo;
  if (filters.movementType) params.movementType = filters.movementType;
  if (filters.search) params.search = filters.search;
  const resp = await client.get<MovementsPageResponse>('/movements', { params });
  return resp.data;
}

export async function createMovement(
  dto: MovementInputDto
): Promise<MovementOutputDto> {
  const client = axiosWithAuth();
  const resp = await client.post<MovementOutputDto>('/movements', dto);
  return resp.data;
}

export async function fetchMovementById(
  id: number
): Promise<MovementOutputDto> {
  const client = axiosWithAuth();
  const resp = await client.get<MovementOutputDto>(`/movements/${id}`);
  return resp.data;
}

export async function updateMovement(
  id: number,
  dto: MovementInputDto
): Promise<MovementOutputDto> {
  const client = axiosWithAuth();
  const resp = await client.put<MovementOutputDto>(`/movements/${id}`, dto);
  return resp.data;
}

export async function deleteMovement(
  id: number
): Promise<void> {
  const client = axiosWithAuth();
  await client.delete(`/movements/${id}`);
}

// Tendencias y recuentos
export interface DailyMovementCountDto {
  date: string;
  count: number;
}
export async function fetchDailyMovementTrend(
  days: number
): Promise<DailyMovementCountDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<DailyMovementCountDto[]>(`/movements/daily-trend`, {
    params: { days },
  });
  return resp.data;
}

export interface MovementTypeCountDto {
  movementType: string;
  count: number;
}
export async function fetchMovementTypeCounts(
  days: number
): Promise<MovementTypeCountDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<MovementTypeCountDto[]>('/movements/type-counts', {
    params: { days },
  });
  return resp.data;
}

export async function fetchRecentMovements(
  limit: number
): Promise<MovementOutputDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<MovementOutputDto[]>('/movements/recent', {
    params: { limit },
  });
  return resp.data;
}

export async function fetchMovementsLast24hCount(): Promise<number> {
  const client = axiosWithAuth();
  const resp = await client.get<number>('/movements/last-24h');
  return resp.data;
}

export interface StockEvolutionDto {
  date: string;
  totalStock: number;
}
export async function fetchStockEvolution(
  days: number
): Promise<StockEvolutionDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<StockEvolutionDto[]>('/movements/daily-inventory', {
    params: { days },
  });
  return resp.data;
}

export interface TopProductDto {
  productId: number;
  productName: string;
  totalOut: number;
}
export async function fetchTopProducts(
  days: number,
  limit: number
): Promise<TopProductDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<TopProductDto[]>('/movements/top-products', {
    params: { days, limit },
  });
  return resp.data;
}

export interface SnapshotDto {
  date: string;
  totalProducts: number;
  totalValue: string;
}
export async function fetchSnapshotInventory(
  dateFrom: string,
  dateTo: string
): Promise<SnapshotDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<SnapshotDto[]>('/reports/inventory-snapshot', {
    params: { dateFrom, dateTo },
  });
  return resp.data;
}
