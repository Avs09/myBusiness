// src/api/alerts.ts
import axios from 'axios'
import { axiosWithAuth } from './axiosClient'

const BASE_URL = import.meta.env.VITE_API_URL as string

export interface AlertOutputDto {
  id: number
  productId: number
  productName: string
  movementId?: number
  alertType: string    
  createdDate: string
  thresholdMin: number
  thresholdMax: number
}

export interface CriticalAlertOutputDto {
  id: number
  productId: number
  productName: string
  alertType: string
  currentStock: number
  thresholdMin: number
  thresholdMax: number
}

export async function fetchAlertsHistory(
  headers: Record<string, string>
): Promise<AlertOutputDto[]> {
  const resp = await axios.get<AlertOutputDto[]>(`${BASE_URL}/alerts`, { headers })
  return resp.data
}

export async function deleteAlert(
  id: number,
  headers: Record<string,string>
): Promise<void> {
  const client = axiosWithAuth()
  await client.delete(`${BASE_URL}/alerts/${id}`, { headers })
}

export async function fetchCriticalAlerts(
  headers: Record<string,string>
): Promise<CriticalAlertOutputDto[]> {
  const resp = await axios.get<CriticalAlertOutputDto[]>(`${BASE_URL}/alerts/critical`, { headers })
  return resp.data
}

export async function markAlertRead(
  id: number,
  headers: Record<string,string>
): Promise<void> {
  const client = axiosWithAuth()
  await client.post(`${BASE_URL}/alerts/${id}/read`, null, { headers })
}
