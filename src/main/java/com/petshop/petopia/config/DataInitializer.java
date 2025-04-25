package com.petshop.petopia.config;

import com.petshop.petopia.model.user.Role;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.RoleRepository;
import com.petshop.petopia.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ADMIN");
            return roleRepository.save(newRole);
        });

        if (userRepository.findByEmail("admin@petopia.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@petopia.com");
            admin.setPhone("0000000000");
            admin.setAddress("Petopia HQ");
            admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
            admin.setRoles(new HashSet<>() {{
                add(adminRole);
            }});
            admin.setCreatedAt(new Date());
            admin.setUpdatedAt(new Date());
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("Created default admin user: admin@petopia.com / admin123");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
