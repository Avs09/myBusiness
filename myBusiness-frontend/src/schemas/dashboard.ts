// src/schemas/dashboard.ts

/**
 * Este es el DTO que el backend devuelve en /api/dashboard/metrics
 */
export interface DashboardMetricsDto {
  totalProducts: number
  totalInventoryValue: string   
  totalOpenAlerts: number
  movementsLast7Days: number
}
