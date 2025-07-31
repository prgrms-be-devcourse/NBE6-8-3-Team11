# Multi-stage build for optimized image size
FROM openjdk:21-jdk-slim as builder

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
FROM openjdk:21-jre-slim

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 10000

# Set production profile and run
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
