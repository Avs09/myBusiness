// src/components/UnitForm.tsx

import React, { useState } from 'react'
import toast from 'react-hot-toast'
import { createUnit } from '@/api/units'
import Button from '@/components/ui/button'
import Input from '@/components/ui/input'

interface UnitFormProps {
  onClose: () => void
  onCreated: () => void
}

export default function UnitForm({ onClose, onCreated }: UnitFormProps) {
  const [name, setName] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) {
      toast.error('El nombre no puede estar vacío')
      return
    }
    setLoading(true)
    try {
      // Nota: createUnit ya usa axiosWithAuth internamente
      await createUnit({ name: name.trim() })
      toast.success('Unidad creada')
      onCreated()
      onClose()
    } catch (err: any) {
      console.error(err)
      toast.error(err.response?.data?.message || 'No se pudo crear la unidad')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium mb-1">Nombre de Unidad</label>
        <Input
          value={name}
          onChange={e => setName(e.target.value)}
          placeholder="Ej. Kilogramo"
          className="w-full"
        />
      </div>
      <div className="flex justify-end space-x-2">
        <Button variant="secondary" onClick={onClose} type="button">
          Cancelar
        </Button>
        <Button type="submit" disabled={loading}>
          {loading ? 'Guardando...' : 'Crear'}
        </Button>
      </div>
    </form>
  )
}
