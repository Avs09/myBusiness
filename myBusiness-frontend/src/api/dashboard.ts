// src/api/dashboard.ts
import axios from 'axios';
import { axiosWithAuth } from './axiosClient';
import type { DashboardMetricsDto } from '@/schemas/dashboard';

const API_PATH = import.meta.env.VITE_API_URL
  ? import.meta.env.VITE_API_URL.replace(/\/$/, '') + '/api'
  : '/api';

export async function fetchDashboardMetrics(): Promise<DashboardMetricsDto> {
  // Usamos axiosWithAuth para incluir Authorization
  const client = axiosWithAuth();
  // Llamada a /api/dashboard/metrics
  const resp = await client.get<DashboardMetricsDto>('/dashboard/metrics');
  return resp.data;
}
