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
   // Solo cargar si no tenemos datos ya cargados
   if (categories.length === 0 && units.length === 0) {
     console.log('🚀 ProductFilters: Iniciando carga de datos...')

     // Flag para prevenir múltiples ejecuciones
     let isLoading = false

     const loadData = async () => {
       if (isLoading) {
         console.log('⏳ ProductFilters: Ya se está cargando, omitiendo...')
         return
       }

       isLoading = true
       const headers = getAuthHeader() as Record<string, string>

       try {
         const [categoriesData, unitsData] = await Promise.all([
           fetchCategories(headers).catch(() => {
             console.error('❌ ProductFilters: Error cargando categorías')
             return []
           }),
           fetchUnits().catch(() => {
             console.error('❌ ProductFilters: Error cargando unidades')
             return []
           })
         ])

         console.log('✅ ProductFilters: Datos obtenidos:', { categories: categoriesData.length, units: unitsData.length })

         // Usar callback para asegurar actualización correcta del estado
         setCategories(categoriesData)
         setUnits(unitsData)

       } catch (error) {
         console.error('❌ ProductFilters: Error general:', error)
       } finally {
         isLoading = false
       }
     }

     loadData()
   } else {
     console.log('⏭️ ProductFilters: Datos ya disponibles, no se requieren llamadas API')
   }
 }, []) // Remover dependencias para evitar re-ejecuciones

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
