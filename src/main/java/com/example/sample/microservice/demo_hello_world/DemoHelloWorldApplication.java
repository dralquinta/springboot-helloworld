package com.example.sample.microservice.demo_hello_world;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Random;

@SpringBootApplication
public class DemoHelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoHelloWorldApplication.class, args);
    }

    @RestController
    class MockController {

        private final Random random = new Random();

        @GetMapping("/")
        public String hello() throws CustomInternalServerErrorException {
            // Introduce a 30% chance of throwing an exception
            if (random.nextInt(100) < 30) {
                throw new CustomInternalServerErrorException("Random Internal Server Error occurred!");
            }
            return "Hello, World!";
        }
    }

    // Custom exception to simulate 500 error
    class CustomInternalServerErrorException extends RuntimeException {
        public CustomInternalServerErrorException(String message) {
            super(message);
        }
    }

    // Global exception handler to catch the custom exception and return a 500 response
    @ControllerAdvice
    class GlobalExceptionHandler {

        @ExceptionHandler(CustomInternalServerErrorException.class)
        public ResponseEntity<String> handleCustomException(CustomInternalServerErrorException ex) {
            // Return a 500 error response with the exception message
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
