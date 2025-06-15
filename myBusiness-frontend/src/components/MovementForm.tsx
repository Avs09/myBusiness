import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import toast from 'react-hot-toast';
import Button from './ui/button';
import Input from './ui/input';
import Select from './ui/select';
import type { MovementInputDto, MovementOutputDto } from '@/schemas/movement';
import { createMovement, updateMovement } from '@/api/movements';
import { fetchProductById, fetchAllProducts } from '@/api/products';
import type { ProductOutput } from '@/api/products';
import { useAuth } from '@/hooks/useAuth';

interface MovementFormProps {
  initialData?: MovementOutputDto;
  onSaved: (saved: MovementOutputDto) => void;
  onCancel: () => void;
}

interface FormValues {
  productId: number;
  movementType: 'ENTRY' | 'EXIT' | 'ADJUSTMENT';
  quantity: number;
  movementDate: Date;
  reason: string;
}

export default function MovementForm({ initialData, onSaved, onCancel }: MovementFormProps) {
  const isEdit = !!initialData;
  const { register, handleSubmit, setValue, watch, formState: { errors, isSubmitting } } = useForm<FormValues>({
    defaultValues: {
      productId: initialData?.productId ?? 0,
      movementType: initialData?.movementType ?? 'ENTRY',
      quantity: initialData?.quantity ?? 0,
      movementDate: initialData?.movementDate ? new Date(initialData.movementDate) : new Date(),
      reason: initialData?.reason ?? '',
    }
  });
  const watchProductId = watch('productId');
  const watchQuantity = watch('quantity');
  const watchType = watch('movementType');

  const { getAuthHeader } = useAuth();
  const headers = getAuthHeader() as Record<string, string>;

 
  const [products, setProducts] = useState<ProductOutput[]>([]);
  const [loadingProducts, setLoadingProducts] = useState(false);
  const [productsError, setProductsError] = useState<string | null>(null);

  // Detalle del producto seleccionado
  const [productDetail, setProductDetail] = useState<ProductOutput | null>(null);
  const [stockBefore, setStockBefore] = useState<number | null>(null);
  const [stockAfter, setStockAfter] = useState<number | null>(null);

  // Cargar lista de productos al montar (solo una vez)
  useEffect(() => {
    setLoadingProducts(true);
    const hdr = getAuthHeader() as Record<string, string>;
    fetchAllProducts(hdr)
      .then(list => {
        setProducts(list);
        setProductsError(null);
      })
      .catch(err => {
        console.error('Error cargando lista de productos en MovementForm:', err);
        setProducts([]);
        setProductsError('No se pudieron cargar productos');
      })
      .finally(() => setLoadingProducts(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [getAuthHeader]);

  
  useEffect(() => {
    if (watchProductId && watchProductId > 0) {
      const hdr = getAuthHeader() as Record<string, string>;
      fetchProductById(watchProductId, hdr)
        .then(prod => {
          setProductDetail(prod);
          // Si el DTO incluye currentStock, lo tomamos
          if ((prod as any).currentStock != null) {
            setStockBefore(Number((prod as any).currentStock));
          } else {
            setStockBefore(null);
          }
          setStockAfter(null);
        })
        .catch(err => {
          console.error('Error cargando detalle de producto:', err);
          toast.error('No se pudo cargar detalle de producto');
          setProductDetail(null);
          setStockBefore(null);
          setStockAfter(null);
        });
    } else {
      setProductDetail(null);
      setStockBefore(null);
      setStockAfter(null);
    }

  }, [watchProductId]);


  useEffect(() => {
    if (stockBefore != null && watchQuantity != null && watchQuantity >= 0) {
      if (watchType === 'ENTRY') {
        setStockAfter(stockBefore + watchQuantity);
      } else if (watchType === 'EXIT') {
        setStockAfter(stockBefore - watchQuantity);
      } else if (watchType === 'ADJUSTMENT') {
        // Asumimos ajuste como suma; ajustar según lógica de negocio
        setStockAfter(stockBefore + watchQuantity);
      } else {
        setStockAfter(null);
      }
    } else {
      setStockAfter(null);
    }
  }, [stockBefore, watchQuantity, watchType]);

  const onSubmit = async (vals: FormValues) => {
    if (!vals.productId || vals.productId <= 0) {
      toast.error('Selecciona un producto válido');
      return;
    }
    if (vals.quantity <= 0) {
      toast.error('Cantidad debe ser mayor que cero');
      return;
    }

    if (watchType === 'EXIT' && stockBefore != null && vals.quantity > stockBefore) {
      toast.error(`Stock insuficiente (disponible ${stockBefore})`);
      return;
    }
    const dto: MovementInputDto = {
      productId: vals.productId,
      movementType: vals.movementType,
      quantity: vals.quantity,
      movementDate: vals.movementDate.toISOString(),
      reason: vals.reason,
    };
    try {
      let saved: MovementOutputDto;
      if (isEdit && initialData) {
        saved = await updateMovement(initialData.id, dto, headers);
      } else {
        saved = await createMovement(dto, headers);
      }
      toast.success(isEdit ? 'Movimiento actualizado' : 'Movimiento registrado');
      onSaved(saved);
    } catch (err: any) {
      console.error('Error guardando movimiento:', err);
      toast.error(err.response?.data?.message || 'No se pudo guardar movimiento');
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {/* Producto */}
      <div>
        <label className="block text-sm font-medium mb-1">Producto</label>
        {loadingProducts ? (
          <p className="text-sm">Cargando productos...</p>
        ) : productsError ? (
          <p className="text-red-600 text-sm">{productsError}</p>
        ) : (
          <Select
            {...register('productId', {
              required: 'Producto obligatorio',
              valueAsNumber: true,
              validate: v => v > 0 || 'Selecciona un producto'
            })}
            className="w-full"
          >
            <option value={0}>-- Selecciona producto --</option>
            {products.map(p => (
              <option key={p.id} value={p.id}>
                {p.name} (ID: {p.id})
              </option>
            ))}
          </Select>
        )}
        {errors.productId && <p className="text-red-600 text-xs">{errors.productId.message}</p>}
      </div>

      {/* Detalle de producto si disponible */}
      {productDetail ? (
        <div className="bg-gray-50 p-2 rounded space-y-1">
          <p className="text-sm font-medium">Detalle del producto:</p>
          <p className="text-sm">Nombre: {productDetail.name}</p>
          <p className="text-sm">Precio unitario: {productDetail.price}</p>
          {'thresholdMin' in productDetail && (
            <p className="text-sm">Umbral Mínimo: {(productDetail as any).thresholdMin}</p>
          )}
          {'thresholdMax' in productDetail && (
            <p className="text-sm">Umbral Máximo: {(productDetail as any).thresholdMax}</p>
          )}
          {stockBefore != null && (
            <p className="text-sm">Stock actual: {stockBefore}</p>
          )}
          {stockAfter != null && (
            <p className="text-sm">Stock tras operación: {stockAfter}</p>
          )}
        </div>
      ) : null}

      {/* Tipo de movimiento */}
      <div>
        <label className="block text-sm font-medium mb-1">Tipo de movimiento</label>
        <Select {...register('movementType')} className="w-full">
          <option value="ENTRY">Entrada</option>
          <option value="EXIT">Salida</option>
          <option value="ADJUSTMENT">Ajuste</option>
        </Select>
      </div>

      {/* Cantidad */}
      <div>
        <label className="block text-sm font-medium mb-1">Cantidad</label>
        <Input
          type="number"
          step="any"
          {...register('quantity', {
            required: 'Cantidad obligatoria',
            valueAsNumber: true,
            min: { value: 0.0000001, message: 'Cantidad debe ser > 0' },
          })}
          className="w-full"
        />
        {errors.quantity && <p className="text-red-600 text-xs">{errors.quantity.message}</p>}
      </div>

      {/* Fecha de movimiento */}
      <div>
        <label className="block text-sm font-medium mb-1">Fecha y hora</label>
        <DatePicker
          selected={watch('movementDate')}
          onChange={(d) => setValue('movementDate', d ?? new Date())}
          showTimeSelect
          dateFormat="yyyy-MM-dd HH:mm"
          className="w-full border rounded px-2 py-1"
        />
      </div>

      {/* Motivo / descripción */}
      <div>
        <label className="block text-sm font-medium mb-1">Motivo / Descripción</label>
        <textarea
          {...register('reason', {
            required: 'Motivo obligatorio',
            maxLength: { value: 255, message: 'Máximo 255 caracteres' },
          })}
          className="w-full border rounded px-2 py-1"
          rows={3}
        />
        {errors.reason && <p className="text-red-600 text-xs">{errors.reason.message}</p>}
      </div>

      {/* Botones */}
      <div className="flex justify-end space-x-2">
        <Button variant="secondary" type="button" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isEdit ? 'Actualizar' : 'Registrar'}
        </Button>
      </div>
    </form>
  );
}
