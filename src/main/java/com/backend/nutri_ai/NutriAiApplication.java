package com.backend.nutri_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableJpaAuditing
public class NutriAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutriAiApplication.class, args);
	}

}
