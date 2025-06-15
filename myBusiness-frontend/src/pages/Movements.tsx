import React, { useEffect, useState, useCallback, useRef } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '@/hooks/useAuth';
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  ColumnDef,
  flexRender,
} from '@tanstack/react-table';
import Button from '@/components/ui/button';
import Modal from '@/components/ui/modal';
import MovementFilters, { MovementFilters as MF } from '@/components/MovementFilters';
import MovementForm from '@/components/MovementForm';
import {
  fetchMovementsPaginated,
  createMovement,
  updateMovement,
  deleteMovement,
  MovementsPageResponse,
  MovementOutputDto,
} from '@/api/movements';

interface PageInfo {
  pageIndex: number;
  pageSize: number;
  totalPages: number;
  totalElements: number;
}

export default function Movements() {
  const { getAuthHeader } = useAuth();
  const headers = getAuthHeader() as Record<string, string>;

  // filtros en edición y aplicados
  const [filters, setFilters] = useState<MF>({});
  const [appliedFilters, setAppliedFilters] = useState<MF>({});

  // paginación y datos
  const [pageInfo, setPageInfo] = useState<PageInfo>({
    pageIndex: 0,
    pageSize: 10,
    totalPages: 0,
    totalElements: 0,
  });
  const [data, setData] = useState<MovementOutputDto[]>([]);
  const [loading, setLoading] = useState(false);

  // modal de formulario
  const [showFormModal, setShowFormModal] = useState(false);
  const [editingMovement, setEditingMovement] = useState<MovementOutputDto | null>(null);

  // Ref para abortar peticiones anteriores si el usuario dispara varias seguidas
  const abortControllerRef = useRef<AbortController | null>(null);

  // Función para cargar página con filtros dados
  const fetchPage = useCallback(
    async (page: number, filtersToUse: MF) => {
      setLoading(true);
      // cancelar petición anterior
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
      const ac = new AbortController();
      abortControllerRef.current = ac;
      try {
        const resp: MovementsPageResponse = await fetchMovementsPaginated(
          page,
          pageInfo.pageSize,
          filtersToUse,
          headers
        );
        setData(resp.content);
        setPageInfo({
          pageIndex: resp.number,
          pageSize: resp.size,
          totalElements: resp.totalElements,
          totalPages: resp.totalPages,
        });
      } catch (err: any) {
        if (err.name === 'CanceledError' || err.message === 'canceled') {
          // petición cancelada, ignorar
        } else {
          console.error(err);
          toast.error(err.response?.data?.message || 'Error cargando movimientos');
        }
      } finally {
        setLoading(false);
      }
    },
    
    [pageInfo.pageSize]
  );

  // Carga inicial al montar
  useEffect(() => {
    fetchPage(0, appliedFilters);

  }, []);

 
  const handleApplyFilters = useCallback(() => {
    setAppliedFilters(filters);
    setPageInfo(prev => ({ ...prev, pageIndex: 0 }));
    fetchPage(0, filters);
  }, [filters, fetchPage]);

  // Columnas para react-table
  const columns = React.useMemo<ColumnDef<MovementOutputDto>[]>(
    () => [
      { accessorKey: 'id', header: 'ID' },
      {
        accessorKey: 'productName',
        header: 'Producto',
        cell: ({ getValue }) => String(getValue()),
      },
      {
        accessorKey: 'movementType',
        header: 'Tipo',
      },
      {
        accessorKey: 'quantity',
        header: 'Cantidad',
      },
      {
        accessorKey: 'reason',
        header: 'Motivo',
      },
      {
        accessorKey: 'movementDate',
        header: 'Fecha',
        cell: ({ getValue }) => new Date(getValue<string>()).toLocaleString(),
      },
      {
        accessorKey: 'createdBy',
        header: 'Usuario',
      },
      {
        id: 'actions',
        header: 'Acciones',
        cell: ({ row }) => (
          <div className="space-x-2">
            <Button
              size="sm"
              onClick={() => {
                setEditingMovement(row.original);
                setShowFormModal(true);
              }}
            >
              Editar
            </Button>
            <Button
              size="sm"
              variant="destructive"
              onClick={async () => {
                if (!confirm('¿Seguro que deseas eliminar este movimiento?')) return;
                try {
                  await deleteMovement(row.original.id!, headers);
                  toast.success('Movimiento eliminado');
                  // recargar la misma página con filtros aplicados
                  fetchPage(pageInfo.pageIndex, appliedFilters);
                } catch (err) {
                  console.error(err);
                  toast.error('No se pudo eliminar movimiento');
                }
              }}
            >
              Eliminar
            </Button>
          </div>
        ),
      },
    ],
    [headers, pageInfo.pageIndex, appliedFilters, fetchPage]
  );

  const table = useReactTable({
    data,
    columns,
    state: {},
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
  });

  // Paginación manual
  const handlePrevPage = () => {
    if (pageInfo.pageIndex > 0) {
      const newPage = pageInfo.pageIndex - 1;
      setPageInfo(prev => ({ ...prev, pageIndex: newPage }));
      fetchPage(newPage, appliedFilters);
    }
  };
  const handleNextPage = () => {
    if (pageInfo.pageIndex + 1 < pageInfo.totalPages) {
      const newPage = pageInfo.pageIndex + 1;
      setPageInfo(prev => ({ ...prev, pageIndex: newPage }));
      fetchPage(newPage, appliedFilters);
    }
  };

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-semibold">Movimientos</h1>

      {/* Filtros */}
      <MovementFilters
        filters={filters}
        onChange={setFilters}
        onApply={handleApplyFilters}
      />

      {/* Botón registrar */}
      <div className="mb-4">
        <Button
          onClick={() => {
            setEditingMovement(null);
            setShowFormModal(true);
          }}
        >
          + Registrar Movimiento
        </Button>
      </div>

      {/* Tabla */}
      <div className="overflow-x-auto bg-white shadow rounded-lg">
        <table className="min-w-full table-auto border-collapse">
          <thead className="bg-gray-100">
            {table.getHeaderGroups().map(hg => (
              <tr key={hg.id}>
                {hg.headers.map(header => (
                  <th
                    key={header.id}
                    className="p-2 border text-left font-medium"
                  >
                    {flexRender(header.column.columnDef.header, header.getContext())}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length} className="p-4 text-center">Cargando...</td>
              </tr>
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="p-4 text-center">No hay movimientos.</td>
              </tr>
            ) : (
              table.getRowModel().rows.map(row => (
                <tr key={row.id} className="hover:bg-gray-50">
                  {row.getVisibleCells().map(cell => (
                    <td key={cell.id} className="p-2 border">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Paginación */}
      <div className="flex justify-between items-center mt-4">
        <span>
          Página {pageInfo.pageIndex + 1} de {pageInfo.totalPages} ({pageInfo.totalElements} registros)
        </span>
        <div className="space-x-2">
          <Button
            disabled={pageInfo.pageIndex === 0 || loading}
            onClick={handlePrevPage}
          >
            Anterior
          </Button>
          <Button
            disabled={pageInfo.pageIndex + 1 >= pageInfo.totalPages || loading}
            onClick={handleNextPage}
          >
            Siguiente
          </Button>
        </div>
      </div>

      {/* Modal de formulario */}
      {showFormModal && (
        <Modal onClose={() => setShowFormModal(false)}>
          <h2 className="text-xl font-semibold mb-4">
            {editingMovement ? 'Editar Movimiento' : 'Registrar Movimiento'}
          </h2>
          <MovementForm
            initialData={editingMovement ?? undefined}
            onSaved={(saved) => {
              setShowFormModal(false);
              // recargar con appliedFilters en la misma página
              fetchPage(pageInfo.pageIndex, appliedFilters);
            }}
            onCancel={() => setShowFormModal(false)}
          />
        </Modal>
      )}
    </div>
  );
}
