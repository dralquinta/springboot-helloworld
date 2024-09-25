FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/demo-hello-world-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/demo-hello-world-0.0.1-SNAPSHOT.jar"]