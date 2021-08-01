package com.matching.mentoree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MentoreeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentoreeApplication.class, args);
	}

}
