# Multi-stage build Dockerfile for HSBC Transaction Management System

# First stage: Build stage
FROM openjdk:21-jdk-slim as builder

# Set working directory
WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .

# Copy Maven Wrapper (if exists)
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (utilize Docker cache)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Second stage: Runtime stage
FROM openjdk:21-jre-slim

# Install necessary tools
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r hsbc && useradd -r -g hsbc hsbc

# Set working directory
WORKDIR /app

# Copy JAR file from build stage
COPY --from=builder /app/target/*.jar app.jar

# Change file ownership
RUN chown -R hsbc:hsbc /app

# Switch to non-root user
USER hsbc

# Expose port
EXPOSE 8080

# Set JVM parameters
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+PrintGC"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Startup command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Metadata labels
LABEL maintainer="HSBC Development Team <dev@hsbc.com>"
LABEL description="HSBC Transaction Management System"
LABEL version="1.0.0"
LABEL application="hsbc-transaction-management"
