# 🏗️ Stage 1: Build stage - Maven tho JAR create cheyyadam
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Source code ni correct folder nunchi copy cheyyadam
COPY timesheet/ .

# Tests skip chesi JAR build cheyyadam
RUN mvn clean package -DskipTests

# 🚀 Stage 2: Runtime stage - JAR ni run cheyyadam
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Build stage nunchi final JAR ni copy cheyyadam
COPY --from=build /app/target/*.jar app.jar

# Application port expose cheyyadam (8888)
EXPOSE 8888

# Spring Boot app ni run cheyyadam
ENTRYPOINT ["java", "-jar", "app.jar"]
