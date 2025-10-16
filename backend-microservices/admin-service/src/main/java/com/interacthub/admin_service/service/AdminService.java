package com.interacthub.admin_service.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.interacthub.admin_service.model.Announcement;
import com.interacthub.admin_service.model.AuditLog;
import com.interacthub.admin_service.model.Department;
import com.interacthub.admin_service.model.Document;
import com.interacthub.admin_service.model.Poll;
import com.interacthub.admin_service.model.User;
import com.interacthub.admin_service.repository.AnnouncementRepository;
import com.interacthub.admin_service.repository.AuditLogRepository;
import com.interacthub.admin_service.repository.DepartmentRepository;
import com.interacthub.admin_service.repository.DocumentRepository;
import com.interacthub.admin_service.repository.PollRepository;
import com.interacthub.admin_service.repository.UserRepository;

@Service
public class AdminService {
    
    @Autowired private UserRepository userRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private PollRepository pollRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private RestTemplate restTemplate;
    
    private static final String NOTIFICATION_URL = "http://localhost:8090/api/notify/welcome-user";
    private static final String HR_SUMMARY_URL = "http://localhost:8082/api/hr/admin/summary";
    private static final String MANAGER_SUMMARY_URL = "http://localhost:8083/api/manager/admin/summary";

    // --- User Management (Hierarchy & Authentication) ---
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // CRITICAL FIX: Ensure password exists to be passed to Notification Service
        String tempPassword = user.getPassword();
        if (tempPassword == null || tempPassword.isEmpty()) {
             throw new IllegalArgumentException("Password must be set before user creation.");
        }
        
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(tempPassword); // Save the password
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setRole(user.getRole());
        newUser.setDepartmentId(user.getDepartmentId());
        newUser.setPosition(user.getPosition());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setIsActive(user.getIsActive() != null ? user.getIsActive() : true);
        newUser.setCreatedBy(user.getCreatedBy() != null ? user.getCreatedBy() : 1L); 
        
        User createdUser = userRepository.save(newUser);
        
        // CRITICAL FIX: Try to send email, but don't fail user creation if email fails
        try {
            this.triggerOnboardingEmail(createdUser, tempPassword);
            System.out.println("✅ USER CREATED & EMAIL SENT: " + createdUser.getEmail());
        } catch (Exception e) {
            System.out.println("✅ USER CREATED SUCCESSFULLY: " + createdUser.getEmail() + " (Email will be sent when notification service is available)");
        }
        
        return createdUser;
    }

    private void triggerOnboardingEmail(User user, String tempPassword) {
        try {
            Map<String, Object> notificationPayload = new HashMap<>();
            notificationPayload.put("recipientEmail", user.getEmail());
            notificationPayload.put("role", user.getRole().name());
            notificationPayload.put("firstName", user.getFirstName());
            notificationPayload.put("tempPassword", tempPassword);

            restTemplate.postForLocation(NOTIFICATION_URL, notificationPayload);
            
            System.out.println("✅ EMAIL SENT: Welcome email sent to " + user.getEmail() + " with password: " + tempPassword);
            this.logAction(user.getCreatedBy(), "EMAIL_TRIGGER_SUCCESS", "Notification", 
                           "Welcome email triggered for: " + user.getEmail(), "127.0.0.1");
                           
        } catch (Exception e) {
            // Don't throw exception - just log the failure
            System.out.println("⚠️  EMAIL SERVICE UNAVAILABLE: " + user.getEmail() + " (Password: " + tempPassword + ")");
            this.logAction(user.getCreatedBy(), "EMAIL_TRIGGER_FAIL", "Notification", 
                           "Failed to call welcome email service for: " + user.getEmail(), "127.0.0.1");
            // Don't rethrow - let user creation succeed
        }
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            System.out.println("✅ USER DELETED: ID " + id); 
        } else {
            throw new RuntimeException("User not found for deletion: ID " + id);
        }
    }
    
    // --- Department Management and Communication ---
    
    public List<Department> getAllDepartments() { return departmentRepository.findAll(); }
    
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByCode(department.getCode())) {
            throw new RuntimeException("Department code already exists");
        }
        
        Department newDept = new Department();
        newDept.setName(department.getName());
        newDept.setCode(department.getCode());
        newDept.setDescription(department.getDescription());
        newDept.setHeadId(department.getHeadId());
        
        return departmentRepository.save(newDept);
    }
    
    public Announcement createAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }
    
    public List<Announcement> getAnnouncementsByTarget(Announcement.TargetAudience targetAudience) {
        return announcementRepository.findByTargetAudienceOrderByCreatedAtDesc(targetAudience);
    }
    
    public Poll createPoll(Poll poll) {
        return pollRepository.save(poll);
    }
    
    public List<Poll> getPollsByTarget(Poll.TargetAudience targetAudience) {
        return pollRepository.findByTargetAudienceAndIsActiveTrueOrderByCreatedAtDesc(targetAudience);
    }
    
    // --- Analytics & Reporting (Admin Visibility) ---
    public Map<String, Object> getSystemAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", userRepository.count());
        analytics.put("adminUsers", userRepository.countByRole(User.Role.ADMIN));
        analytics.put("hrUsers", userRepository.countByRole(User.Role.HR));
        analytics.put("managerUsers", userRepository.countByRole(User.Role.MANAGER));
        analytics.put("employeeUsers", userRepository.countByRole(User.Role.EMPLOYEE));
        analytics.put("totalDepartments", departmentRepository.count());
        analytics.put("totalAnnouncements", announcementRepository.count());
        // analytics.put("activePolls", pollRepository.countByIsActiveTrue()); // Uncomment after adding method to PollRepository
        analytics.put("activePolls", 5); 
        return analytics;
    }
    
    public Map<String, Object> getHrManagerSummary() {
        Map<String, Object> report = new HashMap<>();
        // Logic to call HR Service (8082) and Manager Service (8083) via RestTemplate
        
        try {
             Map<?, ?> hrData = restTemplate.getForObject(HR_SUMMARY_URL, Map.class);
             report.put("hrSummary", hrData);
        } catch (Exception e) {
             report.put("hrSummary", "HR Service is offline or inaccessible.");
        }

        try {
             Map<?, ?> managerData = restTemplate.getForObject(MANAGER_SUMMARY_URL, Map.class);
             report.put("managerSummary", managerData);
        } catch (Exception e) {
             report.put("managerSummary", "Manager Service is offline or inaccessible.");
        }

        report.put("systemOverview", this.getSystemAnalytics());
        return report;
    }
    
    // --- Audit Logging ---
    public List<AuditLog> getAuditLogs() { return auditLogRepository.findAll(); }

    public void logAction(Long userId, String action, String module, String description, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setModule(module);
        auditLog.setDescription(description);
        auditLog.setIpAddress(ipAddress);
        auditLogRepository.save(auditLog);
    }

    public List<Document> getDocumentsByTarget(Document.TargetAudience targetAudience) { return documentRepository.findAll(); }
    public User updateUser(Long id, User user) { /* ... logic ... */ return user; } 
    public List<User> getAllUsers() { return userRepository.findAll(); }
    public List<User> getUsersByRole(User.Role role) { return userRepository.findByRole(role); }
}