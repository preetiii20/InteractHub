package com.interacthub.admin_service.config;

import com.interacthub.admin_service.model.User;
import com.interacthub.admin_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (!userRepository.existsByEmail("admin@interacthub.com")) {
            User admin = new User();
            admin.setEmail("admin@interacthub.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);
            admin.setIsActive(true);
            admin.setCreatedBy(1L);
            
            userRepository.save(admin);
            System.out.println("✅ Admin user created successfully!");
        } else {
            System.out.println("✅ Admin user already exists!");
        }
    }
}
