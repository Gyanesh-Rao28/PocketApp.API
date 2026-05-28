package com.gyanesh.pocketManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PocketManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocketManagerApplication.class, args);
	}

}
