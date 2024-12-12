package com.tripactions.baselineService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@PropertySource(value = "classpath:/git.properties", ignoreResourceNotFound = true)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        log.info("Testing logstash configurations.");
        SpringApplication.run(Application.class);
    }
}