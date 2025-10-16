package com.interacthub.admin_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetAudience targetAudience;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "target_department_id")
    private Long targetDepartmentId;
    @Column(name = "is_pinned")
    private Boolean isPinned = false;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum AnnouncementType { GENERAL, URGENT, POLICY, EVENT, UPDATE }
    public enum TargetAudience { ALL, HR, MANAGER, EMPLOYEE, DEPARTMENT }
    
    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public AnnouncementType getType() { return type; }
    public void setType(AnnouncementType type) { this.type = type; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Long getTargetDepartmentId() { return targetDepartmentId; }
    public void setTargetDepartmentId(Long targetDepartmentId) { this.targetDepartmentId = targetDepartmentId; }
    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}