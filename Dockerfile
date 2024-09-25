FROM openjdk:17-jdk-slim

# Copy the JAR file
RUN ls -lash ./target
COPY ./target/demo-hello-world-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]