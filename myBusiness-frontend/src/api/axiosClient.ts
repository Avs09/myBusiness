// src/api/axiosClient.ts
import axios, { AxiosRequestConfig } from 'axios';

export function axiosWithAuth() {
  const token = localStorage.getItem('token');
  // Si VITE_API_URL está definido (por ejemplo "http://localhost:8080"), lo usamos y añadimos "/api".
  // Si no, baseURL será "/api", lo que Nginx redirige al backend container.
  const baseURL = import.meta.env.VITE_API_URL
    ? `${import.meta.env.VITE_API_URL.replace(/\/$/, '')}/api`
    : '/api';

  const config: AxiosRequestConfig = {
    baseURL,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  };
  return axios.create(config);
}
