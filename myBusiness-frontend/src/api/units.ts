// src/api/units.ts

import { axiosWithAuth } from './axiosClient';

export interface UnitDto {
  id: number;
  name: string;
}

export interface UnitInputDto {
  name: string;
}

/**
 * Obtiene todas las unidades.
 */
export async function fetchUnits(): Promise<UnitDto[]> {
  const client = axiosWithAuth();
  const resp = await client.get<UnitDto[]>('/units');
  return resp.data;
}

/**
 * Crea una nueva unidad.
 * @param data Objeto con la propiedad `name` de la unidad a crear.
 */
export async function createUnit(
  data: UnitInputDto
): Promise<UnitDto> {
  const client = axiosWithAuth();
  const resp = await client.post<UnitDto>('/units', data);
  return resp.data;
}
