// src/api/units.ts
import { axiosWithAuth } from './axiosClient';

export interface UnitDto {
  id: number;
  name: string;
}

export async function fetchUnits(): Promise<UnitDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<UnitDto[]>('/units');
  return resp.data;
}

export interface UnitInputDto { name: string; }
export async function createUnit(
  data: UnitInputDto
): Promise<UnitDto> {
  const client = axiosWithAuth();
  const resp = await client.post<UnitDto>('/units', data);
  return resp.data;
}
