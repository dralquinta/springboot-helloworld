package com.example.sample.microservice.demo_hello_world;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoHelloWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoHelloWorldApplication.class, args);
	}

@RestController
class MockController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}

}
