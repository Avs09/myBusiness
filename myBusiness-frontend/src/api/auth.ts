// src/api/auth.ts

const API_BASE = import.meta.env.VITE_API_URL
  ? import.meta.env.VITE_API_URL.replace(/\/$/, '')
  : ''; // si vacío, fetch('/api/usuarios/...') usará ruta relativa

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

export async function register(payload: { name: string; email: string; password: string }): Promise<void> {
  // Endpoint: POST /api/users/register
  const url = `${API_BASE}/api/users/register`;
  const resp = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!resp.ok) {
    const msg = await parseError(resp);
    throw new Error(msg);
  }
}

export async function verifyEmailCode(email: string, code: string): Promise<void> {
  // POST /api/users/verify
  const url = `${API_BASE}/api/users/verify`;
  const resp = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, code }),
  });
  if (!resp.ok) {
    const msg = await parseError(resp);
    throw new Error(msg);
  }
}

export async function login({
  email,
  password,
}: {
  email: string;
  password: string;
}): Promise<string> {
  // POST /api/auth/login
  const url = `${API_BASE}/api/auth/login`;
  const resp = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  if (!resp.ok) {
    const msg = await parseError(resp);
    throw new Error(msg);
  }

  const data = await resp.json();
  return data.token as string;
}
