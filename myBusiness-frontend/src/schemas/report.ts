
export interface InventoryReportRowDto {
  productId: number
  productName: string
  categoryName: string
  unitName: string
  currentStock: number     
  lastMovementDate: string  
}

/**
 * Filtros para el reporte.
 */
export interface ReportFilter {
  productId?: number
  categoryId?: number
  unitId?: number
  dateFrom?: string   // “YYYY-MM-DD”
  dateTo?: string     // “YYYY-MM-DD”
  movementType?: 'ENTRY' | 'EXIT' | 'ADJUSTMENT'
  thresholdBelow?: boolean
}

/**
 * DTO para programación de reportes.
 */
export interface ScheduleDto extends ReportFilter {
  email: string
  frequency: 'DAILY' | 'WEEKLY'
}

/**
 * Snapshot histórico de inventario.
 */
export interface SnapshotDto {
  date: string
  totalProducts: number
  totalValue: string
}

/**
 * DTO para resumen ejecutivo (KPIs) de reporte.
 */
export interface ReportSummaryDto {
  totalSkus: number        
  totalValue: string      
  daysOfStock: number      
}
