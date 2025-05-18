FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY target/api-bancaria-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]