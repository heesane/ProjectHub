package com.project.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjecthubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjecthubApplication.class, args);
	}

}
