# 단순화된 빠른 빌드용 Dockerfile
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew ./
COPY gradle ./gradle/
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY src ./src/

# 빠른 빌드 (복잡한 캐싱 제거)
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install Redis and curl in one layer
RUN apk add --no-cache curl redis && \
    echo '#!/bin/sh' > /app/start.sh && \
    echo 'redis-server --daemonize yes --bind 127.0.0.1 --port 6379' >> /app/start.sh && \
    echo 'sleep 1' >> /app/start.sh && \
    echo 'exec java -server -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Copy built jar
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# 빠른 헬스체크
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=2 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV SPRING_PROFILES_ACTIVE=fly

CMD ["/app/start.sh"]
