// src/api/products.ts
import axios from 'axios'
import { axiosWithAuth } from './axiosClient'


const BASE_URL = import.meta.env.VITE_API_URL as string

// -------------- Tipos para frontend --------------

// Datos para crear o editar un producto
export interface ProductInput {
  name: string
  thresholdMin: number
  thresholdMax: number
  price: number
  categoryId: number
  unitId: number
}

// Lo que devuelve el backend al leer/crear/editar un producto
export interface ProductOutput {
  id: number
  name: string
  thresholdMin: number
  thresholdMax: number
  price: number
  categoryId: number 
  unitId: number     
  categoryName: string
  unitName: string
  createdDate: string   // ISO string
  createdBy: string | null
  modifiedDate: string | null
  modifiedBy: string | null
}

// -------------- Tipos paginación Spring Data --------------
export interface PageResponse<T> {
  content: T[]
  number: number        // página actual (0-based)
  size: number          // tamaño de página
  totalElements: number
  totalPages: number
}

// -------------- Funciones HTTP --------------
/** Filtros para el listado paginado */
export interface ProductFilters {
  search?: string
  categoryId?: number | null
  unitId?: number | null
}

export async function fetchProductsPaginated(
  page: number,
  size: number,
  filters: ProductFilters,
  headers: Record<string,string>
): Promise<PageResponse<ProductOutput>> {
  const params: Record<string, any> = { page, size }
  if (filters.search)              params.name       = filters.search
  if (filters.categoryId != null)  params.categoryId = filters.categoryId
  if (filters.unitId     != null)  params.unitId     = filters.unitId

  const resp = await axios.get<PageResponse<ProductOutput>>(
    `${BASE_URL}/products`,
    { headers, params }
  )
  return resp.data
}

// GET ALL: /api/products/all
export async function fetchAllProducts(
  headers: Record<string,string>
): Promise<ProductOutput[]> {
  const url = `${BASE_URL}/products/all`
  const resp = await axios.get<ProductOutput[]>(url, { headers })
  return resp.data
}

// GET /api/products/{id}
export async function fetchProductById(
  id: number,
  headers: Record<string,string>
): Promise<ProductOutput> {
  const client = axiosWithAuth()
  const resp = await client.get<ProductOutput>(`${BASE_URL}/products/${id}`, { headers })
  return resp.data
}

// POST /api/products
export async function createProduct(
  data: ProductInput,
  headers: Record<string,string>
): Promise<ProductOutput> {
  const client = axiosWithAuth()
  const resp = await client.post<ProductOutput>(`${BASE_URL}/products`, data, { headers })
  return resp.data
}

// PUT /api/products/{id}
export async function updateProduct(
  id: number,
  data: ProductInput,
  headers: Record<string,string>
): Promise<ProductOutput> {
  const client = axiosWithAuth()
  const resp = await client.put<ProductOutput>(`${BASE_URL}/products/${id}`, data, { headers })
  return resp.data
}

// DELETE /api/products/{id}
export async function deleteProduct(
  id: number,
  headers: Record<string,string>
): Promise<void> {
  const client = axiosWithAuth()
  await client.delete<void>(`${BASE_URL}/products/${id}`, { headers })
}

/** Importar productos desde un XLSX (multipart) */
export async function importProductsExcel(
  file: File,
  headers: Record<string,string>
): Promise<void> {
  const client = axiosWithAuth()
  const form = new FormData();
  form.append('file', file);
  await client.post(`${BASE_URL}/products/import/excel`, form, {
    headers: { ...headers, 'Content-Type': 'multipart/form-data' }
  });
}
