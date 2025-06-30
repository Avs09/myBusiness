MyBusiness – Sistema de Gestión de Inventario

MyBusiness es una aplicación full‑stack para la gestión de inventarios, desarrollada con Java (Spring Boot) en el backend y React (Vite + TypeScript) en el frontend. Ofrece funcionalidades para administrar productos, categorías, unidades, movimientos de stock, alertas, reportes y autenticación de usuarios con verificación por correo (SMTP vía Mailtrap).
Índice

    Características

    Requisitos Previos

    Estructura del Repositorio

    Configuración del Entorno Local

    Ejecución de la Aplicación

    Documentación de la API

    Consejos de Desarrollo

    Contribuir

    Licencia

Características

    Registro e Inicio de Sesión con verificación de correo y autenticación JWT

    Gestión de Productos (CRUD) con umbrales y auditoría de campos

    Movimientos de Stock: entradas, salidas y ajustes con cálculo automático

    Alertas de stock bajo/alto (marcar como leídas/eliminar)

    Reportes y Dashboard: KPIs, snapshots, tendencias, exportación a CSV/Excel/PDF

    Campos Personalizados para productos

    Notificaciones por Correo (SMTP vía Mailtrap)

    Auditoría de cambios con Spring Data JPA

Requisitos Previos

    Git

    Java 21+ y Gradle 8.5+

    Node.js 18+ y npm

    Docker y Docker Compose (opcional)

Estructura del Repositorio
text

root/
├── backend/               # Spring Boot (Java)
│   ├── src/main/java/     # Código fuente
│   ├── build.gradle       # Configuración de Gradle
│   └── Dockerfile
├── frontend/              # React (Vite + TypeScript)
│   ├── src/               # Componentes React
│   ├── vite.config.ts     # Configuración de Vite
│   └── Dockerfile
├── docker-compose.yml     # Orquestación de servicios
├── .env.example           # Plantilla de variables
└── README.md              # Este archivo

Configuración del Entorno Local
Copiar variables de entorno
bash

cp .env.example .env

Edita .env y completa los valores (especialmente Mailtrap):
env

# Mailtrap (obtén credenciales en mailtrap.io)
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=<TU_USER>
MAIL_PASSWORD=<TU_PASSWORD>

# PostgreSQL
POSTGRES_DB=mybusiness
POSTGRES_USER=admin
POSTGRES_PASSWORD=secret

Backend
bash

cd backend
./gradlew clean build
./gradlew bootRun  # Escucha en http://localhost:8080

Frontend
bash

cd frontend
npm install
npm run dev  # Escucha en http://localhost:3000

Con Docker Compose
bash

docker-compose up --build

Accede a:

    Frontend: http://localhost:3000

    API: http://localhost:8080/api

    Healthcheck: http://localhost:8080/actuator/health

Ejecución de la Aplicación

    Abre http://localhost:3000

    Regístrate con nombre/email/contraseña

    Verifica tu cuenta con el código de Mailtrap

    Inicia sesión y gestiona tu inventario

Documentación de la API

Con el backend en ejecución:
🔗 Swagger UI: http://localhost:8080/swagger-ui.html

Incluye:

    Endpoints interactivos

    Esquemas de petición/respuesta

    Ejemplos de uso

Consejos de Desarrollo

    Hot-reload frontend: npm run dev

    Ver SQL generado: Agrega spring.jpa.show-sql=true en application.properties

    Nueva funcionalidad: Sigue el flujo:
    usecase → controller → repository

    Auditoría: Revisa AuditingConfig.java

Contribuir

    Haz fork del repositorio

    Crea tu rama:

bash

git checkout -b feature/nueva-funcionalidad

    Realiza commits descriptivos

    Abre un Pull Request

Licencia

MIT
text

Permiso libre de uso, modificación y distribución.
Incluye atribución al autor original.
