FROM openjdk:17-jdk-slim

# Print contents of the target/ directory to debug
RUN ls -l target/

# Copy the JAR file
COPY --from=build target/demo-hello-world-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]