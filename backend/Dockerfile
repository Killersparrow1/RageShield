FROM gradle:8.13-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre-jammy
COPY --from=build /home/gradle/src/build/libs/*.jar /app/rageshield.jar
ENTRYPOINT ["java", "-jar", "/app/rageshield.jar"]
