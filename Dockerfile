FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/testtask-backend.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
