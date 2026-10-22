FROM openjdk:17-jdk-alpine


WORKDIR /springboot-rest-api
COPY target/springboot-rest-api-0.0.1-SNAPSHOT.jar app.jar

Expose 8080

CMD ["java", "-jar", "/springboot-rest-api/app.jar"]

