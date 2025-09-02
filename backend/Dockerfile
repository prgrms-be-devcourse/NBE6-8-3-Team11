# Multi-stage build for optimized image size and faster builds
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

# Copy gradle wrapper and build files first (for better caching)
COPY gradlew ./
COPY gradle ./gradle/
COPY build.gradle.kts ./
COPY settings.gradle.kts ./

# Download dependencies first (cached layer unless dependencies change)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copy source code (frequently changing layer)
COPY src ./src/

# Build application with optimizations
RUN ./gradlew build -x test --no-daemon --parallel --build-cache

# Runtime stage with optimizations
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install Redis and curl in single layer to reduce build time
RUN apk add --no-cache curl redis && \
    echo '#!/bin/sh' > /app/start.sh && \
    echo 'redis-server --daemonize yes --bind 127.0.0.1 --port 6379' >> /app/start.sh && \
    echo 'sleep 1' >> /app/start.sh && \
    echo 'exec java -server -XX:+UseContainerSupport -XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Copy built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Optimized health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=2 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set fly profile for Fly.io deployment
ENV SPRING_PROFILES_ACTIVE=fly

# Start Redis first, then Spring Boot
CMD ["/app/start.sh"]
