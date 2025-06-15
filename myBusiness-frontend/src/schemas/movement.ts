// src/schemas/movement.ts

/**
 * DTO para creación/edición de movimiento.
 */
export interface MovementInputDto {
  productId: number;
  movementType: 'ENTRY' | 'EXIT' | 'ADJUSTMENT';
  quantity: number;
  movementDate?: string; 
  reason: string;
}

/**
 * DTO que devuelve el backend al crear/leer movimiento.
 */
export interface MovementOutputDto {
  id: number;
  productId: number;
  productName: string;
  movementType: 'ENTRY' | 'EXIT' | 'ADJUSTMENT';
  quantity: number;
  reason: string;
  movementDate: string;   
  createdBy: string;
  createdDate: string;   
  modifiedBy?: string;
  modifiedDate?: string;
  stockBefore?: number;
  stockAfter?: number;
}
