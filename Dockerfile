FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/adrec-1.0.0.jar app.jar
CMD ["java", "-jar", "app.jar"]
