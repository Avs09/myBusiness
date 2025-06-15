import axios from 'axios';
import { axiosWithAuth } from './axiosClient';

export interface NotificationOutputDto {
  id: number;
  message: string;
  createdDate: string;  // ISO
  isRead: boolean;
}

/**
 * Trae todas las notificaciones no leídas (GET /api/notifications)
 */
export async function fetchUnreadNotifications(): Promise<NotificationOutputDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<NotificationOutputDto[]>('/notifications');
  return resp.data;
}

/**
 * Marca una notificación como leída (PUT /api/notifications/{id}/read)
 */
export async function markNotificationAsRead(id: number): Promise<void> {
  const client = axiosWithAuth();
  await client.put(`/notifications/${id}/read`);
}
