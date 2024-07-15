FROM openjdk:17-jdk-alpine
COPY build/libs/callback-handler-0.0.1-SNAPSHOT.jar callback-handler.jar
ENTRYPOINT ["java", "-jar", "/callback-handler.jar"]