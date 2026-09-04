FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build

FROM eclipse-temurin:25-jre

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/build/libs/deploy_render-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]