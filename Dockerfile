FROM openjdk:17-jdk-slim AS builder
ARG JAR_FILE=build/libs/QuestionService-0.0.1.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]