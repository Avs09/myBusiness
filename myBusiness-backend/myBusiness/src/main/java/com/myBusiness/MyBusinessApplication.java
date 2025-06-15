package com.myBusiness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Configuration
@EnableAsync

 @EnableJpaAuditing(auditorAwareRef = "auditorProvider")
 public class MyBusinessApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBusinessApplication.class, args);
    }
 }