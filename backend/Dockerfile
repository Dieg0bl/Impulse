FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Add application user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Copy the JAR file
COPY target/impulse-lean-backend-*.jar app.jar

# Change ownership of the app directory
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
