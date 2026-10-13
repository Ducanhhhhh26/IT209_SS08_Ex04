# Stage 1: Build the application
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy gradle wrapper and build files first for layer caching
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source and build
COPY src/ src/
RUN gradle build --no-daemon -x test

# Stage 2: Runtime image (slim JRE)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy compiled classes from builder
COPY --from=builder /app/build/classes/java/main/ ./classes/

# Default command
CMD ["java", "-cp", "classes", "Main"]
