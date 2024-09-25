package com.example.sample.microservice.demo_hello_world;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        public ResponseEntity<String> hello() {
            // Introduce a 50% chance of returning a 500 error
            try {
                if (random.nextInt(100) < 50) {
                    throw new RuntimeException("Simulated Internal Server Error");
                }
                // Log successful requests to stdout
                logger.info("Request successful: Hello, World!");
                return new ResponseEntity<>("Hello, World!", HttpStatus.OK);
            } catch (Exception ex) {
                // Log the full stack trace to stderr
                logger.error("Internal Server Error occurred", ex);
                return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
