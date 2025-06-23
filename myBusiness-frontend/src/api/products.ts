// src/api/products.ts
import { axiosWithAuth } from './axiosClient';

export interface ProductInput {
  name: string;
  thresholdMin: number;
  thresholdMax: number;
  price: number;
  categoryId: number;
  unitId: number;
}

export interface ProductOutput {
  id: number;
  name: string;
  thresholdMin: number;
  thresholdMax: number;
  price: number;
  categoryId: number;
  unitId: number;
  categoryName: string;
  unitName: string;
  createdDate: string;
  createdBy: string | null;
  modifiedDate: string | null;
  modifiedBy: string | null;
}

export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ProductFilters {
  search?: string;
  categoryId?: number | null;
  unitId?: number | null;
}

export async function fetchProductsPaginated(
  page: number,
  size: number,
  filters: ProductFilters
): Promise<PageResponse<ProductOutput>> {
  const client = axiosWithAuth();
  const params: Record<string, any> = { page, size };
  if (filters.search) params.name = filters.search;
  if (filters.categoryId != null) params.categoryId = filters.categoryId;
  if (filters.unitId != null) params.unitId = filters.unitId;

  const resp = await client.get<PageResponse<ProductOutput>>('/products', { params });
  return resp.data;
}

export async function fetchAllProducts(): Promise<ProductOutput[]> {
  const client = axiosWithAuth();
  const resp = await client.get<ProductOutput[]>('/products/all');
  return resp.data;
}

export async function fetchProductById(id: number): Promise<ProductOutput> {
  const client = axiosWithAuth();
  const resp = await client.get<ProductOutput>(`/products/${id}`);
  return resp.data;
}

export async function createProduct(
  data: ProductInput
): Promise<ProductOutput> {
  const client = axiosWithAuth();
  const resp = await client.post<ProductOutput>('/products', data);
  return resp.data;
}

export async function updateProduct(
  id: number,
  data: ProductInput
): Promise<ProductOutput> {
  const client = axiosWithAuth();
  const resp = await client.put<ProductOutput>(`/products/${id}`, data);
  return resp.data;
}

export async function deleteProduct(id: number): Promise<void> {
  const client = axiosWithAuth();
  await client.delete(`/products/${id}`);
}

export async function importProductsExcel(
  file: File
): Promise<void> {
  const client = axiosWithAuth();
  const form = new FormData();
  form.append('file', file);
  await client.post('/products/import/excel', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}
