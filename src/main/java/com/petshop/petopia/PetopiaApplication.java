package com.petshop.petopia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetopiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetopiaApplication.class, args);
	}
}
