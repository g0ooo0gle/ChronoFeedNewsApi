package com.github.g0ooo0gle.chronofeednewsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChronofeednewsapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChronofeednewsapiApplication.class, args);
	}

}
