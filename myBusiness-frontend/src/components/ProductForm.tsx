// src/components/ProductForm.tsx
import React, { useEffect, useState } from 'react'
import { useForm, Controller } from 'react-hook-form'
import toast from 'react-hot-toast'
import { useAuth } from '@/hooks/useAuth'
import type { ProductInput, ProductOutput } from '@/api/products'
import { fetchCategories } from '@/api/categories'
import { fetchUnits } from '@/api/units'
import Button from '@/components/ui/button'
import Input from '@/components/ui/input'
import Select from '@/components/ui/select'

interface Option { id: number; name: string }

interface ProductFormProps {
  initialData: ProductOutput | null
  onSave: (data: ProductInput) => Promise<void>
  onCancel: () => void
}

export default function ProductForm({
  initialData,
  onSave,
  onCancel,
}: ProductFormProps) {
  const { getAuthHeader } = useAuth()
  const headers = getAuthHeader() as Record<string, string>

  const [categories, setCategories] = useState<Option[]>([])
  const [units, setUnits] = useState<Option[]>([])
  const [step, setStep] = useState(0)
  const steps = ['Datos básicos', 'Stock & Umbrales', 'Categoría / Unidad']

  const {
    register,
    control,
    handleSubmit,
    reset,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<ProductInput>({
    defaultValues: {
      name: initialData?.name ?? '',
      price: initialData?.price ?? 0,
      thresholdMin: initialData?.thresholdMin ?? 0,
      thresholdMax: initialData?.thresholdMax ?? 0,
      categoryId: initialData?.categoryId ?? 0,
      unitId: initialData?.unitId ?? 0,
    },
  })

  const formValues = watch()

  // Cargo categorías y unidades
  useEffect(() => {
    fetchCategories(headers)
      .then(data => {
        setCategories(data)
        if (data.length === 0) {
          toast.error('Primero debes crear al menos una categoría')
        }
      })
      .catch(() => toast.error('No se pudieron cargar categorías'))

    fetchUnits()
      .then(data => {
        setUnits(data)
        if (data.length === 0) {
          toast.error('Primero debes crear al menos una unidad')
        }
      })
      .catch(() => toast.error('No se pudieron cargar unidades'))
  }, [])

  // Si cambian los datos iniciales, reseteo todo
  useEffect(() => {
    reset({
      name: initialData?.name ?? '',
      price: initialData?.price ?? 0,
      thresholdMin: initialData?.thresholdMin ?? 0,
      thresholdMax: initialData?.thresholdMax ?? 0,
      categoryId: initialData?.categoryId ?? 0,
      unitId: initialData?.unitId ?? 0,
    })
    setStep(0)
  }, [initialData])

  const canNext = () => {
    switch (step) {
      case 0:
        return !errors.name && !errors.price && !!formValues.name && formValues.price > 0
      case 1:
        return !errors.thresholdMin && !errors.thresholdMax
      case 2:
        // además validar que existan categorías y unidades
        if (categories.length === 0 || units.length === 0) return false
        return !errors.categoryId && !errors.unitId && formValues.categoryId > 0 && formValues.unitId > 0
      default:
        return false
    }
  }

  // Al pulsar “Guardar” en el último paso
  const onFinalSave = handleSubmit(async data => {
    try {
      await onSave(data)
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Error guardando producto')
    }
  })

  return (
    <div className="space-y-6">
      {/* Pasos */}
      <div className="flex space-x-4">
        {steps.map((label, i) => (
          <div
            key={i}
            className={`flex-1 text-center py-2 border-b-2 ${
              i === step ? 'border-blue-600 font-semibold' : 'border-gray-300 text-gray-500'
            }`}
          >
            {label}
          </div>
        ))}
      </div>

      {/* Contenido de cada paso */}
      {step === 0 && (
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Nombre</label>
            <Input
              {...register('name', {
                required: 'El nombre es obligatorio',
                maxLength: { value: 100, message: 'Máximo 100 caracteres' },
              })}
              className="w-full"
            />
            {errors.name && <p className="text-red-600 text-xs">{errors.name.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Precio</label>
            <Input
              type="number"
              step="0.01"
              {...register('price', {
                valueAsNumber: true,
                required: 'El precio es obligatorio',
                min: { value: 0.01, message: 'El precio debe ser mayor a 0' },
              })}
              className="w-full"
            />
            {errors.price && <p className="text-red-600 text-xs">{errors.price.message}</p>}
          </div>
        </div>
      )}

      {step === 1 && (
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Umbral Mínimo</label>
            <Input
              type="number"
              {...register('thresholdMin', {
                valueAsNumber: true,
                required: 'Umbral mínimo obligatorio',
                min: { value: 0, message: 'No puede ser negativo' },
              })}
              className="w-full"
            />
            {errors.thresholdMin && <p className="text-red-600 text-xs">{errors.thresholdMin.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Umbral Máximo</label>
            <Input
              type="number"
              {...register('thresholdMax', {
                valueAsNumber: true,
                required: 'Umbral máximo obligatorio',
                min: {
                  value: formValues.thresholdMin,
                  message: 'Debe ser ≥ umbral mínimo',
                },
              })}
              className="w-full"
            />
            {errors.thresholdMax && <p className="text-red-600 text-xs">{errors.thresholdMax.message}</p>}
          </div>
        </div>
      )}

      {step === 2 && (
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Categoría</label>
            <Controller
              control={control}
              name="categoryId"
              rules={{ min: { value: 1, message: 'Selecciona una categoría' } }}
              render={({ field }) => (
                <Select {...field} className="w-full">
                  <option value={0}>-- Seleccione --</option>
                  {categories.map(c => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </Select>
              )}
            />
            {errors.categoryId && <p className="text-red-600 text-xs">{errors.categoryId.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Unidad</label>
            <Controller
              control={control}
              name="unitId"
              rules={{ min: { value: 1, message: 'Selecciona una unidad' } }}
              render={({ field }) => (
                <Select {...field} className="w-full">
                  <option value={0}>-- Seleccione --</option>
                  {units.map(u => (
                    <option key={u.id} value={u.id}>{u.name}</option>
                  ))}
                </Select>
              )}
            />
            {errors.unitId && <p className="text-red-600 text-xs">{errors.unitId.message}</p>}
          </div>
          {(categories.length === 0 || units.length === 0) && (
            <p className="text-red-600 text-sm">
              Debes crear primero una categoría y una unidad antes de registrar un producto.
            </p>
          )}
        </div>
      )}

      {/* Botones */}
      <div className="flex justify-between mt-6">
        <Button
          type="button"
          variant="secondary"
          onClick={step > 0 ? () => setStep(step - 1) : onCancel}
        >
          {step > 0 ? 'Atrás' : 'Cancelar'}
        </Button>

        {step < steps.length - 1 ? (
          <Button
            type="button"
            disabled={!canNext()}
            onClick={() => canNext() && setStep(step + 1)}
          >
            Siguiente
          </Button>
        ) : (
          <Button
            type="button"
            disabled={isSubmitting || !canNext()}
            onClick={onFinalSave}
          >
            {initialData ? 'Actualizar' : 'Crear'}
          </Button>
        )}
      </div>
    </div>
  )
}
