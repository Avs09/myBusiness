// src/api/categories.ts
import axios from 'axios';
import { axiosWithAuth } from './axiosClient';

const API_BASE = import.meta.env.VITE_API_URL
  ? import.meta.env.VITE_API_URL.replace(/\/$/, '') + '/api'
  : '/api';

export interface CategorySummaryDto {
  categoryId: number;
  categoryName: string;
  totalStock: number;
}

export async function fetchCategorySummaries(
  headers: Record<string, string>
): Promise<CategorySummaryDto[]> {
  const resp = await axios.get<CategorySummaryDto[]>(`${API_BASE}/categories/summary`, { headers });
  return resp.data;
}

export interface CategoryDto {
  id: number;
  name: string;
}

export async function fetchCategories(
  headers: Record<string, string>
): Promise<CategoryDto[]> {
  const resp = await axios.get<CategoryDto[]>(`${API_BASE}/categories`, { headers });
  return resp.data;
}

export interface CategoryInputDto { name: string; }
export async function createCategory(
  data: CategoryInputDto,
  headers: Record<string, string>
): Promise<CategoryDto> {
  const client = axiosWithAuth();
  const resp = await client.post<CategoryDto>('/categories', data, { headers });
  return resp.data;
}
