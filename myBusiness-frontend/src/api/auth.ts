// src/api/auth.ts
const API = import.meta.env.VITE_API_URL; 

/**
 * Lee el cuerpo de error como texto. Si es JSON con {message}, extrae ese campo.
 */
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
  const resp = await fetch(`${API}/users/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!resp.ok) {
    // Leer como texto para no romper si el body viene vacío
    const text = await resp.text();
    let errMessage = 'Registro fallido';
    if (text) {
      try {
        const obj = JSON.parse(text);
        errMessage = obj.message || errMessage;
      } catch {
        // no es JSON válido, dejamos el mensaje genérico
      }
    }
    throw new Error(errMessage);
  }
}


 /**
  * Verifica el código enviado al usuario.
  * Llama a POST /users/verify
  */
export async function verifyEmailCode(email: string, code: string) {
  const resp = await fetch(`${API}/users/verify`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, code }),
  });

  if (!resp.ok) {
    // Intentamos leer el JSON { message: "..." } que devuelve el back
    const errBody = await resp.json().catch(() => null);
    throw new Error(errBody?.message || "Error desconocido");
  }
}

export interface AuthError {
  message: string;
}

export async function login({
  email,
  password,
}: {
  email: string;
  password: string;
}): Promise<string> {
  const resp = await fetch(
    `${import.meta.env.VITE_API_URL}/auth/login`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    }
  );

  if (!resp.ok) {
    // Intentamos leer JSON { message: "detalle del error" }
    let errData: AuthError;
    try {
      errData = await resp.json();
    } catch {
      errData = { message: "Error desconocido" };
    }
    throw new Error(errData.message || "Login fallido");
  }

  const data = await resp.json();
  return data.token as string;
}
