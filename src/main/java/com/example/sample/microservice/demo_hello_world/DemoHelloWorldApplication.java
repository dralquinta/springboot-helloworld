package com.example.sample.microservice.demo_hello_world;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoHelloWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoHelloWorldApplication.class, args);
	}

@RestController
class MockController {

    @GetMapping("/")
    public String hello() {
        return "Hello, World!";
    }

	// Redirect root (/) to /hello
        @RequestMapping("/")
        @ResponseBody
        public String redirectToHello() {
            return "redirect:/hello";
        }
}

}
