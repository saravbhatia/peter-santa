package com.tripactions.taCallBot.features.helloWorld;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/taCallBot/helloWorld")
public class HelloWorldController {
    @GetMapping
    public String getHelloWorld() {
        log.info("Hello world check.");
        return "Hello ta-call-bot";
    }
}
