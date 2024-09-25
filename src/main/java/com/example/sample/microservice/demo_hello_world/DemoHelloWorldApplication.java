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
import java.util.Random;

@SpringBootApplication
public class DemoHelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoHelloWorldApplication.class, args);
    }

    @RestController
    class MockController {

        private final Random random = new Random();
        private static final Logger logger = LoggerFactory.getLogger(MockController.class);

        @GetMapping("/")
        public ResponseEntity<String> hello(WebRequest request) {
            try {
                // 50% chance of error
                if (random.nextInt(100) < 50) {
                    throw new RuntimeException("Simulated Internal Server Error");
                }

                // Successful response HTML with CSS
                String successHtml = "<html><head><style>" +
                        "body {font-family: Arial, sans-serif; text-align: center; padding: 50px;}" +
                        "h1 {color: green;}" +
                        "</style></head><body>" +
                        "<h1>Hello, World!</h1>" +
                        "<p>Your request was successful!</p>" +
                        "</body></html>";

                logger.info("Request successful: Hello, World!");
                return new ResponseEntity<>(successHtml, HttpStatus.OK);

            } catch (Exception ex) {
                // Log the error with full stack trace
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
