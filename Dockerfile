FROM maven:3.8.4-openjdk-17-slim AS build

COPY pom.xml ./
COPY .mvn .mvn
RUN mvn clean install

COPY src src
RUN mvn package

FROM openjdk:17-jdk-slim
WORKDIR /home/app
COPY --from=build target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]