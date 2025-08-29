# Multi-stage build for optimized image size
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew ./
COPY gradle ./gradle/
COPY build.gradle.kts ./
COPY settings.gradle.kts ./

# Copy source code
COPY src ./src/

# Make gradlew executable and build
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install Redis and curl
RUN apk add --no-cache curl redis

# Create a startup script
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'redis-server --daemonize yes --bind 127.0.0.1 --port 6379' >> /app/start.sh && \
    echo 'sleep 2' >> /app/start.sh && \
    echo 'java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Copy built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set fly profile for Fly.io deployment
ENV SPRING_PROFILES_ACTIVE=fly

# Start Redis first, then Spring Boot
CMD ["/app/start.sh"]
