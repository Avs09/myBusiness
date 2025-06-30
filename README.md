# MyBusiness - Sistema de Gestión de Inventario

**MyBusiness** es una aplicación full-stack para la gestión de inventarios, desarrollada con **Java (Spring Boot)** en el backend y **React (Vite + TypeScript)** en el frontend. Ofrece funcionalidades para administrar productos, categorías, unidades, movimientos de stock, alertas, reportes y autenticación de usuarios con verificación por correo.

---

## Índice

1. [Características](#características)  
2. [Requisitos Previos](#requisitos-previos)  
3. [Estructura del Repositorio](#estructura-del-repositorio)  
4. [Configuración del Entorno Local](#configuración-del-entorno-local)  
   - [Variables de Entorno & Mailtrap](#variables-de-entorno--mailtrap)  
   - [Backend](#backend)  
   - [Frontend](#frontend)  
   - [Con Docker Compose](#con-docker-compose)  
5. [Ejecución de la Aplicación](#ejecución-de-la-aplicación)  
6. [Documentación de la API](#documentación-de-la-api)  

---

## Características

- **Registro e Inicio de Sesión** con verificación de correo y autenticación JWT.  
- **Gestión de Productos** (CRUD) con umbrales y auditoría de campos.  
- **Movimientos de Stock**: entradas, salidas y ajustes, con cálculo automático de stock.  
- **Alertas** de stock bajo/alto, marcar como leídas o eliminar.  
- **Reportes y Dashboard**: KPIs, snapshots de inventario, tendencias diarias/semanales, exportación a CSV/Excel/PDF.  
- **Campos Personalizados** y valores asociados para cada producto.  
- **Notificaciones por Correo** (SMTP via Mailtrap).  
- **Auditoría** de quién creó o modificó y fecha/hora usando Spring Data JPA.  

---

## Requisitos Previos

- **Git**  
- **Java 21** (LTS) + **Gradle 8.5+**  
- **Node.js 18+** + **npm**  
- **Docker** + **Docker Compose** (opcional)  

---


## Estructura del Repositorio

```text
root/
├── backend/               # Aplicación Spring Boot (Java)
│   ├── src/main/java/...  # Código fuente Java
│   ├── src/main/resources/application.properties
│   ├── build.gradle
│   └── Dockerfile
├── frontend/              # Aplicación React (Vite + TS)
│   ├── src/               # Código fuente React/TypeScript
│   ├── vite.config.ts
│   ├── Dockerfile
│   └── .env.example       # Variables de entorno para Vite
├── docker-compose.yml     # Orquesta PostgreSQL, backend y frontend
└── README.md              # Este archivo ---

```text
## Configuración del Entorno Local

### Variables de Entorno & Mailtrap

1. Copia y renombra el fichero de ejemplo:

   ```bash
   cp .env.example .env

    Abre .env y ajusta tus credenciales:

    # --- PostgreSQL ---
    POSTGRES_DB=myBusiness
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=Babaluarara123
    DB_PORT=5433

    # --- Backend ---
    SPRING_PROFILES_ACTIVE=dev
    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/${POSTGRES_DB}
    SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
    SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
    SPRING_JPA_OPEN_IN_VIEW=false

    # --- Frontend ---
    VITE_API_URL=

    # --- Mail (Mailtrap) ---
    MAIL_HOST=sandbox.smtp.mailtrap.io
    MAIL_PORT=2525
    MAIL_USERNAME=<TU_MAILTRAP_USER>
    MAIL_PASSWORD=<TU_MAILTRAP_PASSWORD>

    Configurar Mailtrap:

        Regístrate en Mailtrap.io.

        Ve a sandbox > SMPT

        Copia Username, Password y pégalos en .env.

        Con esto la aplicación enviará correos en entorno de desarrollo.

Backend

cd backend
./gradlew clean build
./gradlew bootRun

    El servidor arranca en http://localhost:8080.

    Lee variables de .env para configuración (base de datos, JWT, SMTP…).

Frontend

cd frontend
npm install
npm run dev

    Sirve en http://localhost:3000 y hace proxy de /api → http://localhost:8080/api.

    Para build de producción:

    npm run build

    Genera /dist con los archivos estáticos.

Con Docker Compose

docker-compose up --build

    Frontend: http://localhost:3000

    API Backend: http://localhost:8080/api

Ejecución de la Aplicación

    Visita http://localhost:3000.

    Regístrate y revisa tu bandeja en Mailtrap.

    Ingresa el código de verificación.

    Haz login y explora el panel.

Documentación de la API

Con el backend activo, abre:

http://localhost:8080/swagger-ui.html

