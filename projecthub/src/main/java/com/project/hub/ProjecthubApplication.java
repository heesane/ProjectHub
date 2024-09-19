package com.project.hub;

import com.project.hub.repository.document.ProjectDocumentsRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = {
				ProjectDocumentsRepository.class,
		}
))
@EnableCaching
public class ProjecthubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjecthubApplication.class, args);
	}

}
