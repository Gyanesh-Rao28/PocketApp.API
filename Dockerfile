# 1. Matched to your Java 24 compilation target
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# 2. Renaming the JAR destination inside the container is perfectly fine!
COPY target/pocketManager-0.0.1-SNAPSHOT.jar pocketApp-V1.0.jar

# 3. Matched to Spring Boot's default internal port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "pocketApp-V1.0.jar"]