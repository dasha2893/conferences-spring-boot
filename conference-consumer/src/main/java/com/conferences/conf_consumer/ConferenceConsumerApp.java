package com.conferences.conf_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.conferences.common.entity")
@EnableJpaRepositories(basePackages = "com.conferences.conf_consumer.repository")
public class ConferenceConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(ConferenceConsumerApp.class, args);
    }
}