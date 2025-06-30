MyBusiness - Sistema de Gestión de Inventario

MyBusiness es una aplicación full‑stack para la gestión de inventarios, desarrollada con Java (Spring Boot) en el backend y React (Vite + TypeScript) en el frontend. Ofrece funcionalidades para administrar productos, categorías, unidades, movimientos de stock, alertas, reportes y autenticación de usuarios con verificación por correo.
Índice

    Características

    Requisitos Previos

    Estructura del Repositorio

    Configuración del Entorno Local

        Variables de Entorno

        Backend

        Frontend

        Con Docker Compose

    Ejecución de la Aplicación

    Documentación de la API

Características

    Registro e Inicio de Sesión con verificación de correo y autenticación JWT.

    Gestión de Productos (CRUD) con umbrales y auditoría de campos.

    Movimientos de Stock: entradas, salidas y ajustes, con cálculo automático de stock.

    Alertas de stock bajo/alto, marcar como leídas o eliminar.

    Reportes y Dashboard: KPIs, snapshots de inventario, tendencias diarias/semanales, exportación a CSV/Excel/PDF.

    Campos Personalizados y valores asociados para cada producto.

    Notificaciones por Correo (SMTP, Mailtrap por defecto).

    Auditoría de quién creó o modificó y fecha/hora usando Spring Data JPA.

Requisitos Previos

    Git

    Java 21 (LTS) y Gradle 8.5+ (para backend local)

    Node.js 18+ y npm (para frontend local)

    Docker y Docker Compose (opcional, para despliegue en contenedores)

Estructura del Repositorio

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

Configuración del Entorno Local
Variables de Entorno

Antes de arrancar backend o frontend, copia y adapta el archivo de ejemplo:

cp .env.example .env

Abre .env y revisa:

# --- PostgreSQL ---
POSTGRES_DB=myBusiness
POSTGRES_USER=postgres
POSTGRES_PASSWORD=Babaluarara123
DB_PORT=5433

# --- Spring Boot (backend) ---
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
SPRING_JPA_OPEN_IN_VIEW=false

# --- Frontend (Vite) ---
VITE_API_URL=

# --- Mail (Mailtrap) ---
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=<TU_MAILTRAP_USER>
MAIL_PASSWORD=<TU_MAILTRAP_PASS>

Configuración de Mailtrap

    Regístrate o inicia sesión en Mailtrap.

    Crea un Inbox.

    Ve a Sandbox > Integration

    Copia Username y Password.

    Pega esos valores en tu .env reemplazando <TU_MAILTRAP_USER> y <TU_MAILTRAP_PASS>.

Backend

    Entra al directorio backend:

cd backend

Comprueba que tu .env contenga las credenciales de Mailtrap.

Construye y ejecuta:

    ./gradlew clean build
    ./gradlew bootRun

    El servidor estará en http://localhost:8080.

Frontend

    Entra al directorio frontend:

cd frontend

Instala dependencias y arranca en modo desarrollo:

npm install
npm run dev

Accede a http://localhost:3000 (proxy a /api → http://localhost:8080/api).

Para compilar en producción:

    npm run build

    Genera la carpeta dist/.

Con Docker Compose

    Asegúrate de haber copiado y editado .env en la raíz del proyecto.

    Levanta todos los servicios:

    docker-compose up --build

    URLs de acceso:

        Frontend: http://localhost:3000

        API Backend: http://localhost:8080/api

Ejecución de la Aplicación

    Abre http://localhost:3000.

    Regístrate; recibirás un código en tu Inbox de Mailtrap.

    Introduce el código para verificar el email.

    Inicia sesión y disfruta del dashboard y funcionalidades.

Documentación de la API

Con el backend en marcha, visita:

http://localhost:8080/swagger-ui.html

