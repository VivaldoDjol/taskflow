# syntax=docker/dockerfile:1

# Stage 1: build the Spring Boot jar with Maven.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Prime the dependency cache first so code changes don't re-pull the world.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# Stage 2: slim runtime image with just the JRE and the jar.
FROM eclipse-temurin:17-jre
WORKDIR /app


COPY --from=build /build/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
