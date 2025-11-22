package com.interacthub.chat.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.interacthub.chat.model.ChatGroup;
import com.interacthub.chat.model.GroupMember;
import com.interacthub.chat.model.GroupMessage;
import com.interacthub.chat.repository.ChatGroupRepository;
import com.interacthub.chat.repository.GroupMemberRepository;
import com.interacthub.chat.repository.GroupMessageRepository;
import com.interacthub.chat.service.FileStorageService;

@RestController
@RequestMapping("/api/group")
@CrossOrigin(origins = "http://localhost:3000")
public class GroupChatController {

    private final ChatGroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private final GroupMessageRepository msgRepo;
    private final SimpMessagingTemplate broker;
    private final FileStorageService fileStorageService;

    public GroupChatController(ChatGroupRepository groupRepo, GroupMemberRepository memberRepo, 
                               GroupMessageRepository msgRepo, SimpMessagingTemplate broker,
                               FileStorageService fileStorageService) {
        this.groupRepo = groupRepo; 
        this.memberRepo = memberRepo; 
        this.msgRepo = msgRepo; 
        this.broker = broker;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/create")
    public Map<String,Object> createGroup(@RequestBody Map<String,Object> req) {
        String name = String.valueOf(req.getOrDefault("name","Group"));
        String createdByName = String.valueOf(req.getOrDefault("createdByName","User"));
        @SuppressWarnings("unchecked")
        List<String> members = (List<String>) req.getOrDefault("members", new ArrayList<>());

        ChatGroup g = new ChatGroup();
        g.setGroupId(UUID.randomUUID().toString());
        g.setName(name);
        g.setCreatedByName(createdByName);
        ChatGroup saved = groupRepo.save(g);

        for (String m : members) {
            if (m == null || m.trim().isEmpty()) continue;
            GroupMember gm = new GroupMember();
            gm.setGroupId(saved.getGroupId());
            gm.setMemberName(m.trim());
            memberRepo.save(gm);
        }
        return Map.of("groupId", saved.getGroupId(), "name", saved.getName());
    }

    // Health check for upload endpoint
    @GetMapping("/upload-file/test")
    public ResponseEntity<Map<String, String>> testUploadEndpoint() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Upload endpoint is accessible"));
    }

    // File upload endpoint (MUST be before path variable routes to avoid conflicts)
    @PostMapping("/upload-file")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("groupId") String groupId,
            @RequestParam("senderName") String senderName,
            @RequestParam(value = "content", required = false, defaultValue = "") String content) {
        System.out.println("📤 File upload received");
        System.out.println("   - groupId: " + groupId);
        System.out.println("   - senderName: " + senderName);
        System.out.println("   - fileName: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("   - fileSize: " + (file != null ? file.getSize() : 0));
        System.out.println("   - content: " + content);
        
        try {
            if (file == null || file.isEmpty()) {
                System.err.println("❌ File is null or empty");
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty or not provided"));
            }
            
            if (groupId == null || groupId.isBlank()) {
                System.err.println("❌ groupId is null or blank");
                return ResponseEntity.badRequest().body(Map.of("error", "groupId is required"));
            }
            FileStorageService.FileUploadResult uploadResult = fileStorageService.storeFile(file);
            
            // Create and save message with file
            GroupMessage msg = new GroupMessage();
            msg.setGroupId(groupId);
            msg.setSenderName(senderName);
            msg.setContent(content.isEmpty() ? "📎 " + uploadResult.getOriginalFileName() : content);
            msg.setFileUrl(uploadResult.getFileUrl());
            msg.setFileName(uploadResult.getOriginalFileName());
            msg.setFileType(uploadResult.getFileType());
            msg.setFileSize(uploadResult.getFileSize());
            
            GroupMessage saved = msgRepo.save(msg);
            broker.convertAndSend("/topic/group."+saved.getGroupId(), saved);
            
            System.out.println("✅ File uploaded successfully - messageId: " + saved.getId() + ", fileUrl: " + uploadResult.getFileUrl());
            
            return ResponseEntity.ok(Map.of(
                "messageId", saved.getId(),
                "fileUrl", uploadResult.getFileUrl(),
                "fileName", uploadResult.getOriginalFileName(),
                "message", "File uploaded successfully"
            ));
        } catch (Exception e) {
            System.err.println("❌ Error uploading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage(), "detail", e.getClass().getSimpleName()));
        }
    }
    
    @GetMapping("/{groupId}/members")
    public List<GroupMember> members(@PathVariable String groupId) {
        return memberRepo.findByGroupId(groupId);
    }

    @GetMapping("/{groupId}/history")
    public List<GroupMessage> history(@PathVariable String groupId) {
        return msgRepo.findByGroupIdOrderBySentAtAsc(groupId);
    }
    
    // File download endpoint
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = fileStorageService.loadFileAsBytes(fileName);
            ByteArrayResource resource = new ByteArrayResource(fileContent);
            
            // Determine content type
            String contentType = "application/octet-stream";
            if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileName.toLowerCase().endsWith(".pdf")) {
                contentType = "application/pdf";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // STOMP send: /app/group.send
    @MessageMapping("/group.send")
    public void send(GroupMessage msg) {
        if (msg.getSenderName() == null || msg.getSenderName().isBlank()) msg.setSenderName("User");
        if (msg.getGroupId() == null || msg.getGroupId().isBlank()) return;
        GroupMessage saved = msgRepo.save(msg);
        broker.convertAndSend("/topic/group."+saved.getGroupId(), saved);
    }
}
