import React, { useState } from 'react';
import { createCategory } from '@/api/categories';
import Button from '@/components/ui/button';
import Input from '@/components/ui/input';
import toast from 'react-hot-toast';
import Modal from '@/components/ui/modal';

interface Props { open: boolean; onClose: () => void; onCreated: (cat: { id: number; name: string }) => void }
export default function CategoryModal({ open, onClose, onCreated }: Props) {
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSave = async () => {
    if (!name.trim()) return toast.error('Nombre obligatorio');
    setLoading(true);
    try {
      const headers = {};
      const newCat = await createCategory({ name: name.trim() }, headers);
      toast.success('Categoría creada');
      onCreated(newCat);
      setName('');
      onClose();
    } catch (e: any) {
      toast.error(e.response?.data?.message || 'Error creando categoría');
    } finally { setLoading(false) }
  };

  if (!open) return null;
  return (
    <Modal onClose={onClose}>
      <h2 className="text-xl font-semibold mb-4">Nueva Categoría</h2>
      <Input
        placeholder="Nombre de la categoría"
        value={name}
        onChange={e => setName(e.target.value)}
        disabled={loading}
      />
      <div className="mt-4 flex justify-end space-x-2">
        <Button variant="secondary" onClick={onClose}>Cancelar</Button>
        <Button onClick={handleSave} disabled={loading}>Crear</Button>
      </div>
    </Modal>
  );
}