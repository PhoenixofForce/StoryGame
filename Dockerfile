FROM eclipse-temurin:21-jre-alpine

FROM openjdk:21-jdk
EXPOSE 8080
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]