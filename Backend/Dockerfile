# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
# copy only necessary directories to leverage Docker cache for dependencies
COPY src ./src

# Build the project and create the runnable jar
RUN mvn -B -DskipTests package spring-boot:repackage

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the jar built in the previous stage
COPY --from=build /workspace/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Use a non-root user for better security
RUN addgroup --system appgroup && adduser --system appuser -G appgroup
USER appuser

# Configure environment variables recommendations
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
