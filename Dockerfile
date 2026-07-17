FROM node:24.15-alpine AS frontend
WORKDIR /workspace/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM eclipse-temurin:21-jdk-alpine AS backend
WORKDIR /workspace/backend
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
COPY backend/src/ src/
COPY --from=frontend /workspace/frontend/dist/frontend/browser/ src/main/resources/static/
RUN chmod +x mvnw && ./mvnw package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache nginx \
    && addgroup -S app \
    && adduser -S app -G app
COPY --from=backend /workspace/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar
COPY --from=frontend /workspace/frontend/dist/frontend/browser/ /app/static/
COPY deploy/nginx.conf.template /app/nginx.conf.template
COPY deploy/start.sh /app/start.sh
RUN chmod +x /app/start.sh
USER app
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- http://127.0.0.1:${PORT:-8080}/actuator/health/readiness || exit 1
ENTRYPOINT ["/app/start.sh"]
