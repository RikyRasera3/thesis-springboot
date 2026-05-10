FROM eclipse-temurin:26-jdk-alpine-3.23 AS build

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:26-jre-alpine-3.23 AS run

WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
