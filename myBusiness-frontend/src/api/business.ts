// src/api/business.ts

const API_BASE = import.meta.env.VITE_API_URL
  ? import.meta.env.VITE_API_URL.replace(/\/$/, '')
  : ''; // si vacío, fetch('/api/...') usará ruta relativa

// Utilidad para parsear error
async function parseError(resp: Response): Promise<string> {
  const text = await resp.text();
  if (!text) return 'Error desconocido';
  try {
    const obj = JSON.parse(text);
    return obj.message || JSON.stringify(obj);
  } catch {
    return text;
  }
}

export interface BusinessInput {
  name: string;
  nit: string;
  description?: string;
  address?: string;
  phone?: string;
  email?: string;
  website?: string;
  logoUrl?: string;
  industry?: string;
}

export interface BusinessOutput {
  id: number;
  name: string;
  nit: string;
  description?: string;
  address?: string;
  phone?: string;
  email?: string;
  website?: string;
  logoUrl?: string;
  industry?: string;
  createdAt: string;
  updatedAt: string;
}

export async function getBusiness(): Promise<BusinessOutput> {
  const url = `${API_BASE}/api/business`;
  const resp = await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    },
  });

  if (!resp.ok) {
    const msg = await parseError(resp);
    throw new Error(msg);
  }

  return resp.json();
}

export async function createBusiness(data: BusinessInput): Promise<BusinessOutput> {
  const url = `${API_BASE}/api/business`;
  const resp = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    },
    body: JSON.stringify(data),
  });

  if (!resp.ok) {
    const msg = await parseError(resp);
    throw new Error(msg);
  }

  return resp.json();
}

export async function updateBusiness(data: BusinessInput): Promise<BusinessOutput> {
  const url = `${API_BASE}/api/business`;
  const resp = await fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    },
    body: JSON.stringify(data),
  });

  if (!resp.ok) {
    const msg = await parseError(resp);
    throw new Error(msg);
  }

  return resp.json();
}