# README - MyBusiness (Gestión de Inventario)

Guía paso a paso para clonar, configurar y ejecutar el proyecto con Docker Compose.

## 1. Clonar el repositorio

En tu terminal, ejecuta:

```bash
git clone https://github.com/Avs09/myBusiness.git
cd myBusiness
```

Esto crea la carpeta `myBusiness` con la siguiente estructura:

```
myBusiness/
├── myBusiness-backend/
│   ├── gradlew
│   ├── gradle/
│   └── myBusiness/
│       ├── src/
│       ├── build.gradle
│       ├── settings.gradle
│       ├── src/main/resources/application.properties
│       └── Dockerfile
├── myBusiness-frontend/
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   ├── nginx.conf
│   └── Dockerfile
├── .env.example
├── docker-compose.yml
├── .gitignore
└── .github/workflows/ci.yml
```

## 2. Crear archivo de entorno (.env)

En `myBusiness/`, copia:

```bash
cp .env.example .env
```

Verifica que `myBusiness/.env` contenga exactamente:

```env
POSTGRES_DB=myBusiness
POSTGRES_USER=postgres
POSTGRES_PASSWORD=Babaluarara123
DB_PORT=5433
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/myBusiness
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=Babaluarara123
SPRING_JPA_OPEN_IN_VIEW=false
VITE_API_URL=
```

## 3. Verificar archivos clave

* **Backend**:

  * Dockerfile: `myBusiness/myBusiness-backend/myBusiness/Dockerfile`
  * Gradle wrapper: `myBusiness/myBusiness-backend/gradlew` y `myBusiness/myBusiness-backend/gradle/`
  * build.gradle: `myBusiness/myBusiness-backend/myBusiness/build.gradle`
  * settings.gradle: `myBusiness/myBusiness-backend/myBusiness/settings.gradle`
  * application.properties: `myBusiness/myBusiness-backend/myBusiness/src/main/resources/application.properties`
* **Frontend**:

  * Dockerfile: `myBusiness/myBusiness-frontend/Dockerfile`
  * package.json: `myBusiness/myBusiness-frontend/package.json`
  * vite.config.ts: `myBusiness/myBusiness-frontend/vite.config.ts`
  * nginx.conf: `myBusiness/myBusiness-frontend/nginx.conf`
  * Código fuente: `myBusiness/myBusiness-frontend/src/`
* **docker-compose.yml**: `myBusiness/docker-compose.yml`
* **.gitignore**: `myBusiness/.gitignore`
* **.env.example**: `myBusiness/.env.example`
* **CI config**: `myBusiness/.github/workflows/ci.yml`

## 4. Docker Compose

En `myBusiness/`, ejecuta:

```bash
# Levantar servicios
docker-compose up -d --build
```

* Esto construye imágenes y levanta contenedores:

  * Postgres en `localhost:5433`
  * Backend en `localhost:8080`
  * Frontend en `localhost:3000`

Para verificar estado:

```bash
docker-compose ps
```

Para ver logs de backend:

```bash
docker-compose logs -f mybusiness-backend
```

Para detener y eliminar contenedores/redes:

```bash
docker-compose down
```

Para reiniciar sin cache si es necesario:

```bash
docker-compose build --no-cache
docker-compose up -d
```

## 5. Conexión a la base de datos

En pgAdmin o cliente SQL, crea conexión con:

* Host: `localhost`
* Puerto: `5433`
* Base de datos: `myBusiness`
* Usuario: `postgres`
* Contraseña: `Babaluarara123`

## 6. Acceder a la aplicación

* **Frontend**: abre en el navegador [http://localhost:3000](http://localhost:3000)
* **Backend API**: accesible en [http://localhost:8080/api](http://localhost:8080/api)

## 7. CI/CD (GitHub Actions)

Archivo en `myBusiness/.github/workflows/ci.yml`:

```yaml
name: CI
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
jobs:
  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          distribution: temurin
          java-version: '21'
      - run: |
          cd myBusiness-backend/myBusiness
          ./gradlew clean build --no-daemon
  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: |
          cd myBusiness-frontend
          npm ci
          npm run build
```


Fin.
