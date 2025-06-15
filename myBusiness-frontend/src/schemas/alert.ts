export interface AlertInputDto {
  productId: number;
  alertType: 'UNDERSTOCK' | 'OVERSTOCK';
}

export interface AlertOutputDto {
  id: number;
  productId: number;
  alertType: string;
  triggeredAt: string;
  isRead: boolean;
  createdBy: string;
  createdDate: string;
  modifiedBy?: string;
  modifiedDate?: string;
}
