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
  const [previewData, setPreviewData] = useState<ProductInput[]>([])
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

  const buildPreview = (data: ProductInput[]) => {
    const preview: ProductInput[] = []
    const rowErrors: typeof errorsData = []
    data.forEach((row, idx) => {
      const errs: string[] = []
      if (!row.name) errs.push('Falta nombre')
      if (isNaN(row.price) || row.price <= 0) errs.push('Precio inválido')
      if (isNaN(row.thresholdMin) || row.thresholdMin < 0) errs.push('UmbralMin inválido')
      if (isNaN(row.thresholdMax) || row.thresholdMax < row.thresholdMin) errs.push('UmbralMax inválido')
      if (!row.categoryId) errs.push('Categoría inválida')
      if (!row.unitId) errs.push('Unidad inválida')
      if (errs.length) rowErrors.push({ row: idx + 2, errors: errs })
      preview.push(row)
    })
    setPreviewData(preview)
    setErrorsData(rowErrors)
    setShowImportModal(true)
  }

  const confirmImport = async () => {
    const headers = getAuthHeader() as Record<string,string>
    let success = 0, fail = 0
    for (let i = 0; i < previewData.length; i++) {
      if (errorsData.some(e => e.row === i + 2)) { fail++; continue }
      try {
        await createProduct(previewData[i])
        success++
      } catch {
        fail++
      }
    }
    toast.success(`Importados: ${success}, Fallidos: ${fail}`)
    setShowImportModal(false)
    loadPage(pageInfo.pageNumber)
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
                <td className="p-2 border">{p.price}</td>
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
                  <th className="border px-2">CatId</th>
                  <th className="border px-2">UnitId</th>
                </tr>
              </thead>
              <tbody>
                {previewData.map((r,i) => (
                  <tr key={i} className={errorsData.some(e => e.row === i+2) ? 'bg-red-50' : ''}>
                    <td className="border px-2">{i+2}</td>
                    <td className="border px-2">{r.name}</td>
                    <td className="border px-2">{r.price}</td>
                    <td className="border px-2">{r.categoryId}</td>
                    <td className="border px-2">{r.unitId}</td>
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
