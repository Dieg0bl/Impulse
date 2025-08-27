## Multi-stage build for backend (Maven + Spring Boot)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY backend/pom.xml ./
COPY backend/src ./src
RUN mvn -f pom.xml -DskipTests package -DskipITs

FROM eclipse-temurin:21-jre
ARG JAR_FILE=/workspace/target/*.jar
COPY --from=builder /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
