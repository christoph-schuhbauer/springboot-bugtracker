FROM openjdk:17-jdk-alpine

EXPOSE 8080
WORKDIR /app

COPY target/bugtracker-0.0.1-SNAPSHOT.jar bugtracker-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","bugtracker-0.0.1-SNAPSHOT.jar"]