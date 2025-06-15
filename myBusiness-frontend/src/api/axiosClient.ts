// src/api/axiosClient.ts
import axios, { AxiosRequestConfig } from 'axios';


export function axiosWithAuth() {
  const token = localStorage.getItem('token');
  const config: AxiosRequestConfig = {
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  };
  return axios.create(config);
}