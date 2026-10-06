package com.arco.secure_student_management.config;

import com.arco.secure_student_management.user.Role;
import com.arco.secure_student_management.user.User;
import com.arco.secure_student_management.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();

                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(
                        passwordEncoder.encode("Admin@123")
                );
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);

                userRepository.save(admin);
            }

            if (!userRepository.existsByUsername("user")) {

                User user = new User();

                user.setUsername("user");
                user.setEmail("user@example.com");
                user.setPassword(
                        passwordEncoder.encode("User@123")
                );
                user.setRole(Role.USER);
                user.setEnabled(true);

                userRepository.save(user);
            }
        };
    }
}
