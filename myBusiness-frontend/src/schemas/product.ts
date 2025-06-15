// src/schemas/product.ts


export interface ProductOutputDto {
  id: number;
  name: string;
  thresholdMin: number;
  thresholdMax: number;
  price: string; 
  categoryName: string;
  unitName: string;
  createdDate: string; 
  createdBy: string | null;
  modifiedDate: string | null;
  modifiedBy: string | null;
}
