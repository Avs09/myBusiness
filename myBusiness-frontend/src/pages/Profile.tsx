// src/pages/Profile.tsx
import React, { useState, useEffect } from 'react'
import { useAuth } from '@/hooks/useAuth'
import {
  User,
  Mail,
  Calendar,
  Shield,
  Edit3,
  Save,
  X,
  Camera,
  MapPin,
  Phone,
  Building,
  FileText,
  Globe,
  Hash
} from 'lucide-react'
import Button from '@/components/ui/button'
import toast from 'react-hot-toast'
import { getBusiness, createBusiness, updateBusiness, BusinessInput, BusinessOutput } from '@/api/business'

export default function Profile() {
  const { user, getAuthHeader } = useAuth()
  const [isEditing, setIsEditing] = useState(false)
  const [business, setBusiness] = useState<BusinessOutput | null>(null)
  const [businessLoading, setBusinessLoading] = useState(true)
  const [businessEditing, setBusinessEditing] = useState(false)
  const [formData, setFormData] = useState({
    name: '',
    email: user?.email || '',
    phone: '',
    location: '',
    bio: ''
  })
  const [businessFormData, setBusinessFormData] = useState<BusinessInput>({
    name: '',
    nit: '',
    description: '',
    address: '',
    phone: '',
    email: '',
    website: '',
    logoUrl: '',
    industry: ''
  })

  // Fetch user profile data
  const fetchProfile = async () => {
    try {
      const response = await fetch('/api/users/profile', {
        method: 'GET',
        headers: getAuthHeader()
      })

      if (response.ok) {
        const profileData = await response.json()
        setFormData({
          name: profileData.name || '',
          email: user?.email || '',
          phone: profileData.phone || '',
          location: profileData.location || '',
          bio: profileData.bio || ''
        })
      }
    } catch (error) {
      console.error('Error fetching profile:', error)
    }
  }

  // Load profile data on component mount
  useEffect(() => {
    fetchProfile()
  }, [])

  const handleSave = async () => {
    try {
      // Call the API to update user profile
      const response = await fetch('/api/users/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          ...getAuthHeader()
        },
        body: JSON.stringify({
          name: formData.name,
          phone: formData.phone,
          location: formData.location,
          bio: formData.bio
        })
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`)
      }

      const result = await response.json()
      toast.success(result.message || 'Perfil actualizado correctamente')
      setIsEditing(false)
      // Refresh profile data
      await fetchProfile()
    } catch (error: any) {
      console.error('Error updating profile:', error)
      toast.error(error.message || 'Error actualizando el perfil')
    }
  }

  const handleCancel = () => {
    setFormData({
      name: '',
      email: user?.email || '',
      phone: '',
      location: '',
      bio: ''
    })
    setIsEditing(false)
  }

  // Load business data on component mount
  useEffect(() => {
    const loadBusiness = async () => {
      try {
        const businessData = await getBusiness()
        setBusiness(businessData)
        setBusinessFormData({
          name: businessData.name,
          nit: businessData.nit,
          description: businessData.description || '',
          address: businessData.address || '',
          phone: businessData.phone || '',
          email: businessData.email || '',
          website: businessData.website || '',
          logoUrl: businessData.logoUrl || '',
          industry: businessData.industry || ''
        })
      } catch (error) {
        // Business not found, that's okay for new users
        console.log('No business found for user')
      } finally {
        setBusinessLoading(false)
      }
    }

    loadBusiness()
  }, [])

  const handleBusinessSave = async () => {
    try {
      if (business) {
        // Update existing business
        const updatedBusiness = await updateBusiness(businessFormData)
        setBusiness(updatedBusiness)
        toast.success('Información del negocio actualizada correctamente')
      } else {
        // Create new business
        const newBusiness = await createBusiness(businessFormData)
        setBusiness(newBusiness)
        toast.success('Negocio creado correctamente')
      }
      setBusinessEditing(false)
    } catch (error: any) {
      toast.error(error.message || 'Error guardando información del negocio')
    }
  }

  const handleBusinessCancel = () => {
    if (business) {
      setBusinessFormData({
        name: business.name,
        nit: business.nit,
        description: business.description || '',
        address: business.address || '',
        phone: business.phone || '',
        email: business.email || '',
        website: business.website || '',
        logoUrl: business.logoUrl || '',
        industry: business.industry || ''
      })
    } else {
      setBusinessFormData({
        name: '',
        nit: '',
        description: '',
        address: '',
        phone: '',
        email: '',
        website: '',
        logoUrl: '',
        industry: ''
      })
    }
    setBusinessEditing(false)
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="bg-white shadow-sm rounded-lg p-6 mb-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Mi Perfil</h1>
              <p className="text-gray-600 mt-1">Gestiona tu información personal</p>
            </div>
            {!isEditing && (
              <Button
                onClick={() => setIsEditing(true)}
                className="flex items-center space-x-2"
              >
                <Edit3 className="w-4 h-4" />
                <span>Editar Perfil</span>
              </Button>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Profile Card */}
          <div className="lg:col-span-1">
            <div className="bg-white shadow-sm rounded-lg p-6">
              <div className="text-center">
                <div className="relative inline-block">
                  <div className="w-24 h-24 bg-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                    <User className="w-12 h-12 text-white" />
                  </div>
                  {isEditing && (
                    <button className="absolute bottom-0 right-0 bg-blue-600 text-white p-2 rounded-full hover:bg-blue-700 transition-colors">
                      <Camera className="w-4 h-4" />
                    </button>
                  )}
                </div>
                <h2 className="text-xl font-semibold text-gray-900">
                  {formData.name || 'Administrador'}
                </h2>
                <p className="text-gray-600">{formData.email}</p>
                <div className="mt-4 flex items-center justify-center space-x-2 text-sm text-gray-500">
                  <Shield className="w-4 h-4" />
                  <span>Usuario Verificado</span>
                </div>
              </div>
            </div>
          </div>

          {/* Profile Details */}
          <div className="lg:col-span-2">
            <div className="bg-white shadow-sm rounded-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-6">
                Información Personal
              </h3>

              <div className="space-y-6">
                {/* Name */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Nombre Completo
                    </label>
                    {isEditing ? (
                      <div className="relative">
                        <User className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                        <input
                          type="text"
                          value={formData.name}
                          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                          className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="Ingresa tu nombre"
                        />
                      </div>
                    ) : (
                      <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                        <User className="w-4 h-4 text-gray-400" />
                        <span className="text-gray-900">{formData.name || 'No especificado'}</span>
                      </div>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Correo Electrónico
                    </label>
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Mail className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{formData.email}</span>
                    </div>
                  </div>
                </div>

                {/* Phone */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Teléfono
                  </label>
                  {isEditing ? (
                    <div className="relative">
                      <Phone className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <input
                        type="tel"
                        value={formData.phone}
                        onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                        className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Ingresa tu teléfono"
                      />
                    </div>
                  ) : (
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Phone className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{formData.phone || 'No especificado'}</span>
                    </div>
                  )}
                </div>

                {/* Location */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Ubicación
                  </label>
                  {isEditing ? (
                    <div className="relative">
                      <MapPin className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <input
                        type="text"
                        value={formData.location}
                        onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                        className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Ciudad, País"
                      />
                    </div>
                  ) : (
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <MapPin className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{formData.location || 'No especificado'}</span>
                    </div>
                  )}
                </div>

                {/* Bio */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Biografía
                  </label>
                  {isEditing ? (
                    <textarea
                      value={formData.bio}
                      onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                      rows={4}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                      placeholder="Cuéntanos un poco sobre ti..."
                    />
                  ) : (
                    <div className="p-3 bg-gray-50 rounded-md min-h-[100px]">
                      <p className="text-gray-900 whitespace-pre-wrap">
                        {formData.bio || 'No hay biografía disponible.'}
                      </p>
                    </div>
                  )}
                </div>

                {/* Account Info */}
                <div className="border-t pt-6">
                  <h4 className="text-sm font-medium text-gray-700 mb-4">Información de la Cuenta</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Calendar className="w-4 h-4 text-gray-400" />
                      <div>
                        <p className="text-xs text-gray-500">Miembro desde</p>
                        <p className="text-sm text-gray-900">Enero 2024</p>
                      </div>
                    </div>
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Shield className="w-4 h-4 text-gray-400" />
                      <div>
                        <p className="text-xs text-gray-500">Estado</p>
                        <p className="text-sm text-green-600">Verificado</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              {isEditing && (
                <div className="flex justify-end space-x-3 mt-8 pt-6 border-t">
                  <Button
                    variant="secondary"
                    onClick={handleCancel}
                    className="flex items-center space-x-2"
                  >
                    <X className="w-4 h-4" />
                    <span>Cancelar</span>
                  </Button>
                  <Button
                    onClick={handleSave}
                    className="flex items-center space-x-2"
                  >
                    <Save className="w-4 h-4" />
                    <span>Guardar Cambios</span>
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Business Information Section */}
        <div className="bg-white shadow-sm rounded-lg p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-lg font-semibold text-gray-900">Información del Negocio</h3>
              <p className="text-gray-600 mt-1">Gestiona los datos de tu empresa</p>
            </div>
            {!businessEditing && (
              <Button
                onClick={() => setBusinessEditing(true)}
                className="flex items-center space-x-2"
                variant={business ? "secondary" : "primary"}
              >
                <Edit3 className="w-4 h-4" />
                <span>{business ? 'Editar Negocio' : 'Crear Negocio'}</span>
              </Button>
            )}
          </div>

          {businessLoading ? (
            <div className="text-center py-8">
              <p className="text-gray-500">Cargando información del negocio...</p>
            </div>
          ) : (
            <div className="space-y-6">
              {/* Business Name and NIT */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Nombre del Negocio *
                  </label>
                  {businessEditing ? (
                    <div className="relative">
                      <Building className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <input
                        type="text"
                        value={businessFormData.name}
                        onChange={(e) => setBusinessFormData({ ...businessFormData, name: e.target.value })}
                        className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Nombre de tu negocio"
                        required
                      />
                    </div>
                  ) : (
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Building className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{business?.name || 'No configurado'}</span>
                    </div>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    NIT *
                  </label>
                  {businessEditing ? (
                    <div className="relative">
                      <Hash className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <input
                        type="text"
                        value={businessFormData.nit}
                        onChange={(e) => setBusinessFormData({ ...businessFormData, nit: e.target.value })}
                        className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="NIT de tu negocio"
                        required
                      />
                    </div>
                  ) : (
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Hash className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{business?.nit || 'No configurado'}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Industry and Website */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Industria
                  </label>
                  {businessEditing ? (
                    <input
                      type="text"
                      value={businessFormData.industry}
                      onChange={(e) => setBusinessFormData({ ...businessFormData, industry: e.target.value })}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="Ej: Tecnología, Comercio, Servicios"
                    />
                  ) : (
                    <div className="p-3 bg-gray-50 rounded-md">
                      <span className="text-gray-900">{business?.industry || 'No especificada'}</span>
                    </div>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Sitio Web
                  </label>
                  {businessEditing ? (
                    <div className="relative">
                      <Globe className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <input
                        type="url"
                        value={businessFormData.website}
                        onChange={(e) => setBusinessFormData({ ...businessFormData, website: e.target.value })}
                        className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="https://www.tunegocio.com"
                      />
                    </div>
                  ) : (
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Globe className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">
                        {business?.website ? (
                          <a href={business.website} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">
                            {business.website}
                          </a>
                        ) : 'No configurado'}
                      </span>
                    </div>
                  )}
                </div>
              </div>

              {/* Address and Phone */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Dirección
                  </label>
                  {businessEditing ? (
                    <div className="relative">
                      <MapPin className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <input
                        type="text"
                        value={businessFormData.address}
                        onChange={(e) => setBusinessFormData({ ...businessFormData, address: e.target.value })}
                        className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Dirección de tu negocio"
                      />
                    </div>
                  ) : (
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <MapPin className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{business?.address || 'No configurada'}</span>
                    </div>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Teléfono
                  </label>
                  {businessEditing ? (
                    <div className="relative">
                      <Phone className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <input
                        type="tel"
                        value={businessFormData.phone}
                        onChange={(e) => setBusinessFormData({ ...businessFormData, phone: e.target.value })}
                        className="pl-10 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="Teléfono de contacto"
                      />
                    </div>
                  ) : (
                    <div className="flex items-center space-x-2 p-3 bg-gray-50 rounded-md">
                      <Phone className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{business?.phone || 'No configurado'}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Descripción
                </label>
                {businessEditing ? (
                  <textarea
                    value={businessFormData.description}
                    onChange={(e) => setBusinessFormData({ ...businessFormData, description: e.target.value })}
                    rows={3}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                    placeholder="Describe tu negocio..."
                  />
                ) : (
                  <div className="p-3 bg-gray-50 rounded-md min-h-[80px]">
                    <p className="text-gray-900 whitespace-pre-wrap">
                      {business?.description || 'No hay descripción disponible.'}
                    </p>
                  </div>
                )}
              </div>

              {/* Business Action Buttons */}
              {businessEditing && (
                <div className="flex justify-end space-x-3 mt-8 pt-6 border-t">
                  <Button
                    variant="secondary"
                    onClick={handleBusinessCancel}
                    className="flex items-center space-x-2"
                  >
                    <X className="w-4 h-4" />
                    <span>Cancelar</span>
                  </Button>
                  <Button
                    onClick={handleBusinessSave}
                    className="flex items-center space-x-2"
                  >
                    <Save className="w-4 h-4" />
                    <span>{business ? 'Actualizar Negocio' : 'Crear Negocio'}</span>
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}