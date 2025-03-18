package com.myBusiness;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "My Business API", version = "1.0", description = "API for My Business inventory management"))
public class MyBusinessApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBusinessApplication.class, args);
    }
}
