// src/hooks/useAuth.tsx
import React, { createContext, useContext, ReactNode, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import { login as loginApi, register as registerApi } from '@/api/auth';

interface DecodedToken {
  sub: string;
  exp: number;
}

interface AuthContextType {
  user: { email: string } | null;
  login: (data: { email: string; password: string }) => Promise<void>;
  requestRegister: (data: { name: string; email: string; password: string }) => Promise<void>;
  logout: () => void;
  getAuthHeader: () => Record<string, string>;
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  login: async () => {},
  requestRegister: async () => {},
  logout: () => {},
  getAuthHeader: () => ({} as Record<string, string>),
});

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();

  // Inicializar el usuario desde localStorage de forma SINCRÓNICA para evitar logout al refrescar
  const initialUser: { email: string } | null = (() => {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      if (decoded.exp * 1000 > Date.now()) {
        return { email: decoded.sub };
      } else {
        localStorage.removeItem('token');
        return null;
      }
    } catch (err) {
      console.error('Error al decodificar token:', err);
      localStorage.removeItem('token');
      return null;
    }
  })();

  const [user, setUser] = useState<{ email: string } | null>(initialUser);

  async function login({ email, password }: { email: string; password: string }) {
    const token = await loginApi({ email, password });
    localStorage.setItem('token', token);
    setUser({ email });
    navigate('/dashboard');
  }

  async function requestRegister({
    name,
    email,
    password,
  }: {
    name: string;
    email: string;
    password: string;
  }): Promise<void> {
    await registerApi({ name, email, password });
  }

  function logout() {
    localStorage.removeItem('token');
    setUser(null);
    navigate('/login');
  }

  function getAuthHeader(): Record<string, string> {
    const token = localStorage.getItem('token');
    return token ? { Authorization: `Bearer ${token}` } : ({} as Record<string, string>);
  }

  return (
    <AuthContext.Provider value={{ user, login, requestRegister, logout, getAuthHeader }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
