// src/api/alerts.ts
import axios from 'axios'
import { axiosWithAuth } from './axiosClient';

const API_BASE = import.meta.env.VITE_API_URL
  ? import.meta.env.VITE_API_URL.replace(/\/$/, '') + '/api'
  : '/api';

export interface AlertOutputDto {
  id: number;
  productId: number;
  productName: string;
  movementId?: number;
  alertType: string;
  currentStock: number;    // Nuevo campo: stock actual del producto
  thresholdMin: number;
  thresholdMax: number;
  createdDate: string;
}

export interface CriticalAlertOutputDto {
  id: number;
  productId: number;
  productName: string;
  alertType: string;
  currentStock: number;
  thresholdMin: number;
  thresholdMax: number;
}

export async function fetchAlertsHistory(
  headers: Record<string, string>
): Promise<AlertOutputDto[]> {
  const resp = await axios.get<AlertOutputDto[]>(
    `${API_BASE}/alerts`,
    { headers }
  )
  return resp.data
}

export async function deleteAlert(
  id: number,
  headers: Record<string, string>
): Promise<void> {
  const client = axiosWithAuth()
  await client.delete(`/alerts/${id}`, { headers })
}

export async function fetchCriticalAlerts(
  headers: Record<string, string>
): Promise<CriticalAlertOutputDto[]> {
  const resp = await axios.get<CriticalAlertOutputDto[]>(
    `${API_BASE}/alerts/critical`,
    { headers }
  )
  return resp.data
}

export async function markAlertRead(
  id: number,
  headers: Record<string, string>
): Promise<void> {
  const client = axiosWithAuth()
  await client.post(
    `/alerts/${id}/read`,
    null,
    { headers }
  )
}

export async function fetchUnreadAlerts(
  headers: Record<string, string>
): Promise<AlertOutputDto[]> {
  const resp = await axios.get<AlertOutputDto[]>(
    `${API_BASE}/alerts/unread`,
    { headers }
  )
  return resp.data
}
