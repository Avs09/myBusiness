# ./Dockerfile 
# Etapa 1: Construcción con Node
# -----------------------------
FROM node:18-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

# Argumento para configurar la URL del backend en tiempo de build (si se desea)
ARG VITE_API_URL
ENV VITE_API_URL=${VITE_API_URL}

COPY . .
RUN npm run build

# -----------------------------
# Etapa 2: Servir con NGINX
# -----------------------------
FROM nginx:stable-alpine AS production

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
