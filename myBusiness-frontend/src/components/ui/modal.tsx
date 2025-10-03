// src/components/Modal.tsx
import React, { ReactNode } from 'react'
import { X as XIcon } from 'lucide-react'

interface ModalProps {
  onClose: () => void
  children: ReactNode
  className?: string
}

export default function Modal({ onClose, children, className = '' }: ModalProps) {
  return (
    <div className="fixed inset-0 bg-black/30 flex justify-center items-center z-50">
      <div className={`bg-white rounded-lg shadow-lg w-full max-w-lg p-6 relative ${className}`}>
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-gray-800"
        >
          <XIcon size={20} />
        </button>
        {children}
      </div>
    </div>
  )
}
