package com.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);
    private int requestCount = 0;

    @GetMapping("/")
    public String home() {
        requestCount++;
        log.info("Request #{} - GET /", requestCount);
        return "CI/CD Pipeline v1.0 (request #" + requestCount + ")";
    }

    @GetMapping("/api/hello")
    public String hello() {
        log.info("Hello endpoint called");
        return "Hello from DevOps Demo!";
    }

    @GetMapping("/api/health")
    public String health() {
        return "{"status":"UP"}";
    }
}