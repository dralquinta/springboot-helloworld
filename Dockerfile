FROM openjdk:17-jdk-slim
RUN ls -lash
RUN ls -lash ./target
COPY ./target/demo-hello-world-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]