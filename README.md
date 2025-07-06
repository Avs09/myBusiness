# MyBusiness - Sistema de Gestión de Inventario

**MyBusiness** es una aplicación full-stack para la gestión de inventarios, desarrollada con **Java (Spring Boot)** en el backend y **React (Vite + TypeScript)** en el frontend. Ofrece funcionalidades para administrar productos, categorías, unidades, movimientos de stock, alertas, reportes y autenticación de usuarios con verificación por correo.

---

## Índice

1. [Características](#características)
2. [Requisitos Previos](#requisitos-previos)
3. [Estructura del Repositorio](#estructura-del-repositorio)
4. [Configuración del Entorno Local](#configuración-del-entorno-local)

   * [Backend](#backend)
   * [Frontend](#frontend)
   * [Con Docker Compose](#con-docker-compose)
5. [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
6. [Documentación de la API](#documentación-de-la-api)

---

## Características

* **Registro e Inicio de Sesión** con verificación de correo y autenticación JWT.
* **Gestión de Productos** (CRUD) con umbrales y auditoría de campos.
* **Movimientos de Stock**: entradas, salidas y ajustes, con cálculo automático de stock.
* **Alertas** de stock bajo/alto, marcar como leídas o eliminar.
* **Reportes y Dashboard**: KPIs, snapshots de inventario, tendencias diarias/semanales, exportación a CSV/Excel/PDF.
* **Campos Personalizados** y valores asociados para cada producto.
* **Notificaciones por Correo** (SMTP, Mailtrap por defecto).
* **Auditoría** de quién creó o modificó y fecha/hora usando Spring Data JPA.

## Requisitos Previos

* **Git**
* **Java 21** (LTS) y **Gradle 8.5+** (para backend local)
* **Node.js 18+** y **npm** (para frontend local)
* **Docker** y **Docker Compose** (opcional, para despliegue en contenedores)

## Estructura del Repositorio

```
root/
├── backend/               # Aplicación Spring Boot (Java)
│   ├── src/main/java/...  # Código fuente Java
│   ├── build.gradle       # Configuración de Gradle
│   └── Dockerfile         # Instrucciones de Docker
├── frontend/              # Aplicación React (Vite + TS)
│   ├── src/               # Código fuente React
│   ├── vite.config.ts     # Configuración de Vite
│   └── Dockerfile         # Instrucciones de Docker
├── docker-compose.yml     # Orquesta base de datos, backend y frontend
└── README.md              # Este archivo
```


## Copiar archivo de entorno

Antes de iniciar, copia el ejemplo de variables de entorno y ajústalo. en la raíz ejecuta:

   ```bash
   cd myBusiness
   cp .env.example .env
   ```

 Abre luego .env y reemplaza los valores según tu configuración y tu cuenta de Mailtrap.

 
## Configuración del Entorno Local

### Backend

1. Entra a la carpeta `myBusiness` del backend:

   ```bash
   cd ./myBusiness-backend/myBusiness
   ```
   
2. Construye y ejecuta con Gradle:

   ```bash
   .\gradlew.bat clean build
   .\gradlew.bat bootRun
   ```
   
### Frontend

1. Entra a la carpeta `frontend`:

   ```bash
   cd ./myBusiness-frontend
   ```
2. Instala dependencias y levanta el servidor de desarrollo:

   ```bash
   npm install
   npm run dev
   ```

   * La app estará en `http://localhost:3000`

## Configuración del entorno de producción con Docker-compose   
1. Ejecuta en la raíz:

    #### para levantar servicios
     ```bash
     docker-compose up --build
     ```
    ##### para eliminar contenedores
    ```bash 
    docker-compose down -v
    ```

2. Accede a:

   * **Frontend:** `http://localhost:3000`

## Ejecución de la Aplicación

1. Regístrate desde la página de bienvenida.
2. Verifica tu correo: encontrarás un código para activar la cuenta.
3. Inicia sesión y accede al dashboard y al resto de funcionalidades.

## Documentación de la API

Integrada con **Springdoc OpenAPI**. Con el backend en marcha, visita:

```
http://localhost:8080/swagger-ui.html
```

Ahí encontrarás todos los endpoints, esquemas de datos y ejemplos.
