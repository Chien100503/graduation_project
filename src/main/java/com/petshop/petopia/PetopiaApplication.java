package com.petshop.petopia;

import com.petshop.petopia.model.Role;
import com.petshop.petopia.model.User;
import com.petshop.petopia.repository.RoleRepository;
import com.petshop.petopia.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class PetopiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetopiaApplication.class, args);
	}
}
