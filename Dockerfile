FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY build.gradle settings.gradle gradle.properties ./
COPY src src

RUN chmod +x ./gradlew && ./gradlew build --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar todo el contenido del quarkus-app
COPY --from=build /app/build/quarkus-app/lib/ /app/lib/
COPY --from=build /app/build/quarkus-app/app/ /app/app/
COPY --from=build /app/build/quarkus-app/quarkus/ /app/quarkus/
COPY --from=build /app/build/quarkus-app/quarkus-run.jar /app/

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "-Dquarkus.http.host=0.0.0.0", "/app/quarkus-run.jar"]