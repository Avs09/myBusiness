# -----------------------------
# Etapa 1: Build con Gradle y JDK 21
# -----------------------------
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /home/gradle/project

# Copiar Gradle wrapper y configuración para cache de dependencias
COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradle/ gradle/
COPY --chown=gradle:gradle build.gradle settings.gradle* ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY --chown=gradle:gradle src/ src/

# Empaquetar JAR
RUN ./gradlew clean bootJar --no-daemon

# -----------------------------
# Etapa 2: Runtime con JRE 21
# -----------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
