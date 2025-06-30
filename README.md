# MyBusiness – Sistema de Gestión de Inventario

**MyBusiness** es una aplicación full‑stack para la gestión de inventarios, desarrollada con **Java 21 (Spring Boot)** en el backend y **React (Vite + TypeScript)** en el frontend. Ofrece funcionalidades para administrar productos, categorías, unidades, movimientos de stock, alertas, reportes y autenticación de usuarios con verificación por correo.

---

## Índice

1. [Características](#características)  
2. [Requisitos Previos](#requisitos-previos)  
3. [Estructura del Repositorio](#estructura-del-repositorio)  
4. [Configuración del Entorno Local](#configuración-del-entorno-local)  
   - [Copiar fichero de entorno](#copiar-fichero-de-entorno)  
   - [Backend](#backend)  
   - [Frontend](#frontend)  
   - [Con Docker Compose](#con-docker-compose)  
5. [Ejecución de la Aplicación](#ejecución-de-la-aplicación)  
6. [Documentación de la API](#documentación-de-la-api)  

---

## Características

- **Registro e Inicio de Sesión** con verificación por código enviado al correo y autenticación JWT.  
- **Gestión de Productos** (CRUD) con umbrales, auditoría de cambios y validaciones.  
- **Movimientos de Stock**: entradas, salidas y ajustes, con cálculo automático de stock antes y después.  
- **Alertas** de stock bajo o alto, marcación como leídas o eliminación permanente.  
- **Reportes y Dashboard**: KPIs, snapshots históricos, tendencias diarias/semanales, exportación a CSV/Excel/PDF.  
- **Campos Personalizados**: definir campos extras por producto y registrar sus valores.  
- **Notificaciones por Correo** usando SMTP (configurado con Mailtrap en desarrollo).  
- **Auditoría** automática de quién creó/modificó y cuándo, via Spring Data JPA.

---

## Requisitos Previos

- **Git**  
- **Java 21 (LTS)** y **Gradle 8.5+** (o usar Docker)  
- **Node.js 18+** y **npm** (o Yarn)  
- **Docker** y **Docker Compose** (opcional)

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
└── README.md              # Este archivo

Configuración del Entorno Local
Copiar fichero de entorno

En la raíz del proyecto, copia los ejemplos de variables de entorno y ajústalos:

cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env

Backend

    Entra en la carpeta:

cd backend

Construye y arranca en modo desarrollo:

./gradlew clean build
./gradlew bootRun

    El backend escuchará en http://localhost:8080.

SMTP / Mailtrap

    Crea una cuenta en Mailtrap.

    Ve a Sandbox -> crea un nuevo sandbox en "Add sanbox"
    selecciona integration -> SMTP

    Copia Username y Password.

    Rellena tu backend/.env con esos valores bajo la sección # --- Mail ---:

    MAIL_HOST=sandbox.smtp.mailtrap.io
    MAIL_PORT=2525
    MAIL_USERNAME=TU_USERNAME_MAILTRAP
    MAIL_PASSWORD=TU_PASSWORD_MAILTRAP

    El fichero application.properties ya hace referencia a estas variables.

Variables de entorno principales:

    SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST>:<PUERTO>/<DB>
    SPRING_DATASOURCE_USERNAME=<USER>
    SPRING_DATASOURCE_PASSWORD=<PASSWORD>
    jwt.secret=<TU_SECRETO_JWT>
    jwt.expiration-ms=3600000

Frontend

    Entra en la carpeta:

cd frontend

Instala dependencias y levanta servidor de desarrollo:

npm install
npm run dev

    La app estará disponible en http://localhost:3000 y todas las peticiones a /api se proxyarán a http://localhost:8080/api.

Variables Vite en frontend/.env (puedes usar VITE_API_URL para apuntar a otro backend):

VITE_API_URL=http://localhost:8080

Para compilar en modo producción:

    npm run build

        Genera la carpeta dist/ con los assets listos para servir.

Con Docker Compose

    En la raíz del proyecto, copia y ajusta el fichero:

cp .env.example .env

Arranca todos los servicios:

    docker-compose up --build

    Accede a:

        Frontend: http://localhost:3000

        API Backend: http://localhost:8080/api

Ejecución de la Aplicación

    Abre en el navegador http://localhost:3000.

    Regístrate en la pantalla de bienvenida.

    Revisa tu bandeja de Mailtrap: recibirás un código de verificación.

    Introduce el código en el formulario de verificación.

    Una vez activada la cuenta, inicia sesión y accede al Dashboard y al resto de módulos.

Documentación de la API

Con el backend en marcha, abre:

http://localhost:8080/swagger-ui.html

Encontrarás todos los endpoints, modelos de datos y ejemplos de uso.
