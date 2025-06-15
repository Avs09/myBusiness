// src/schemas/common.ts
export interface PageResponseDto<T> {
  content: T[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
}
