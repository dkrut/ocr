FROM maven:17-eclipse-temurin AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/ocr-1.0-SNAPSHOT.jar ./ocr.jar
COPY src/main/resources/tessdata ./src/main/resources/tessdata

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "ocr.jar"]