package com.interacthub.manager.controller;

import com.interacthub.manager.model.ManagerAnnouncement;
import com.interacthub.manager.model.ManagerPoll;
import com.interacthub.manager.model.Project;
import com.interacthub.manager.model.Task;
import com.interacthub.manager.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    // --- 1. Employee Creation Hierarchy (Manager -> Employee) ---

    @PostMapping("/employee/onboard")
    public ResponseEntity<?> onboardEmployee(@RequestBody Map<String, Object> payload) {
        // Manager ID and Department ID would be extracted from JWT (Future Step)
        // Hardcoded for testing: Manager ID 2, Dept ID 1
        Long managerId = ((Number) payload.getOrDefault("managerId", 2L)).longValue();
        Long departmentId = ((Number) payload.getOrDefault("departmentId", 1L)).longValue(); 
        
        try {
            Map<String, Object> response = managerService.createEmployeeAccount(payload, managerId, departmentId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- 2. Project Management ---

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return ResponseEntity.ok(managerService.createProject(project));
    }

    @GetMapping("/projects/my/{managerId}")
    public List<Project> getMyProjects(@PathVariable Long managerId) {
        return managerService.getManagerProjects(managerId);
    }

    @PostMapping("/tasks/assign")
    public ResponseEntity<Task> assignTask(@RequestBody Task task) {
        return ResponseEntity.ok(managerService.assignTask(task));
    }

    // --- 3. Manager Local Communication (Department Comms) ---

    @PostMapping("/announcements/local")
    public ResponseEntity<ManagerAnnouncement> createLocalAnnouncement(@RequestBody ManagerAnnouncement announcement) {
        // Manager ID and Department ID are assumed to be set inside the service logic or validated here
        return ResponseEntity.ok(managerService.createLocalAnnouncement(announcement));
    }

    @PostMapping("/polls/local")
    public ResponseEntity<ManagerPoll> createLocalPoll(@RequestBody ManagerPoll poll) {
        return ResponseEntity.ok(managerService.createLocalPoll(poll));
    }
    
    @GetMapping("/comms/global")
    public List<?> getGlobalComms() {
        // Calls Admin Service (8081) to fetch comms targeted to MANAGER or ALL
        return managerService.getGlobalAnnouncements("MANAGER");
    }

    // --- 4. Interaction (Reacting to Polls/Announcements) ---
    
    @PostMapping("/interaction/comment")
    public ResponseEntity<?> postComment(@RequestBody Map<String, Object> payload) {
        // Hardcoded userId 2L for testing (Manager's own ID)
        Long userId = ((Number) payload.getOrDefault("userId", 2L)).longValue();
        String entityType = (String) payload.get("entityType");
        Long entityId = ((Number) payload.get("entityId")).longValue();
        String comment = (String) payload.get("comment");
        
        // This method calls the Chat Service (8085) to save the interaction and push it live
        managerService.postInteraction(userId, entityType, entityId, comment);
        return ResponseEntity.ok(Map.of("message", "Interaction posted successfully."));
    }

    // --- 5. Admin Visibility Endpoint (Called by Admin Service 8081) ---

    @GetMapping("/admin/summary")
    public ResponseEntity<Map<String, Object>> getAdminSummary() {
        return ResponseEntity.ok(managerService.getManagerAdminSummary());
    }
}