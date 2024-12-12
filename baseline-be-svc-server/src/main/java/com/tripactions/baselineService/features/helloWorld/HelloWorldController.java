package com.tripactions.baselineService.features.helloWorld;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/baselineService/helloWorld")
public class HelloWorldController {
    @GetMapping
    public String getHelloWorld() {
        log.info("Hello world check.");
        return "Hello baseline-be-svc";
    }
}
