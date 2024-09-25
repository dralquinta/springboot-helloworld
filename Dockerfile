FROM openjdk:17-jdk-slim

# Copy the JAR file
ADD /home/dalquint/DevOps/springboot-helloworld/demo-hello-world-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]