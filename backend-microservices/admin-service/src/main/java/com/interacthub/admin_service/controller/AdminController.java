package com.interacthub.admin_service.controller;

import com.interacthub.admin_service.model.*;
import com.interacthub.admin_service.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    // --- 1. User Management (CRUD) ---
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // This endpoint handles both initial Admin account creation and creating Manager/HR accounts.
        return ResponseEntity.ok(adminService.createUser(user));
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(adminService.updateUser(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User ID " + id + " deleted successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/users/role/{role}")
    public List<User> getUsersByRole(@PathVariable User.Role role) {
        return adminService.getUsersByRole(role);
    }
    
    // --- 2. Department Management ---
    
    @GetMapping("/departments")
    public List<Department> getAllDepartments() {
        return adminService.getAllDepartments();
    }
    
    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        try {
            return ResponseEntity.ok(adminService.createDepartment(department));
        } catch (RuntimeException e) {
             return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // --- 3. Global Communication Management (Announcements/Polls) ---
    
    @PostMapping("/announcements")
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        return ResponseEntity.ok(adminService.createAnnouncement(announcement));
    }
    
    @GetMapping("/announcements/target/{targetAudience}")
    public List<Announcement> getAnnouncementsByTarget(@PathVariable Announcement.TargetAudience targetAudience) {
        return adminService.getAnnouncementsByTarget(targetAudience);
    }
    
    @PostMapping("/polls")
    public ResponseEntity<Poll> createPoll(@RequestBody Poll poll) {
        return ResponseEntity.ok(adminService.createPoll(poll));
    }
    
    @GetMapping("/polls/target/{targetAudience}")
    public List<Poll> getPollsByTarget(@PathVariable Poll.TargetAudience targetAudience) {
        return adminService.getPollsByTarget(targetAudience);
    }
    
    // --- 4. Analytics & Reporting (Cross-Service Visibility) ---

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        return ResponseEntity.ok(adminService.getSystemAnalytics());
    }

    @GetMapping("/dashboard/full-report")
    public ResponseEntity<Map<String, Object>> getFullAdminReport() {
        // This method calls the Manager (8083) and HR (8082) services for visibility data
        return ResponseEntity.ok(adminService.getHrManagerSummary());
    }

    @GetMapping("/audit-logs")
    public List<AuditLog> getAuditLogs() {
        return adminService.getAuditLogs();
    }
}