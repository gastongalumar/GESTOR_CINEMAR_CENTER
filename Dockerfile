# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# El pom.xml vive en Back/ y su sourceDirectory es backend/src/main/java.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY backend/src ./backend/src
RUN ./mvnw -B -q -DskipTests package

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache wget tzdata \
    && addgroup -S app \
    && adduser -S app -G app

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

RUN mkdir -p /app/uploads \
    && chown -R app:app /app

USER app

ENV TZ=America/Argentina/Buenos_Aires \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom" \
    UPLOAD_DIR=/app/uploads \
    SERVER_PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=10 \
    CMD wget -q -O /dev/null http://127.0.0.1:8080/api/peliculas/vigentes || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
