package com.interacthub.admin_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interacthub.admin_service.model.Announcement;
import com.interacthub.admin_service.model.AuditLog;
import com.interacthub.admin_service.model.Department;
import com.interacthub.admin_service.model.Poll;
import com.interacthub.admin_service.model.User;
import com.interacthub.admin_service.service.AdminService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
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
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
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
    
    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id, @RequestHeader(value = "X-User-Name", required = false) String userName) {
        try {
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "User name is required"));
            }
            adminService.deleteAnnouncement(id, userName.trim());
            
            // Broadcast deletion to all connected clients
            messagingTemplate.convertAndSend("/topic/announcements.deleted", Map.of(
                "id", id,
                "type", "ANNOUNCEMENT",
                "deletedBy", userName.trim()
            ));
            
            return ResponseEntity.ok(Map.of("message", "Announcement deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/polls/{id}")
    public ResponseEntity<?> deletePoll(@PathVariable Long id, @RequestHeader(value = "X-User-Name", required = false) String userName) {
        try {
            if (userName == null || userName.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "User name is required"));
            }
            adminService.deletePoll(id, userName.trim());
            
            // Broadcast deletion to all connected clients
            messagingTemplate.convertAndSend("/topic/polls.deleted", Map.of(
                "id", id,
                "type", "POLL",
                "deletedBy", userName.trim()
            ));
            
            return ResponseEntity.ok(Map.of("message", "Poll deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
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
    
    // --- 5. New Admin Dashboard Endpoints ---
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
    
    @GetMapping("/audit/logs")
    public ResponseEntity<Map<String, Object>> getAuditLogsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAuditLogsPaginated(page, size));
    }
    
    @GetMapping("/monitoring")
    public ResponseEntity<Map<String, Object>> getSystemMonitoring() {
        return ResponseEntity.ok(adminService.getSystemMonitoring());
    }
}