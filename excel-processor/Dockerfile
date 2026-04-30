# Étape 1 : Build (Kanbniw l'fichier .jar b Maven w Java 21)
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
# Telechargement dyal les dependances (bach yb9aw f l'cache)
RUN mvn dependency:go-offline
COPY src ./src
# Build dyal l'application bla man-lanciw les tests
RUN mvn clean package -DskipTests

# Étape 2 : Run (Kan-lanciw l'app f image khfifa JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Kanjibou ghir l'fichier .jar mn l'étape 1 (bach tb9a l'image sghira)
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]