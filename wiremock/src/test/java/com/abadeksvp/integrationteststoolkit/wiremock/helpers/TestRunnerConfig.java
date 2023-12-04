package com.abadeksvp.integrationteststoolkit.wiremock.helpers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestRunnerConfig {

    public static void main(String[] args) {
        SpringApplication.run(TestRunnerConfig.class, args);
    }
}
