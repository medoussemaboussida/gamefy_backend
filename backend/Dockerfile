# Use official Eclipse Temurin (OpenJDK) base image
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set working directory
WORKDIR /app

# Copy only the build files for caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build the application (produces a JAR)
RUN ./mvnw clean package -DskipTests

# Final runtime image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set default environment variables
# ENV MONGODB_URI="mongodb://gamefy:gamefy2025@mongodb:27017/gamefy?authSource=gamefy" \
#     JAVA_OPTS="-Xmx512m -Dspring.profiles.active=prod"

# Expose port (should match your server.port)
EXPOSE 8080

# Health check (adjust endpoint as needed)
# HEALTHCHECK --interval=30s --timeout=3s \
#   CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run as non-root user for security
RUN useradd -m gamefy && chown -R gamefy:gamefy /app
USER gamefy

# Entry point (use shell form for env variable expansion)
ENTRYPOINT ["java", "-jar", "app.jar"]