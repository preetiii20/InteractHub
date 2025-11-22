package com.interacthub.admin_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.interacthub.admin_service.model.User;
import com.interacthub.admin_service.repository.UserRepository;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Manual field setting uses the fixed getters/setters
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
        newUser.setFirstName(user.getFirstName() != null ? user.getFirstName() : "Default");
        newUser.setLastName(user.getLastName() != null ? user.getLastName() : "User");
        newUser.setRole(user.getRole() != null ? user.getRole() : User.Role.EMPLOYEE);
        newUser.setIsActive(true);
        newUser.setCreatedBy(user.getCreatedBy());
        
        return userRepository.save(newUser);
    }
    
    public Map<String, Object> login(String email, String password) {
        try {
            // Simple admin login - create user if doesn't exist
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;
            
            if (userOptional.isPresent()) {
                user = userOptional.get();
                // Check password - try both plain text and hashed
                boolean passwordMatch = user.getPassword().equals(password) || 
                                      passwordEncoder.matches(password, user.getPassword());
                if (!passwordMatch) {
                    throw new RuntimeException("Invalid credentials");
                }
            } else {
                // Create admin user automatically
                if ("admin@interacthub.com".equals(email) && "admin123".equals(password)) {
                    user = new User();
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setFirstName("Admin");
                    user.setLastName("User");
                    user.setRole(User.Role.ADMIN);
                    user.setIsActive(true);
                    user.setCreatedBy(1L);
                    user = userRepository.save(user);
                } else {
                    throw new RuntimeException("Invalid credentials");
                }
            }
            
            // Generate simple token (not JWT for now to avoid library conflicts)
            String token = "token-" + System.currentTimeMillis() + "-" + email;
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("role", user.getRole().name());
            
            return response;
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid credentials: " + e.getMessage());
        }
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}