// src/components/ProductImport.tsx
import React, { useState, useEffect } from 'react'
import Papa from 'papaparse'
import toast from 'react-hot-toast'
import { fetchCategories, CategoryDto } from '@/api/categories'
import { fetchUnits, UnitDto } from '@/api/units'
import Button from '@/components/ui/button'
import type { ProductInput } from '@/api/products'

interface RowPreview {
  row: number
  data: {
    name?: string
    price?: number
    thresholdMin?: number
    thresholdMax?: number
    categoryName?: string
    unitName?: string
  }
  errors: string[]
}

export default function ProductImport() {
  // Estado para lista de categorías y unidades
  const [categories, setCategories] = useState<CategoryDto[]>([])
  const [units, setUnits] = useState<UnitDto[]>([])

  // Estado de la vista previa del CSV
  const [preview, setPreview] = useState<RowPreview[]>([])
  const [hasErrors, setHasErrors] = useState(true)

  // 1) Cargar categorías y unidades al montar
  useEffect(() => {
    const headers = {} 
    fetchCategories(headers)
      .then(setCategories)
      .catch(() => toast.error('Error cargando categorías'))
    fetchUnits()
      .then(setUnits)
      .catch(() => toast.error('Error cargando unidades'))
  }, [])

  // 2) Al seleccionar archivo CSV
  const handleFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    Papa.parse(file, {
      header: true,
      skipEmptyLines: true,
      complete: (result) => {
        const rows: RowPreview[] = (result.data as any[]).map((raw, idx) => {
          const rowNum = idx + 2
          const name = raw.name?.trim()
          const price = parseFloat(raw.price)
          const thresholdMin = parseInt(raw.thresholdMin, 10)
          const thresholdMax = parseInt(raw.thresholdMax, 10)
          const categoryName = raw.categoryName?.trim()
          const unitName = raw.unitName?.trim()

          const errors: string[] = []
          if (!name) errors.push('Nombre vacío')
          if (isNaN(price) || price <= 0) errors.push('Precio inválido')
          if (isNaN(thresholdMin) || thresholdMin < 0) errors.push('UmbralMin inválido')
          if (isNaN(thresholdMax) || thresholdMax < thresholdMin) errors.push('UmbralMax inválido')
          if (!categoryName || !categories.find(c => c.name === categoryName))
            errors.push('Categoría inválida')
          if (!unitName || !units.find(u => u.name === unitName))
            errors.push('Unidad inválida')

          return {
            row: rowNum,
            data: { name, price, thresholdMin, thresholdMax, categoryName, unitName },
            errors
          }
        })

        setPreview(rows)
        setHasErrors(rows.some(r => r.errors.length > 0))
      },
      error: (err) => {
        console.error(err)
        toast.error('Error parseando CSV')
      }
    })
  }

  // 3) Confirmar importación (solo si no hay errores)
  const handleImport = () => {
    const toImport: ProductInput[] = preview.map(r => {
      const { name, price, thresholdMin, thresholdMax, categoryName, unitName } = r.data
      const category = categories.find(c => c.name === categoryName)
      const unit = units.find(u => u.name === unitName)
      return {
        name: name!,
        price: price!,
        thresholdMin: thresholdMin!,
        thresholdMax: thresholdMax!,
        categoryId: category!.id,
        unitId: unit!.id
      }
    })


    console.log('Importar estos productos:', toImport)
    toast.success(`Lista de importación preparada (${toImport.length} líneas)`)
  }

  return (
    <div className="p-6 space-y-4">
      <h2 className="text-xl font-semibold">Importar Productos CSV</h2>

      <input
        type="file"
        accept=".csv"
        onChange={handleFile}
        className="border p-2"
      />

      {preview.length > 0 && (
        <div className="overflow-auto max-h-64">
          <table className="w-full table-auto border-collapse">
            <thead>
              <tr className="bg-gray-100">
                <th className="p-2 border">Fila</th>
                <th className="p-2 border">Nombre</th>
                <th className="p-2 border">Precio</th>
                <th className="p-2 border">UmbralMin</th>
                <th className="p-2 border">UmbralMax</th>
                <th className="p-2 border">Categoría</th>
                <th className="p-2 border">Unidad</th>
                <th className="p-2 border">Errores</th>
              </tr>
            </thead>
            <tbody>
              {preview.map(r => (
                <tr
                  key={r.row}
                  className={r.errors.length ? 'bg-red-50' : ''}
                >
                  <td className="p-2 border">{r.row}</td>
                  <td className="p-2 border">{r.data.name}</td>
                  <td className="p-2 border">{r.data.price}</td>
                  <td className="p-2 border">{r.data.thresholdMin}</td>
                  <td className="p-2 border">{r.data.thresholdMax}</td>
                  <td className="p-2 border">{r.data.categoryName}</td>
                  <td className="p-2 border">{r.data.unitName}</td>
                  <td className="p-2 border">
                    {r.errors.length > 0
                      ? r.errors.join(', ')
                      : <span className="text-green-600">OK</span>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {preview.length > 0 && (
        <div className="flex justify-end space-x-2">
          <Button
            onClick={handleImport}
            disabled={hasErrors}
          >
            Confirmar Importación
          </Button>
          <Button
            variant="secondary"
            onClick={() => { setPreview([]); setHasErrors(true) }}
          >
            Cancelar
          </Button>
        </div>
      )}
    </div>
  )
}
