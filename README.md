# MyBusiness - Sistema de Gestión de Inventario

**MyBusiness** es una aplicación full‑stack para la gestión de inventarios, desarrollada con **Java (Spring Boot)** en el backend y **React (Vite + TypeScript)** en el frontend. Ofrece funcionalidades para administrar productos, categorías, unidades, movimientos de stock, alertas, reportes y autenticación de usuarios con verificación por correo (SMTP vía Mailtrap).

---

## Índice

1. [Características](#características)  
2. [Requisitos Previos](#requisitos-previos)  
3. [Estructura del Repositorio](#estructura-del-repositorio)  
4. [Configuración del Entorno Local](#configuración-del-entorno-local)  
   - [Copiar variables de entorno](#copiar-variables-de-entorno)  
   - [Backend](#backend)  
   - [Frontend](#frontend)  
   - [Con Docker Compose](#con-docker-compose)  
5. [Ejecución de la Aplicación](#ejecución-de-la-aplicación)  
6. [Documentación de la API](#documentación-de-la-api)  
7. [Consejos de Desarrollo](#consejos-de-desarrollo)  
8. [Contribuir](#contribuir)  
9. [Licencia](#licencia)  

---

## Características

- **Registro e Inicio de Sesión** con verificación de correo y autenticación JWT.  
- **Gestión de Productos** (CRUD) con umbrales y auditoría de campos.  
- **Movimientos de Stock**: entradas, salidas y ajustes, con cálculo automático de stock.  
- **Alertas** de stock bajo/alto, marcar como leídas o eliminar.  
- **Reportes y Dashboard**: KPIs, snapshots de inventario, tendencias diarias/semanales, exportación a CSV/Excel/PDF.  
- **Campos Personalizados** y valores asociados para cada producto.  
- **Notificaciones por Correo** (SMTP vía Mailtrap).  
- **Auditoría** de quién creó o modificó y fecha/hora usando Spring Data JPA.  

---

## Requisitos Previos

- **Git**  
- **Java 21** (LTS) y **Gradle 8.5+** (para backend local)  
- **Node.js 18+** y **npm** (para frontend local)  
- **Docker** y **Docker Compose** (opcional)  

---

## Estructura del Repositorio

```text
root/
├── backend/               # Spring Boot (Java)
│   ├── src/main/java/...  # Código Java
│   ├── build.gradle       # Configuración de Gradle
│   └── Dockerfile         # Docker image del backend
├── frontend/              # React (Vite + TS)
│   ├── src/               # Código React
│   ├── vite.config.ts     # Configuración de Vite
│   └── Dockerfile         # Docker image del frontend
├── docker-compose.yml     # Orquesta: db, backend y frontend
├── .env.example           # Ejemplo de variables de entorno
└── README.md              # Este archivo

Configuración del Entorno Local
Copiar variables de entorno

Antes de arrancar, copia el archivo de ejemplo y adáptalo:

cp .env.example .env

Abre .env y ajusta:

    PostgreSQL

POSTGRES_DB=...
POSTGRES_USER=...
POSTGRES_PASSWORD=...
DB_PORT=...

Spring Boot

SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
SPRING_JPA_OPEN_IN_VIEW=false

JWT (en application.properties o variables de entorno)

jwt.secret=...
jwt.expiration-ms=...

Mail (Mailtrap)

    Regístrate en https://mailtrap.io

    Crea un inbox nuevo

    Copia las credenciales SMTP y pégalas aquí:

    MAIL_HOST=sandbox.smtp.mailtrap.io
    MAIL_PORT=2525
    MAIL_USERNAME=TU_USER_DE_MAILTRAP
    MAIL_PASSWORD=TU_PASS_DE_MAILTRAP

Frontend

    VITE_API_URL=http://localhost:8080

Backend

    Entra al directorio:

cd backend

Construye y arranca:

    ./gradlew clean build
    ./gradlew bootRun

    El servidor escucha en http://localhost:8080 y las props se pueden ajustar vía .env o application.properties.

Frontend

    Entra al directorio:

cd frontend

Instala dependencias y levanta el servidor de desarrollo:

npm install
npm run dev

La app estará en http://localhost:3000, con proxy /api → http://localhost:8080/api.

Build para producción:

    npm run build

    Genera dist/ listo para servir.

Con Docker Compose

    Asegúrate de tener copiado .env.

    Arranca todo:

    docker-compose up --build

    Accede a:

        Frontend: http://localhost:3000

        Backend API: http://localhost:8080/api

        Healthcheck: http://localhost:8080/actuator/health

Ejecución de la Aplicación

    Abre tu navegador en la landing.

    Haz clic en Registro, rellena nombre, email y contraseña.

    Ve a Mailtrap, copia el código de verificación.

    Introduce el código en la pantalla de verificación.

    Inicia sesión con tu email/contraseña.

Documentación de la API

Con el backend en marcha, visita:

http://localhost:8080/swagger-ui.html

Ahí verás todos los endpoints, esquemas y ejemplos.
Consejos de Desarrollo

    Hot‑reload frontend: npm run dev

    SQL logs: activa spring.jpa.show-sql=true en desarrollo

    Auditoría: revisa AuditingConfig

    Sigue la capa de usecase → controller → repository

Contribuir

    Haz fork del repositorio.

    Crea una rama:

    git checkout -b feature/mi-nueva-funcionalidad

    Realiza cambios con commits claros.

    Abre un Pull Request.

Licencia

Este proyecto está bajo la licencia MIT.
