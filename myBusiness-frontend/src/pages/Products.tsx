// src/pages/Products.tsx
import React, { useEffect, useState, useRef } from 'react'
import toast from 'react-hot-toast'
import Papa from 'papaparse'
import { saveAs } from 'file-saver'
import * as XLSX from 'xlsx'
import {
  fetchProductsPaginated,
  createProduct,
  updateProduct,
  deleteProduct,
  ProductInput,
  ProductOutput,
} from '@/api/products'
import {
  fetchCategories,
  createCategory,
  CategoryDto
} from '@/api/categories'
import {
  fetchUnits,
  createUnit,
  UnitDto
} from '@/api/units'
import type { PageResponseDto } from '@/schemas/common'
import { useAuth } from '@/hooks/useAuth'
import Button from '@/components/ui/button'
import Modal from '@/components/ui/modal'
import ProductForm from '@/components/ProductForm'
import ProductFilters, { ProductFilters as ProductFiltersType } from '@/components/ProductFilters'
import CategoryForm from '@/components/CategoryForm'
import UnitForm from '@/components/UnitForm'
import { formatCOP } from '@/utils/currency'

// Tipos de filas aceptadas para importación (por ID o por nombre)
type ImportRow = {
  name?: string
  price?: number
  thresholdMin?: number
  thresholdMax?: number
  categoryId?: number | null
  unitId?: number | null
  categoryName?: string | null
  unitName?: string | null
}

export default function Products() {
  const { getAuthHeader } = useAuth()
  const fileInputRef = useRef<HTMLInputElement>(null)

  // filtros y paginación
  const [filters, setFilters] = useState<ProductFiltersType>({ search: '', categoryId: null, unitId: null })
  const [productos, setProductos] = useState<ProductOutput[]>([])
  const [pageInfo, setPageInfo] = useState({ pageNumber: 0, pageSize: 20, totalElements: 0, totalPages: 0 })
  const [loading, setLoading] = useState(true)

  // modales
  const [showProductModal, setShowProductModal] = useState(false)
  const [editingProduct, setEditingProduct] = useState<ProductOutput | null>(null)
  const [showCategoryModal, setShowCategoryModal] = useState(false)
  const [showUnitModal, setShowUnitModal] = useState(false)

  // importación
  const [previewData, setPreviewData] = useState<ImportRow[]>([])
  const [errorsData, setErrorsData] = useState<{ row: number; errors: string[] }[]>([])
  const [showImportModal, setShowImportModal] = useState(false)

  // Carga de página
  const loadPage = async (page = 0) => {
    setLoading(true)
    try {
      const headers = getAuthHeader() as Record<string,string>
      const raw = await fetchProductsPaginated(page, pageInfo.pageSize, filters)
      setProductos(raw.content)
      setPageInfo({
        pageNumber: raw.number,
        pageSize: raw.size,
        totalElements: raw.totalElements,
        totalPages: raw.totalPages,
      })
    } catch {
      toast.error('No se pudieron cargar los productos')
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { loadPage(0) }, [filters])

  // CRUD productos
  const handleNew = () => { setEditingProduct(null); setShowProductModal(true) }
  const handleSave = async (data: ProductInput) => {
    const headers = getAuthHeader() as Record<string,string>
    try {
      if (editingProduct) {
        await updateProduct(editingProduct.id, data)
        toast.success('Producto actualizado')
      } else {
        await createProduct(data)
        toast.success('Producto creado')
      }
      setShowProductModal(false)
      loadPage(pageInfo.pageNumber)
    } catch {
      toast.error('No se pudo guardar el producto')
    }
  }
  const handleDelete = async (id: number) => {
    if (!confirm('¿Seguro que quieres eliminar este producto?')) return
    try {
      const headers = getAuthHeader() as Record<string,string>
      await deleteProduct(id)
      toast.success('Producto eliminado')
      loadPage(pageInfo.pageNumber)
    } catch {
      toast.error('No se pudo eliminar el producto')
    }
  }

  // CRUD categorías y unidades
  const handleCreateCategory = () => setShowCategoryModal(true)
  const handleCreateUnit = () => setShowUnitModal(true)

  const handleSaveCategory = async () => {
    // onCreated callback: recarga filtros para actualizar drop-down
    setFilters({ ...filters })
  }
  const handleSaveUnit = async () => {
    setFilters({ ...filters })
  }

  // Importar CSV o XLSX (unificado)
  const onFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return
    const ext = file.name.split('.').pop()?.toLowerCase()
    let data: ProductInput[] = []
    try {
      if (ext === 'csv') {
        const text = await file.text()
        data = Papa.parse<ProductInput>(text, { header: true, skipEmptyLines: true }).data
      } else if (ext === 'xlsx') {
        const buf = await file.arrayBuffer()
        const wb = XLSX.read(buf)
        const sheet = wb.Sheets[wb.SheetNames[0]]
        data = XLSX.utils.sheet_to_json<ProductInput>(sheet)
      } else {
        toast.error('Formato no soportado')
        return
      }
      buildPreview(data)
    } catch {
      toast.error('Error al parsear el archivo')
    }
    e.target.value = ''
  }

  const buildPreview = (data: any[]) => {
    const preview: ImportRow[] = []
    const rowErrors: typeof errorsData = []

    data.forEach((raw, idx) => {
      const errs: string[] = []

      const name = (raw?.name ?? '').toString().trim()
      const price = Number(raw?.price)
      const thresholdMin = Number(raw?.thresholdMin)
      const thresholdMax = Number(raw?.thresholdMax)

      // Aceptar referencia de categoría/unidad por ID o por nombre
      const categoryIdParsed =
        raw?.categoryId !== undefined && raw?.categoryId !== ''
          ? Number(raw?.categoryId)
          : undefined
      const unitIdParsed =
        raw?.unitId !== undefined && raw?.unitId !== ''
          ? Number(raw?.unitId)
          : undefined

      const categoryNameParsed =
        raw?.categoryName !== undefined && raw?.categoryName !== null
          ? String(raw?.categoryName).trim()
          : undefined
      const unitNameParsed =
        raw?.unitName !== undefined && raw?.unitName !== null
          ? String(raw?.unitName).trim()
          : undefined

      // Validaciones mínimas (permitimos auto-creación por nombre)
      if (!name) errs.push('Falta nombre')
      if (isNaN(price) || price <= 0) errs.push('Precio inválido')
      if (isNaN(thresholdMin) || thresholdMin < 0) errs.push('UmbralMin inválido')
      if (isNaN(thresholdMax) || thresholdMax < thresholdMin) errs.push('UmbralMax inválido')

      // Debe venir al menos categoryId o categoryName
      if ((categoryIdParsed == null || isNaN(categoryIdParsed)) && !categoryNameParsed) {
        errs.push('Categoría no especificada')
      }
      // Debe venir al menos unitId o unitName
      if ((unitIdParsed == null || isNaN(unitIdParsed)) && !unitNameParsed) {
        errs.push('Unidad no especificada')
      }

      if (errs.length) rowErrors.push({ row: idx + 2, errors: errs })

      preview.push({
        name,
        price,
        thresholdMin,
        thresholdMax,
        categoryId: categoryIdParsed ?? null,
        unitId: unitIdParsed ?? null,
        categoryName: categoryNameParsed ?? null,
        unitName: unitNameParsed ?? null
      })
    })

    setPreviewData(preview)
    setErrorsData(rowErrors)
    setShowImportModal(true)
  }

  const confirmImport = async () => {
    const headers = getAuthHeader() as Record<string, string>

    try {
      // 1) Cargar cat/units existentes
      const existingCategories = await fetchCategories(headers).catch(() => []) as { id: number; name: string }[]
      const existingUnits = await fetchUnits().catch(() => []) as { id: number; name: string }[]

      const catByName = new Map<string, number>()
      const unitByName = new Map<string, number>()

      existingCategories.forEach(c => catByName.set(c.name.trim().toLowerCase(), c.id))
      existingUnits.forEach(u => unitByName.set(u.name.trim().toLowerCase(), u.id))

      // 2) Detectar nombres nuevos a crear
      const newCategoryNames = new Set<string>()
      const newUnitNames = new Set<string>()

      for (const r of previewData) {
        if (!r.categoryId && r.categoryName) {
          const key = r.categoryName.trim().toLowerCase()
          if (key && !catByName.has(key)) newCategoryNames.add(r.categoryName.trim())
        }
        if (!r.unitId && r.unitName) {
          const key = r.unitName.trim().toLowerCase()
          if (key && !unitByName.has(key)) newUnitNames.add(r.unitName.trim())
        }
      }

      // 3) Crear categorías nuevas
      for (const name of newCategoryNames) {
        try {
          const created = await createCategory({ name }, headers)
          catByName.set(created.name.trim().toLowerCase(), created.id)
        } catch (e) {
          console.error('No se pudo crear categoría', name, e)
        }
      }

      // 4) Crear unidades nuevas
      for (const name of newUnitNames) {
        try {
          const created = await createUnit({ name })
          unitByName.set(created.name.trim().toLowerCase(), created.id)
        } catch (e) {
          console.error('No se pudo crear unidad', name, e)
        }
      }

      // 5) Importar productos con IDs definitivos
      let success = 0, fail = 0
      for (let i = 0; i < previewData.length; i++) {
        // si fila tiene errores de validación, omitir
        if (errorsData.some(e => e.row === i + 2)) { fail++; continue }

        const r = previewData[i]
        const resolvedCategoryId =
          r.categoryId ??
          (r.categoryName ? catByName.get(r.categoryName.trim().toLowerCase()) : undefined)
        const resolvedUnitId =
          r.unitId ??
          (r.unitName ? unitByName.get(r.unitName.trim().toLowerCase()) : undefined)

        if (!resolvedCategoryId || !resolvedUnitId) {
          fail++
          continue
        }

        const dto: ProductInput = {
          name: r.name!,
          price: r.price!,
          thresholdMin: r.thresholdMin!,
          thresholdMax: r.thresholdMax!,
          categoryId: resolvedCategoryId,
          unitId: resolvedUnitId
        }

        try {
          await createProduct(dto)
          success++
        } catch (e) {
          console.error('No se pudo crear producto:', dto?.name, e)
          fail++
        }
      }

      toast.success(`Importados: ${success}, Fallidos: ${fail}`)
      setShowImportModal(false)
      loadPage(pageInfo.pageNumber)
    } catch (e) {
      console.error('Error general importando:', e)
      toast.error('Error general durante la importación')
    }
  }

  // Exportar Excel
  const exportExcel = () => {
    const ws = XLSX.utils.json_to_sheet(productos)
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, 'Productos')
    const buf = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    saveAs(new Blob([buf], { type: 'application/octet-stream' }), 'productos.xlsx')
  }

  return (
    <div className="p-6">
      {/* encabezado */}
      <div className="flex justify-between items-center mb-4 space-x-2">
        <h1 className="text-2xl font-semibold">Productos</h1>
        <div className="space-x-2">
          <Button onClick={handleNew}>+ Nuevo</Button>
          <Button onClick={handleCreateCategory}>+ Categoría</Button>
          <Button onClick={handleCreateUnit}>+ Unidad</Button>
          <Button onClick={() => fileInputRef.current?.click()}>Importar</Button>
          <Button variant="secondary" onClick={exportExcel}>Exportar Excel</Button>
        </div>
        <input
          ref={fileInputRef}
          type="file"
          accept=".csv,.xlsx"
          className="hidden"
          onChange={onFileChange}
        />
      </div>

      {/* filtros y búsqueda */}
      <div className="flex items-center mb-4 space-x-2">
        <ProductFilters filters={filters} onChange={setFilters} />
        <Button onClick={() => loadPage(0)}>Buscar</Button>
      </div>

      {/* tabla */}
      <div className="overflow-x-auto bg-white shadow rounded-lg">
        <table className="min-w-full table-auto border-collapse">
          <thead className="bg-gray-100">
            <tr>
              <th className="p-2 border">ID</th>
              <th className="p-2 border">Nombre</th>
              <th className="p-2 border">Categoría</th>
              <th className="p-2 border">Unidad</th>
              <th className="p-2 border">Precio</th>
              <th className="p-2 border">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {productos.map(p => (
              <tr key={p.id} className="hover:bg-gray-50">
                <td className="p-2 border">{p.id}</td>
                <td className="p-2 border">{p.name}</td>
                <td className="p-2 border">{p.categoryName}</td>
                <td className="p-2 border">{p.unitName}</td>
                <td className="p-2 border">{formatCOP(Number(p.price))}</td>
                <td className="p-2 border space-x-1">
                  <Button size="sm" onClick={() => { setEditingProduct(p); setShowProductModal(true) }}>Editar</Button>
                  <Button size="sm" variant="destructive" onClick={() => handleDelete(p.id)}>Eliminar</Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Paginación */}
      <div className="flex justify-between items-center mt-4">
              {(() => {
         // Asegurarnos de mostrar al menos 1
         const current = pageInfo.pageNumber >= 0 ? pageInfo.pageNumber + 1 : 1
         const total   = pageInfo.totalPages  > 0 ? pageInfo.totalPages   : 1
         return (
           <span>
             Página {current} de {total} ({pageInfo.totalElements} registros)
           </span>
         )
       })()}
      </div>

      {/* modales */}
      {showProductModal && (
        <Modal onClose={() => setShowProductModal(false)}>
          <ProductForm initialData={editingProduct} onSave={handleSave} onCancel={() => setShowProductModal(false)} />
        </Modal>
      )}
      {showCategoryModal && (
        <Modal onClose={() => setShowCategoryModal(false)}>
          <CategoryForm onClose={() => setShowCategoryModal(false)} onCreated={handleSaveCategory} />
        </Modal>
      )}
      {showUnitModal && (
        <Modal onClose={() => setShowUnitModal(false)}>
          <UnitForm onClose={() => setShowUnitModal(false)} onCreated={handleSaveUnit} />
        </Modal>
      )}
      {showImportModal && (
        <Modal onClose={() => setShowImportModal(false)}>
          <h2 className="text-xl mb-4">Vista previa de importación</h2>
          {errorsData.length > 0 && (
            <div className="mb-4 text-red-600">
              <strong>Errores encontrados:</strong>
              <ul className="list-disc ml-5">
                {errorsData.map(e => <li key={e.row}>Fila {e.row}: {e.errors.join(', ')}</li>)}
              </ul>
            </div>
          )}
          <div className="max-h-64 overflow-auto mb-4">
            <table className="w-full table-auto border-collapse">
              <thead className="bg-gray-100">
                <tr>
                  <th className="border px-2">Fila</th>
                  <th className="border px-2">Nombre</th>
                  <th className="border px-2">Precio</th>
                  <th className="border px-2">Categoría</th>
                  <th className="border px-2">Unidad</th>
                </tr>
              </thead>
              <tbody>
                {previewData.map((r,i) => (
                  <tr key={i} className={errorsData.some(e => e.row === i+2) ? 'bg-red-50' : ''}>
                    <td className="border px-2">{i+2}</td>
                    <td className="border px-2">{r.name}</td>
                    <td className="border px-2">{r.price}</td>
                    <td className="border px-2">{r.categoryName ?? r.categoryId}</td>
                    <td className="border px-2">{r.unitName ?? r.unitId}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="flex justify-end space-x-2">
            <Button variant="secondary" onClick={() => setShowImportModal(false)}>Cancelar</Button>
            <Button onClick={confirmImport} disabled={errorsData.length > 0}>Confirmar Importación</Button>
          </div>
        </Modal>
      )}
    </div>
  )
}
