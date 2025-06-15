// src/components/ReportFilters.tsx
import React, { useEffect, useState } from 'react'
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css'
import { fetchCategories } from '@/api/categories'
import { fetchUnits } from '@/api/units'
import Button from './ui/button'
import Select from './ui/select'

import { useAuth } from '@/hooks/useAuth'

export interface ReportFilter {
  productId?: number
  categoryId?: number
  unitId?: number
  dateFrom?: string
  dateTo?: string
  movementType?: 'ENTRY' | 'EXIT' | 'ADJUSTMENT'
  thresholdBelow?: boolean
}

interface ReportFiltersProps {
  filters: ReportFilter
  onChange: (f: ReportFilter) => void
  onApply: () => void
}

export default function ReportFilters({ filters, onChange, onApply }: ReportFiltersProps) {
  const { getAuthHeader } = useAuth()
  const headers = getAuthHeader() as Record<string,string>
  const [categories, setCategories] = useState<{ id: number; name: string }[]>([])
  const [units, setUnits] = useState<{ id: number; name: string }[]>([])

  useEffect(() => {
    fetchCategories(headers).then(setCategories)
    fetchUnits(headers).then(setUnits)
  }, [])

  return (
    <div className="bg-white shadow rounded-lg p-4 mb-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {/* Fecha From */}
      <div>
        <label className="block text-sm mb-1">Desde</label>
        <DatePicker
          selected={filters.dateFrom ? new Date(filters.dateFrom) : null}
          onChange={(d) => onChange({ ...filters, dateFrom: d ? d.toISOString().slice(0,10) : undefined })}
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
          onChange={(d) => onChange({ ...filters, dateTo: d ? d.toISOString().slice(0,10) : undefined })}
          dateFormat="yyyy-MM-dd"
          className="w-full border rounded px-2 py-1"
          placeholderText="YYYY-MM-DD"
        />
      </div>

      {/* Categoría */}
      <div>
        <label className="block text-sm mb-1">Categoría</label>
        <Select
          value={filters.categoryId ?? ''}
          onChange={e => onChange({ ...filters, categoryId: e.target.value ? +e.target.value : undefined })}
        >
          <option value="">Todas</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </Select>
      </div>

      {/* Unidad */}
      <div>
        <label className="block text-sm mb-1">Unidad</label>
        <Select
          value={filters.unitId ?? ''}
          onChange={e => onChange({ ...filters, unitId: e.target.value ? +e.target.value : undefined })}
        >
          <option value="">Todas</option>
          {units.map(u => <option key={u.id} value={u.id}>{u.name}</option>)}
        </Select>
      </div>

      {/* Tipo de movimiento */}
      <div>
        <label className="block text-sm mb-1">Tipo Movimiento</label>
        <Select
          value={filters.movementType ?? ''}
          onChange={e => onChange({ ...filters, movementType: e.target.value as any })}
        >
          <option value="">Todos</option>
          <option value="ENTRY">Entrada</option>
          <option value="EXIT">Salida</option>
          <option value="ADJUSTMENT">Ajuste</option>
        </Select>
      </div>

      {/* Umbral por debajo */}
      <div className="flex items-center space-x-2 mt-2">
        <input
          id="thresholdBelow"
          type="checkbox"
          checked={!!filters.thresholdBelow}
          onChange={e => onChange({ ...filters, thresholdBelow: e.target.checked })}
          className="h-4 w-4"
        />
        <label htmlFor="thresholdBelow" className="text-sm">Mostrar solo bajo umbral</label>
      </div>

      {/* Botón aplicar */}
      <div className="col-span-full flex justify-end">
        <Button onClick={onApply}>Aplicar filtros</Button>
      </div>
    </div>
  )
}
