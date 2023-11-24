FROM ubuntu:latest
LABEL authors="Harry"

ENTRYPOINT ["top", "-b"]

FROM openjdk:11-jdk-slim

WORKDIR /myProject

COPY . /myProject

RUN ./mvnw clean install

EXPOSE 8080

CMD ["java", "-jar", "target/loadGenerator-0.0.1-SNAPSHOT.jar"]