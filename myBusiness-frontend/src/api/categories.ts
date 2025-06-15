// src/api/categories.ts
import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL as string

export interface CategorySummaryDto {
  categoryId: number
  categoryName: string
  totalStock: number
}

// Ya existente: resúmenes por categoría
export async function fetchCategorySummaries(
  headers: Record<string, string>
): Promise<CategorySummaryDto[]> {
  const url = `${BASE_URL}/categories/summary`
  const resp = await axios.get<CategorySummaryDto[]>(url, { headers })
  return resp.data
}


export interface CategoryDto {
  id: number
  name: string
}
export async function fetchCategories(
  headers: Record<string, string>
): Promise<CategoryDto[]> {
  const url = `${BASE_URL}/categories`
  const resp = await axios.get<CategoryDto[]>(url, { headers })
  return resp.data
}

export interface CategoryInputDto { name: string }
export async function createCategory(
  data: CategoryInputDto,
  headers: Record<string,string>
): Promise<CategoryDto> {
  const url = `${BASE_URL}/categories`;
  const resp = await axios.post<CategoryDto>(url, data, { headers });
  return resp.data;
}
