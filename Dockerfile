# Use a base image with Java 21 and Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . .

# Build the application, skipping tests for a faster build
RUN mvn clean install -DskipTests

# Use a smaller base image for the final container
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
