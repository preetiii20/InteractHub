package com.interacthub.admin_service.service;

import com.interacthub.admin_service.model.User;
import com.interacthub.admin_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Manual field setting uses the fixed getters/setters
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFirstName(user.getFirstName() != null ? user.getFirstName() : "Default");
        newUser.setLastName(user.getLastName() != null ? user.getLastName() : "User");
        newUser.setRole(user.getRole() != null ? user.getRole() : User.Role.EMPLOYEE);
        newUser.setIsActive(true);
        newUser.setCreatedBy(user.getCreatedBy()); // <-- FIXED
        
        return userRepository.save(newUser);
    }
    
    public Map<String, Object> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Simple password comparison (no security yet)
            if (password.equals(user.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("message", "Login successful");
                response.put("role", user.getRole().name());
                return response;
            }
        }
        throw new RuntimeException("Invalid credentials");
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}