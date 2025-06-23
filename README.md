# README - MyBusiness

## Estructura esperada tras clonar

```
<myBusiness>/
├── myBusiness-backend/
│   ├── myBusiness/
│   │   ├── src/
│   │   ├── build.gradle
│   │   ├── settings.gradle
│   │   ├── src/main/resources/application.properties
│   │   └── Dockerfile
│   ├── gradlew
│   └── gradle/
├── myBusiness-frontend/
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   ├── nginx.conf
│   └── Dockerfile
├── .env.example
├── docker-compose.yml
├── Makefile
├── .gitignore
└── .github/workflows/ci.yml
```

## 1. Clonar el repositorio

Ejecutar en terminal:

```bash
git clone https://github.com/Avs09/myBusiness.git
cd myBusiness
```

## 2. Copiar variables de entorno

En `myBusiness`, copiar:

```bash
cp .env.example .env
```

Contenido de `myBusiness/.env`:

```
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

## 3. Verificar archivos en backend

* Dockerfile: `myBusiness/myBusiness-backend/myBusiness/Dockerfile`
* Gradle wrapper: `myBusiness/myBusiness-backend/gradlew` y `myBusiness>/myBusiness-backend/gradle/`
* build.gradle y settings.gradle: `myBusiness/myBusiness-backend/myBusiness/build.gradle` y `myBusiness/myBusiness-backend/myBusiness/settings.gradle`
* application.properties: `myBusiness/myBusiness-backend/myBusiness/src/main/resources/application.properties`

## 4. Verificar archivos en frontend

* Dockerfile: `myBusiness/myBusiness-frontend/Dockerfile`
* package.json: `myBusiness/myBusiness-frontend/package.json`
* vite.config.ts: `myBusiness/myBusiness-frontend/vite.config.ts`
* nginx.conf: `myBusiness/myBusiness-frontend/nginx.conf`
* src/: `myBusiness/myBusiness-frontend/src/`

## 5. Verificar docker-compose.yml y Makefile

* `docker-compose.yml`: `myBusiness/docker-compose.yml`. Debe contener:

  * Servicio db con puerto host `${DB_PORT}:5432`.
  * Servicio backend con `context: ./myBusiness-backend/myBusiness`.
  * Servicio frontend con `context: ./myBusiness-frontend`.
* `Makefile`: `myBusiness/Makefile`. Debe incluir:

  ```makefile
  up:
  	docker-compose up -d --build

  down:
  	docker-compose down

  ps:
  	docker-compose ps

  logs-backend:
  	docker logs -f mybusiness-backend

  logs-db:
  	docker logs -f mybusiness-db

  logs-frontend:
  	docker logs -f mybusiness-frontend
  ```

## 6. Levantar servicios

En `myBusiness`, ejecutar:

```bash
make up
```

(si Makefile no está, ejecutar directamente: `docker-compose up -d --build`)

## 7. Verificar contenedores

```bash
make ps
```

Debe mostrar:

* mybusiness-db -> 0.0.0.0:5433->5432
* mybusiness-backend -> 0.0.0.0:8080->8080
* mybusiness-frontend -> 0.0.0.0:3000->80

## 8. Conectar a la base de datos

En pgAdmin o cliente SQL, conectar:

* Host: localhost
* Puerto: 5433
* Base de datos: myBusiness
* Usuario: postgres
* Contraseña: Babaluarara123

## 9. Acceder a la aplicación

* Frontend: [http://localhost:3000](http://localhost:3000)
* Backend API: [http://localhost:8080/api](http://localhost:8080/api)

## 10. Detener servicios

En `myBusiness`, ejecutar:

```bash
make down
```

## 11. CI/CD

Revisar `myBusiness/.github/workflows/ci.yml`. Debe contener:

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
