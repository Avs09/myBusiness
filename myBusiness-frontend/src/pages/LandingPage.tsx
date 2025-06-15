// src/pages/LandingPage.tsx
import React, { useState } from "react";
import { motion } from "framer-motion";
import { Card, CardContent } from "@/components/ui/card";
import Button from "@/components/ui/button";
import Input from "@/components/ui/input";
import PasswordInput from "@/components/ui/passwordInput";
import { Controller } from "react-hook-form";
import { verifyEmailCode } from "@/api/auth";

import {
  Mail as MailIcon,
  Lock as LockIcon,
  User as UserIcon,
  CheckCircle,
  Shield,
  BarChart2,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useNavigate } from "react-router-dom";

// Formularios y validación Zod
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import toast from "react-hot-toast";
import {
  loginSchema,
  LoginDto,
  registerSchema,
  RegisterDto,
} from "@/schemas/auth";

const features = [
  {
    icon: <CheckCircle className="w-8 h-8 text-blue-600" />,
    text: "Control de stock en tiempo real",
  },
  {
    icon: <Shield className="w-8 h-8 text-green-600" />,
    text: "Seguridad y permisos avanzados",
  },
  {
    icon: <BarChart2 className="w-8 h-8 text-purple-600" />,
    text: "Reportes detallados y exportables",
  },
];

type Step = "login" | "register" | "verify";

export default function LandingPage() {
  const [step, setStep] = useState<Step>("login");
  const [pendingEmail, setPendingEmail] = useState("");
  const [pendingPassword, setPendingPassword] = useState("");

  const { login, requestRegister: doRegister } = useAuth();
  const navigate = useNavigate();

  // ── Login form
  const {
    register: loginRegister,
    handleSubmit: onLoginSubmit,
    formState: { errors: loginErrors, isSubmitting: loginSubmitting },
  } = useForm<LoginDto>({
    resolver: zodResolver(loginSchema),
    mode: "onChange",
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const onLogin = async (data: LoginDto) => {
    try {
      await login({ email: data.email, password: data.password });
      toast.success("¡Bienvenido!");
      navigate("/dashboard");
    } catch (err: any) {
      // Ahora mostramos exactamente err.message (que vino de loginApi)
      toast.error(err.message || "Login fallido");
    }
  };

  // ── Register form
  const {
    register: reg,
    control,
    handleSubmit: onRegisterSubmit,
    watch,
    formState: { errors: regErrors, isSubmitting: regSubmitting },
  } = useForm<RegisterDto>({
    resolver: zodResolver(registerSchema),
    mode: "onChange",
    defaultValues: {
      name: "",
      email: "",
      password: "",
      confirmPassword: "",
    },
  });

  const onRegister = async (data: RegisterDto) => {
    try {
   
      await doRegister({
        name: data.name,
        email: data.email,
        password: data.password,
      });
      toast.success("Revisa tu email: enviamos un código de verificación.");
      setPendingEmail(data.email);
      setPendingPassword(data.password);
      setStep("verify");
    } catch (err: any) {
      
      toast.error(err.message || "Registro fallido");
    }
  };

  // ── Verify form
  const {
    register: verifyRegister,
    handleSubmit: onVerifySubmit,
    formState: { errors: verifyErrors, isSubmitting: verifySubmitting },
  } = useForm<{ code: string }>();

  const onVerify = async (data: { code: string }) => {
    try {
      await verifyEmailCode(pendingEmail, data.code);
      toast.success(
        "Correo verificado correctamente. Ahora ya puedes iniciar sesión."
      );
      navigate("/"); // o '/login'
    } catch (err: any) {
      // Si el back devolvió 400 con un mensaje, err.message contiene esa descripción
      toast.error(err.message || "Código inválido");
    }
  };

  return (
    <div className="h-screen w-full flex flex-col">
      {/* Hero Section */}
      <motion.section
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 1 }}
        className="flex flex-1 items-center justify-center bg-gradient-to-br from-blue-50 to-white w-full"
      >
        <div className="w-full max-w-7xl grid grid-cols-1 lg:grid-cols-2 gap-16 px-8">
          <motion.div
            initial={{ x: -200, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ duration: 0.8 }}
            className="flex flex-col justify-center space-y-6"
          >
            <h1 className="text-5xl lg:text-6xl font-extrabold text-gray-800">
              Bienvenido a <span className="text-blue-600">MyBusiness</span>
            </h1>
            <p className="text-gray-600 text-xl lg:text-2xl">
              Optimiza tu inventario, reduce pérdidas y toma decisiones basadas
              en datos en tiempo real.
            </p>
            <ul className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {features.map((f, idx) => (
                <li
                  key={idx}
                  className="flex items-center space-x-4 bg-white p-4 rounded-lg shadow-sm"
                >
                  {f.icon}
                  <span className="text-gray-700 font-medium">{f.text}</span>
                </li>
              ))}
            </ul>
          </motion.div>

          {/* Forms Section */}
          <motion.div
            initial={{ x: 200, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ duration: 0.8 }}
            className="flex flex-col justify-center items-center px-8"
          >
            {/* Tabs */}
            <div className="flex justify-center mb-6 space-x-4">
              <button
                onClick={() => setStep("login")}
                className={`px-4 py-2 font-medium ${
                  step === "login"
                    ? "border-b-4 border-blue-600 text-blue-600"
                    : "text-gray-500"
                }`}
              >
                Login
              </button>
              <button
                onClick={() => setStep("register")}
                className={`px-4 py-2 font-medium ${
                  step === "register"
                    ? "border-b-4 border-green-600 text-green-600"
                    : "text-gray-500"
                }`}
              >
                Registro
              </button>
            </div>

            <Card className="w-full max-w-md">
              <CardContent className="p-6 space-y-4">
                {/* Login Form */}
                {step === "login" && (
                  <form onSubmit={onLoginSubmit(onLogin)}>
                    <fieldset disabled={loginSubmitting} className="space-y-4">
                      <div className="relative w-full">
                        <MailIcon className="absolute left-3 top-3.5 text-gray-400" />
                        <Input
                          type="email"
                          placeholder="Email"
                          className="w-full h-12 pl-10"
                          {...loginRegister("email")}
                        />
                      </div>

                      {/* Mostrar error de email */}
                      {loginErrors.email && (
                        <p className="form-feedback form-feedback--error mt-c1">
                          {loginErrors.email.message}
                        </p>
                      )}

                      <PasswordInput
                        {...loginRegister("password")}
                        placeholder="Contraseña"
                        showStrength={false}
                        error={loginErrors.password?.message}
                        className="w-full"
                      />

                      <Button type="submit" className="w-full h-12">
                        {loginSubmitting ? "Procesando..." : "Iniciar Sesión"}
                      </Button>
                    </fieldset>
                  </form>
                )}

                {/* Register Form */}
                {step === "register" && (
                  <form onSubmit={onRegisterSubmit(onRegister)}>
                    <fieldset disabled={regSubmitting} className="space-y-4">
                      {/* Nombre */}
                      <div className="relative w-full">
                        <UserIcon className="absolute left-3 top-3.5 text-gray-400" />
                        <Input
                          placeholder="Nombre completo"
                          className="w-full h-12 pl-10"
                          {...reg("name")}
                        />
                      </div>
                      {regErrors.name && (
                        <p className="form-feedback form-feedback--error mt-1">
                          {regErrors.name.message}
                        </p>
                      )}

                      {/* Email */}
                      <div className="relative w-full">
                        <MailIcon className="absolute left-3 top-3.5 text-gray-400" />
                        <Input
                          placeholder="ejemplo@dominio.com"
                          className="w-full h-12 pl-10"
                          {...reg("email")}
                        />
                      </div>
                      {regErrors.email && (
                        <p className="form-feedback form-feedback--error mt-1">
                          {regErrors.email.message}
                        </p>
                      )}

                      {/* Contraseña */}
                      <Controller
                        name="password"
                        control={control}
                        render={({ field }) => (
                          <PasswordInput
                            {...field}
                            placeholder="Contraseña"
                            error={regErrors.password?.message}
                            className="w-full"
                          />
                        )}
                      />

                      {/* Confirmar contraseña */}
                      <Controller
                        name="confirmPassword"
                        control={control}
                        render={({ field }) => (
                          <PasswordInput
                            {...field}
                            placeholder="Confirmar contraseña"
                            error={regErrors.confirmPassword?.message}
                            className="w-full"
                          />
                        )}
                      />

                      {/* Mensaje de coincidencia */}
                      {watch("confirmPassword") &&
                        (watch("password") === watch("confirmPassword") ? (
                          <p className="form-feedback form-feedback--success mt-1">
                            ✔️ Las contraseñas coinciden
                          </p>
                        ) : (
                          <p className="form-feedback form-feedback--error mt-1">
                            ❌ Las contraseñas no coinciden
                          </p>
                        ))}

                      <Button
                        type="submit"
                        className="w-full py-3"
                        disabled={regSubmitting}
                      >
                        {regSubmitting ? "Registrando..." : "Crear cuenta"}
                      </Button>
                    </fieldset>
                  </form>
                )}

                {/* Verify Form */}
                {step === "verify" && (
                  <form onSubmit={onVerifySubmit(onVerify)}>
                    <fieldset disabled={verifySubmitting} className="space-y-4">
                      <p className="text-gray-700">
                        Revisa tu email y escribe el código recibido:
                      </p>
                      <div className="relative w-full">
                        <Input
                          type="text"
                          placeholder="Código de verificación"
                          className="w-full h-12 pl-3"
                          {...verifyRegister("code", {
                            required: "Código obligatorio",
                          })}
                        />
                      </div>
                      {verifyErrors.code && (
                        <p className="form-feedback form-feedback--error mt-1">
                          {verifyErrors.code.message}
                        </p>
                      )}
                      <Button type="submit" className="w-full h-12">
                        {verifySubmitting ? "Procesando..." : "Verificar"}
                      </Button>
                    </fieldset>
                  </form>
                )}
              </CardContent>
            </Card>
          </motion.div>
        </div>
      </motion.section>
    </div>
  );
}
