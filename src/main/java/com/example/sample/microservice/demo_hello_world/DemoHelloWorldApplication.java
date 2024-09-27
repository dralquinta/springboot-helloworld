package com.example.sample.microservice.demo_hello_world;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
public class DemoHelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoHelloWorldApplication.class, args);
    }

    @RestController
    class MockController {

        private static final Logger logger = LoggerFactory.getLogger(MockController.class);

        // Track application start time
        private final Instant applicationStartTime = Instant.now();
        // Read the degradation time from the environment variable (with a default)
        private final long DEGRADE_AFTER_SECONDS = Long.parseLong(
                System.getenv().getOrDefault("DEGRADE_AFTER_SECONDS", "120"));

        @GetMapping("/")
        public ResponseEntity<String> hello(WebRequest request) {
			logger.debug("Degradation "+DEGRADE_AFTER_SECONDS);
            try {
                // Calculate the elapsed time since the application started
                Duration elapsedTime = Duration.between(applicationStartTime, Instant.now());

                // Check if elapsed time is greater than or equal to 2 minutes (120 seconds)
                if (elapsedTime.getSeconds() >= DEGRADE_AFTER_SECONDS) {
                    // After 2 minutes, always return 500 error
                    throw new RuntimeException("Simulated Internal Server Error after degradation period");
                }

                // Successful response HTML with CSS
                String successHtml = "<html><head><style>" +
                        "body {font-family: Arial, sans-serif; text-align: center; padding: 50px;}" +
                        "h1 {color: green;}" +
                        "</style></head><body>" +
                        "<h1>Hello, World!</h1>" +
                        "<p>Your request was successful!</p>" +
                        "</body></html>";

                // Log the successful request as INFO in GCP Cloud Logging
                logger.info("Request successful: Hello, World! "+elapsedTime.getSeconds());
                return new ResponseEntity<>(successHtml, HttpStatus.OK);

            } catch (Exception ex) {
                // Log the error with full stack trace as ERROR in GCP Cloud Logging
                logger.error("Internal Server Error occurred", ex);

                // Capture the stack trace
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String stackTrace = sw.toString();

                // Error response HTML with CSS and stack trace
                String errorHtml = "<html><head><style>" +
                        "body {font-family: Arial, sans-serif; background-color: #f2dede; padding: 50px;}" +
                        "h1 {color: red;}" +
                        "pre {background-color: #f9f2f4; border: 1px solid #e5e5e5; padding: 15px;}" +
                        "</style></head><body>" +
                        "<h1>500 Internal Server Error</h1>" +
                        "<p>Something went wrong on the server. Please see the details below:</p>" +
                        "<pre>" + stackTrace + "</pre>" +
                        "</body></html>";

                return new ResponseEntity<>(errorHtml, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
