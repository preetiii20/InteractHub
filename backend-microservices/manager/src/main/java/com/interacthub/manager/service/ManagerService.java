package com.interacthub.manager.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.interacthub.manager.model.ManagerAnnouncement;
import com.interacthub.manager.model.ManagerPoll;
import com.interacthub.manager.model.Project;
import com.interacthub.manager.model.Task;
import com.interacthub.manager.repository.ManagerAnnouncementRepository;
import com.interacthub.manager.repository.ManagerPollRepository;
import com.interacthub.manager.repository.ProjectRepository;
import com.interacthub.manager.repository.TaskRepository; 

@Service
public class ManagerService {

    // --- Final Fields for Dependency Injection ---
    private final RestTemplate restTemplate;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ManagerAnnouncementRepository announcementRepository;
    private final ManagerPollRepository pollRepository;

    // --- Configuration Properties ---
    private final String adminServiceUrl; 
    private final String chatServiceUrl; 

    // --- CONSTRUCTOR INJECTION (FIXED AND COMPLETE) ---
    public ManagerService(
        RestTemplate restTemplate, 
        ProjectRepository projectRepository, 
        TaskRepository taskRepository,
        ManagerAnnouncementRepository announcementRepository, 
        ManagerPollRepository pollRepository,
        @Value("${admin.service.url}") String adminServiceUrl,
        @Value("${chat.service.url}") String chatServiceUrl) {
        
        this.restTemplate = restTemplate;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.announcementRepository = announcementRepository;
        this.pollRepository = pollRepository;
        this.adminServiceUrl = adminServiceUrl;
        this.chatServiceUrl = chatServiceUrl;
    }
    
    // Helper Method: Generates secure, temporary password
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    // --- 1. Employee Creation (Hierarchical) ---
    public Map<String, Object> createEmployeeAccount(Map<String, Object> employeeData, Long managerId, Long departmentId) {
        
        if (!employeeData.containsKey("email") || !employeeData.containsKey("firstName")) {
            throw new IllegalArgumentException("Employee data must contain email and first name.");
        }
        
        String tempPassword = generateTemporaryPassword();
        
        // PAYLOAD for Admin Service (Saving to DB)
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("email", employeeData.get("email"));
        userPayload.put("password", tempPassword); // CRITICAL: SAVING TEMP PASSWORD TO DB
        userPayload.put("firstName", employeeData.get("firstName"));
        userPayload.put("lastName", employeeData.get("lastName"));
        userPayload.put("role", "EMPLOYEE"); 
        userPayload.put("departmentId", departmentId); 
        userPayload.put("createdBy", managerId); 

        try {
            // Call Admin Service's User Create API
            ResponseEntity<Map> adminResponse = restTemplate.postForEntity(
                adminServiceUrl + "/users", userPayload, Map.class);
            
            if (adminResponse.getStatusCode().is2xxSuccessful()) {
                // PAYLOAD for Notification Service (Sending Email)
                Map<String, Object> notifyPayload = new HashMap<>();
                notifyPayload.put("recipientEmail", employeeData.get("email"));
                notifyPayload.put("role", "EMPLOYEE");
                notifyPayload.put("firstName", employeeData.get("firstName"));
                notifyPayload.put("tempPassword", tempPassword); // ✅ CRITICAL: PASSWORD IS SENT TO EMAIL SERVICE

                // Call Notification Service (Port 8090)
                restTemplate.postForLocation("http://localhost:8090/api/notify/welcome-user", notifyPayload);

                return Map.of("message", "Employee created successfully. Credentials sent via email.");
            } else {
                throw new RuntimeException("Admin service rejected user creation.");
            }
        } catch (Exception e) {
            System.err.println("Error calling Admin/Notification Service: " + e.getMessage());
            throw new RuntimeException("Failed to create employee account: Check Admin/Notification Services.");
        }
    }
    
    // --- 2. Project Management & Chat Integration ---

    public Project createProject(Project project) {
        Project newProject = projectRepository.save(project);
        this.createProjectChatChannel(newProject.getId(), newProject.getTitle());
        return newProject;
    }

    public List<Project> getManagerProjects(Long managerId) { return List.of(); }

    public Task assignTask(Task task) { return taskRepository.save(task); }
    
    // --- 3. Communication Methods (Manager's Comms & Global Read) ---
    
    public ManagerAnnouncement createLocalAnnouncement(ManagerAnnouncement announcement) {
        return announcementRepository.save(announcement);
    }
    
    public ManagerPoll createLocalPoll(ManagerPoll poll) {
        return pollRepository.save(poll);
    }
    
    public List<?> getGlobalAnnouncements(String role) {
        try {
            String url = adminServiceUrl + "/announcements/target/" + role;
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Could not fetch global announcements from Admin Service: " + e.getMessage());
            return List.of(Map.of("error", "Admin Comms Service Unreachable"));
        }
    }
    
    public void postInteraction(Long userId, String entityType, Long entityId, String comment) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("senderId", userId);
            payload.put("entityType", entityType);
            payload.put("entityId", entityId);
            payload.put("content", comment);
            payload.put("type", "COMMENT"); 

            restTemplate.postForLocation("http://localhost:8085/api/chat/interactions/post", payload);
            
            System.out.println("✅ Interaction posted and pushed live.");
        } catch (Exception e) {
            System.err.println("Chat Service Offline. Could not post interaction.");
        }
    }

    // --- 4. Internal: Chat Channel Creation ---

    private void createProjectChatChannel(Long projectId, String projectTitle) {
        try {
            Map<String, Object> chatPayload = new HashMap<>();
            chatPayload.put("channelId", "PROJECT_" + projectId);
            chatPayload.put("channelName", projectTitle);
            
            restTemplate.postForLocation("http://localhost:8085/api/chat/channels/create", chatPayload);
            
            System.out.println("✅ Project Chat Channel push successful: " + projectTitle);
        } catch (Exception e) {
            System.err.println("Chat Service Offline (8085). Could not create live channel.");
        }
    }

    // --- 5. Admin Visibility Reporting ---

    public Map<String, Object> getManagerAdminSummary() {
        long totalProjects = projectRepository.count();
        // Placeholder values 
        return Map.of(
            "totalProjects", totalProjects,
            "tasksInProgress", 5, 
            "projectsCompleted", 2
        );
    }
}