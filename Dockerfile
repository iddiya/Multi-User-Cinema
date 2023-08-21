# https://spring.io/guides/gs/spring-boot-docker/
FROM openjdk:17-alpine
ARG JAR_FILE=jar/ecinema-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]