package com.Hintutor.Hinttutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HinttutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(HinttutorApplication.class, args);
	}

}
