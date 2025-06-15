// src/api/units.ts
import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL as string

export interface UnitDto {
  id: number
  name: string
}

// NUEVO: listado completo de unidades para filtros
export async function fetchUnits(
  headers: Record<string, string>
): Promise<UnitDto[]> {
  const url = `${BASE_URL}/units`
  const resp = await axios.get<UnitDto[]>(url, { headers })
  return resp.data
}

export interface UnitInputDto { name: string }
export async function createUnit(
  data: UnitInputDto,
  headers: Record<string,string>
): Promise<UnitDto> {
  const url = `${BASE_URL}/units`;
  const resp = await axios.post<UnitDto>(url, data, { headers });
  return resp.data;
}
