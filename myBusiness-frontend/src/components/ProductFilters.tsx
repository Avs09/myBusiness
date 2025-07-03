// src/components/ProductFilters.tsx
import React, { useEffect, useState } from 'react'
import { useAuth } from '@/hooks/useAuth'
import toast from 'react-hot-toast'
import { fetchCategories } from '@/api/categories'
import { fetchUnits } from '@/api/units'

export interface ProductFilters {
  search: string
  categoryId: number | null
  unitId: number | null
}

interface ProductFiltersProps {
  filters: ProductFilters
  onChange: (newFilters: ProductFilters) => void
}

export default function ProductFilters({ filters, onChange }: ProductFiltersProps) {
  const { getAuthHeader } = useAuth()

  const [categories, setCategories] = useState<{ id: number; name: string }[]>([])
  const [units, setUnits] = useState<{ id: number; name: string }[]>([])

  useEffect(() => {
    
    const headers = getAuthHeader() as Record<string, string>

    fetchCategories(headers)
      .then(setCategories)
      .catch(err => {
        console.error('Error cargando categorías:', err)
        toast.error('No se pudieron cargar categorías')
      })

    fetchUnits()
      .then(setUnits)
      .catch(err => {
        console.error('Error cargando unidades:', err)
        toast.error('No se pudieron cargar unidades')
      })
  }, [getAuthHeader])

  const update = (partial: Partial<ProductFilters>) => {
    onChange({ ...filters, ...partial })
  }

  return (
    <div className="flex flex-wrap items-end gap-4 mb-4">
      {/* Búsqueda libre */}
      <div>
        <label className="block text-sm">Buscar</label>
        <input
          type="text"
          value={filters.search}
          onChange={e => update({ search: e.target.value })}
          placeholder="Nombre de producto..."
          className="border rounded px-3 py-1"
        />
      </div>

      {/* Filtro Categoría */}
      <div>
        <label className="block text-sm">Categoría</label>
        <select
          value={filters.categoryId ?? ''}
          onChange={e => update({ categoryId: e.target.value ? +e.target.value : null })}
          className="border rounded px-3 py-1"
        >
          <option value="">Todas</option>
          {categories.map(c => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
      </div>

      {/* Filtro Unidad */}
      <div>
        <label className="block text-sm">Unidad</label>
        <select
          value={filters.unitId ?? ''}
          onChange={e => update({ unitId: e.target.value ? +e.target.value : null })}
          className="border rounded px-3 py-1"
        >
          <option value="">Todas</option>
          {units.map(u => (
            <option key={u.id} value={u.id}>{u.name}</option>
          ))}
        </select>
      </div>
    </div>
  )
}
