package com.ai.hackemotion.config;

import com.ai.hackemotion.entity.Role;
import com.ai.hackemotion.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner dataInitializer(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ADMIN") == null) {
                Role adminRole = Role.builder().name("ADMIN").build();
                roleRepository.save(adminRole);
            }

            if (roleRepository.findByName("USER") == null) {
                Role userRole = Role.builder().name("USER").build();
                roleRepository.save(userRole);
            }
        };
    }
}
