// src/schemas/auth.ts
import { z } from 'zod'

export const registerSchema = z
  .object({
    name: z
     .string()
     .trim()                               
     .nonempty('El nombre no puede estar vacío')  
     .max(50, 'Máximo 50 caracteres'),

       email: z
    .string()
     .trim()                                 
     .nonempty('Email obligatorio')              
     .email('Formato de email inválido'),
    password: z
      .string()
      .min(8, 'Mínimo 8 caracteres')
      .regex(/[A-Z]/, 'Al menos una mayúscula')
      .regex(/[a-z]/, 'Al menos una minúscula')
      .regex(/[0-9]/, 'Al menos un número')
      .regex(/[^A-Za-z0-9]/, 'Al menos un carácter especial'),
    confirmPassword: z.string().min(1, 'Confirma tu contraseña'),
  })
 
  

export type RegisterDto = z.infer<typeof registerSchema>

export const loginSchema = z.object({
  email: z
    .string()
    .trim()
    .min(1, 'Email obligatorio')
    .email('Formato de email inválido'),
  password: z
    .string()
    .min(1, 'Contraseña obligatoria'),
})
export type LoginDto = z.infer<typeof loginSchema>
