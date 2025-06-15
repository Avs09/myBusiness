// src/pages/ProductsTable.tsx

import React, { useEffect, useMemo, useState } from 'react'
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  ColumnDef,
  flexRender,
} from '@tanstack/react-table'
import toast from 'react-hot-toast'
import {
  fetchProductsPaginated,
  updateProduct,
  ProductOutput,
} from '@/api/products'
import { useAuth } from '@/hooks/useAuth'
import Button from '@/components/ui/button'
import { AlertTriangle } from 'lucide-react'
import type { ProductFilters } from '@/api/products'

interface PageInfo {
  page: number
  pageSize: number
  totalPages: number
  totalElements: number
}

interface ProductsTableProps {
  filters: ProductFilters
  onEditRow: (p: ProductOutput) => void
  onDeleteRow: (id: number) => Promise<void>
}

export default function ProductsTable({
  filters,
  onEditRow,
  onDeleteRow,
}: ProductsTableProps) {
  const { getAuthHeader } = useAuth()
  const headers = useMemo(() => getAuthHeader() as Record<string, string>, [
    getAuthHeader,
  ])

  const [data, setData] = useState<ProductOutput[]>([])
  const [pageInfo, setPageInfo] = useState<PageInfo>({
    page: 0,
    pageSize: 20,
    totalPages: 0,
    totalElements: 0,
  })
  const [loading, setLoading] = useState(false)
  const [sorting, setSorting] = useState<{ id: string; desc: boolean }[]>([])

  const fetchData = async (page: number) => {
    setLoading(true)
    try {
      const resp = await fetchProductsPaginated(
        page,
        pageInfo.pageSize,
        filters,
        headers
      )
      setData(resp.content)
      setPageInfo({
        page: resp.number,
        pageSize: resp.size,
        totalPages: resp.totalPages,
        totalElements: resp.totalElements,
      })
    } catch {
      toast.error('Error cargando productos')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData(0)
    
  }, [filters])

  const columns = useMemo<ColumnDef<ProductOutput>[]>(
    () => [
      {
        accessorKey: 'id',
        header: ({ column }) => (
          <button onClick={column.getToggleSortingHandler()}>
            ID {column.getIsSorted() === 'asc' ? '▲' : column.getIsSorted() === 'desc' ? '▼' : ''}
          </button>
        ),
      },
      {
        accessorKey: 'name',
        header: ({ column }) => (
          <button onClick={column.getToggleSortingHandler()}>
            Nombre {column.getIsSorted() === 'asc' ? '▲' : column.getIsSorted() === 'desc' ? '▼' : ''}
          </button>
        ),
      },
      {
        accessorKey: 'price',
        header: ({ column }) => (
          <button onClick={column.getToggleSortingHandler()}>
            Precio {column.getIsSorted() === 'asc' ? '▲' : column.getIsSorted() === 'desc' ? '▼' : ''}
          </button>
        ),
        cell: ({ getValue, row }) => {
          const initial = getValue<number>()
          const [val, setVal] = useState(initial)
          const onBlur = async () => {
            if (val !== initial) {
              const updated = { ...row.original, price: val }
              setData(old => old.map(p => (p.id === updated.id ? updated : p)))
              try {
                await updateProduct(
                  updated.id,
                  {
                    name: updated.name,
                    thresholdMin: updated.thresholdMin,
                    thresholdMax: updated.thresholdMax,
                    price: val,
                    categoryId: updated.categoryId,
                    unitId: updated.unitId,
                  },
                  headers
                )
                toast.success('Precio actualizado')
              } catch {
                toast.error('No se pudo actualizar precio')
                fetchData(pageInfo.page)
              }
            }
          }
          return (
            <input
              type="number"
              value={val}
              onChange={e => setVal(+e.target.value)}
              onBlur={onBlur}
              className="w-20 border rounded px-1"
            />
          )
        },
      },
      {
        accessorKey: 'currentStock',
        header: ({ column }) => (
          <button onClick={column.getToggleSortingHandler()}>
            Stock {column.getIsSorted() === 'asc' ? '▲' : column.getIsSorted() === 'desc' ? '▼' : ''}
          </button>
        ),
        cell: ({ getValue, row }) => {
          const stock = getValue<number>()
          const { thresholdMin, thresholdMax } = row.original
          const outOfBounds = stock < thresholdMin || stock > thresholdMax
          return (
            <div className="flex items-center space-x-1">
              <span>{stock}</span>
              {outOfBounds && <AlertTriangle className="text-red-500" size={16} />}
            </div>
          )
        },
      },
      {
        accessorKey: 'createdDate',
        header: ({ column }) => (
          <button onClick={column.getToggleSortingHandler()}>
            Creado {column.getIsSorted() === 'asc' ? '▲' : column.getIsSorted() === 'desc' ? '▼' : ''}
          </button>
        ),
        cell: ({ getValue }) => new Date(getValue<string>()).toLocaleDateString(),
      },
      {
        id: 'actions',
        header: 'Acciones',
        cell: ({ row }) => (
          <div className="space-x-2">
            <Button onClick={() => onEditRow(row.original)}>Editar</Button>
            <Button variant="secondary" onClick={() => onDeleteRow(row.original.id)}>
              Eliminar
            </Button>
          </div>
        ),
      },
    ],
    [headers, pageInfo.page, filters]
  )

  const table = useReactTable({
    data,
    columns,
    state: { sorting },
    onSortingChange: up => setSorting(up as any),
    manualSorting: true,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
  })

  const paginationText =
  pageInfo.totalElements === 0
    ? 'No hay productos para mostrar'
    : `Página ${pageInfo.page + 1} de ${Math.max(1, pageInfo.totalPages)} (${pageInfo.totalElements} registros)`

  return (
    <div className="mb-6">
      <table className="min-w-full table-auto border">
        <thead>
          {table.getHeaderGroups().map(hg => (
            <tr key={hg.id}>
              {hg.headers.map(header => (
                <th key={header.id} className="border px-2 py-1 text-left">
                  {flexRender(header.column.columnDef.header, header.getContext())}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map(row => (
            <tr key={row.id} className="hover:bg-gray-50">
              {row.getVisibleCells().map(cell => (
                <td key={cell.id} className="border px-2 py-1">
                  {flexRender(cell.column.columnDef.cell, cell.getContext())}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>

      {/* ── Paginación ── */}
      <div className="flex justify-between items-center mt-4">
       
       <div>{paginationText}</div>
        <div className="space-x-2">
          <Button disabled={pageInfo.page === 0 || loading} onClick={() => fetchData(pageInfo.page - 1)}>
            Anterior
          </Button>
          <Button
            disabled={pageInfo.page + 1 >= pageInfo.totalPages || loading}
            onClick={() => fetchData(pageInfo.page + 1)}
          >
            Siguiente
          </Button>
        </div>
      </div>
    </div>
  )
}
