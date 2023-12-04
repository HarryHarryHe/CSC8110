FROM openjdk:17-jdk-slim

LABEL authors="Harry"

WORKDIR /myProject

COPY . /myProject

RUN ./mvnw clean install

EXPOSE 8080

CMD ["java", "-jar", "target/loadGenerator-0.0.1-SNAPSHOT.jar"]