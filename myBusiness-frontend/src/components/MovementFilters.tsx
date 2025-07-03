import React, { useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import Button from './ui/button';
import Select from './ui/select';
import Input from './ui/input';
import { useAuth } from '@/hooks/useAuth';
import { fetchAllProducts } from '@/api/products';  // GET /api/products/all

/**
 * Filtros para Movimientos.
 */
export interface MovementFilters {
  productId?: number;
  dateFrom?: string; // “YYYY-MM-DD”
  dateTo?: string;   // “YYYY-MM-DD”
  movementType?: 'ENTRY' | 'EXIT' | 'ADJUSTMENT';
  search?: string;
}

interface ProductOption {
  id: number;
  name: string;
}

interface MovementFiltersProps {
  filters: MovementFilters;
  onChange: (f: MovementFilters) => void;
  onApply: () => void;
}

export default function MovementFilters({ filters, onChange, onApply }: MovementFiltersProps) {
  const { getAuthHeader } = useAuth();

  const [products, setProducts] = useState<ProductOption[]>([]);
  const [loadingProducts, setLoadingProducts] = useState(false);
  const [productsError, setProductsError] = useState<string | null>(null);

  useEffect(() => {
    // Ejecutar una sola vez al montar
    const headers = getAuthHeader() as Record<string, string>;
    setLoadingProducts(true);
    fetchAllProducts()
      .then(list => {
        const opts = list.map(p => ({ id: p.id, name: p.name }));
        setProducts(opts);
        setProductsError(null);
      })
      .catch(err => {
        console.error('Error cargando productos para filtros:', err);
        setProductsError('No se pudieron cargar productos');
        setProducts([]);
      })
      .finally(() => setLoadingProducts(false));

  }, [getAuthHeader]);

  return (
    <div className="bg-white shadow rounded-lg p-4 mb-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {/* Producto */}
      <div>
        <label className="block text-sm mb-1">Producto</label>
        {loadingProducts ? (
          <p className="text-sm">Cargando productos...</p>
        ) : productsError ? (
          <p className="text-red-600 text-sm">{productsError}</p>
        ) : (
          <Select
            value={filters.productId != null ? String(filters.productId) : ''}
            onChange={e => {
              const v = e.target.value;
              onChange({ ...filters, productId: v ? Number(v) : undefined });
            }}
            className="w-full"
          >
            <option value="">Todos</option>
            {products.map(prod => (
              <option key={prod.id} value={prod.id}>
                {prod.name} (ID: {prod.id})
              </option>
            ))}
          </Select>
        )}
      </div>

      {/* Fecha From */}
      <div>
        <label className="block text-sm mb-1">Desde</label>
        <DatePicker
          selected={filters.dateFrom ? new Date(filters.dateFrom) : null}
          onChange={(d) => {
            onChange({ ...filters, dateFrom: d ? d.toISOString().slice(0,10) : undefined });
          }}
          dateFormat="yyyy-MM-dd"
          className="w-full border rounded px-2 py-1"
          placeholderText="YYYY-MM-DD"
        />
      </div>

      {/* Fecha To */}
      <div>
        <label className="block text-sm mb-1">Hasta</label>
        <DatePicker
          selected={filters.dateTo ? new Date(filters.dateTo) : null}
          onChange={(d) => {
            onChange({ ...filters, dateTo: d ? d.toISOString().slice(0,10) : undefined });
          }}
          dateFormat="yyyy-MM-dd"
          className="w-full border rounded px-2 py-1"
          placeholderText="YYYY-MM-DD"
        />
      </div>

      {/* Tipo de movimiento */}
      <div>
        <label className="block text-sm mb-1">Tipo Movimiento</label>
        <Select
          value={filters.movementType ?? ''}
          onChange={e =>
            onChange({ ...filters, movementType: e.target.value ? (e.target.value as any) : undefined })
          }
          className="w-full"
        >
          <option value="">Todos</option>
          <option value="ENTRY">Entrada</option>
          <option value="EXIT">Salida</option>
          <option value="ADJUSTMENT">Ajuste</option>
        </Select>
      </div>

      {/* Búsqueda libre */}
      <div>
        <label className="block text-sm mb-1">Buscar (motivo o nombre)</label>
        <Input
          type="text"
          value={filters.search ?? ''}
          onChange={e => onChange({ ...filters, search: e.target.value || undefined })}
          placeholder="Texto libre..."
          className="w-full"
        />
      </div>

      {/* Botón aplicar */}
      <div className="col-span-full flex justify-end mt-2">
        <Button onClick={onApply}>Aplicar filtros</Button>
      </div>
    </div>
  );
}
