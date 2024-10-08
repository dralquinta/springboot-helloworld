FROM openjdk:17-jdk-slim

# Copy the JAR file
COPY ./target/demo-hello-world-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]