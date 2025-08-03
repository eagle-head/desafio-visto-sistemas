# Multi-stage Dockerfile for Spring Boot application with Java 21
# Optimized for minimal image size and security

# Stage 1: Build application
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy Maven wrapper and pom.xml first for better layer caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Extract JAR layers for optimal caching
FROM eclipse-temurin:21-jdk-alpine AS optimizer
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Final runtime image
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks (curl not available in Alpine)
RUN apk add --no-cache wget

# Create non-root user for security
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -G spring -S -D -H spring

WORKDIR /app

# Copy JAR layers in order of change frequency (most stable first)
COPY --from=optimizer --chown=spring:spring /app/dependencies/ ./
COPY --from=optimizer --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=optimizer --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=optimizer --chown=spring:spring /app/application/ ./

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Health check endpoint
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM optimizations for containerized environment
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseZGC \
               -XX:+UseStringDeduplication \
               -XX:+UseContainerSupport \
               -Xlog:gc*:gc.log"

# Use shell form to allow environment variable expansion
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]