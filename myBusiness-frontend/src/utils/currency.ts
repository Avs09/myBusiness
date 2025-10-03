// src/utils/currency.ts
export function formatCOP(value: number | string, opts?: Intl.NumberFormatOptions): string {
  const n = typeof value === 'string' ? Number(value) : value
  if (!isFinite(n as number)) return String(value)
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
    ...opts,
  }).format(n as number)
}