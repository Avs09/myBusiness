// src/api/dashboard.ts

import axios from 'axios'
import type { DashboardMetricsDto } from '@/schemas/dashboard'


export async function fetchDashboardMetrics(
  headers: Record<string, string>
): Promise<DashboardMetricsDto> {
 
  const url = `${import.meta.env.VITE_API_URL}/dashboard/metrics`
  const response = await axios.get<DashboardMetricsDto>(url, { headers })
  return response.data
}
