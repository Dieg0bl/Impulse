# Dockerfile for IMPULSE LEAN Backend - Production Ready
# Phase 12: Complete Java Spring Boot containerization

# =============================================================================
# MULTI-STAGE BUILD FOR OPTIMIZATION
# =============================================================================

# Stage 1: Build Environment
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Install build dependencies
RUN apk add --no-cache curl g++ git make python3

# Copy Maven wrapper and pom.xml
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests -B

# =============================================================================
# Stage 2: Production Image
# =============================================================================

FROM eclipse-temurin:17-jre-alpine AS production

# Install security updates and runtime dependencies
RUN apk update && \
    apk upgrade && \
    apk add --no-cache curl tini && \
    rm -rf /var/cache/apk/*

# Create application user
RUN addgroup -g 1001 -S impulse && \
    adduser -S -D -H -u 1001 -h /app -s /sbin/nologin -G impulse -g impulse impulse

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/impulse-backend-*.jar app.jar

# Copy application configuration
COPY docker/application-docker.yml application.yml
COPY docker/logback-docker.xml logback.xml

# Add health check script
COPY scripts/health-check.sh /usr/local/bin/health-check.sh
RUN chmod +x /usr/local/bin/health-check.sh

# Set ownership
RUN chown -R impulse:impulse /app && \
    chown impulse:impulse /usr/local/bin/health-check.sh

# Switch to non-root user
USER impulse

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD /usr/local/bin/health-check.sh

# Use tini as PID 1 for proper signal handling
ENTRYPOINT ["/sbin/tini", "--"]

# Start application with optimized JVM settings
CMD ["java", \
     "-XX:+UseG1GC", \
     "-XX:+UseContainerSupport", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:+HeapDumpOnOutOfMemoryError", \
     "-XX:HeapDumpPath=/app/heapdumps/", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-Dspring.profiles.active=docker", \
     "-jar", "app.jar"]

# =============================================================================
# LABELS FOR METADATA
# =============================================================================

LABEL maintainer="IMPULSE LEAN Team"
LABEL version="1.0.0"
LABEL description="IMPULSE LEAN Backend - Spring Boot API"
LABEL org.opencontainers.image.title="IMPULSE LEAN Backend"
LABEL org.opencontainers.image.description="Backend API for IMPULSE LEAN habit challenge platform"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.vendor="IMPULSE LEAN"
LABEL org.opencontainers.image.licenses="MIT"
