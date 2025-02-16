package com.myBusiness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MyBusinessApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBusinessApplication.class, args);
	}

}
