FROM node:22-alpine AS build
WORKDIR /app/frontend/angular-app

COPY frontend/angular-app/package*.json ./
RUN npm ci

COPY frontend/angular-app ./
RUN npm run build

FROM nginx:1.27-alpine
COPY infra/nginx/nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/frontend/angular-app/dist/angular-app/browser /usr/share/nginx/html
EXPOSE 80
