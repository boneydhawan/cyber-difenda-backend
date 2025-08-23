# Use OpenJDK 17 as base
FROM eclipse-temurin:17-jdk-alpine as builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper & pom.xml first (for caching deps)
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Install Maven deps (skip tests)
RUN ./mvnw dependency:go-offline -B

# Copy project files
COPY src ./src

# Build Spring Boot jar
RUN ./mvnw clean package -DskipTests

# ================================
# Run stage
# ================================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
