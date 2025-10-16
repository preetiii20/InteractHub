package com.interacthub.manager.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "manager_announcements")
public class ManagerAnnouncement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    
    @Column(name = "manager_id", nullable = false)
    private Long managerId;
    
    @Column(name = "department_id")
    private Long departmentId; 
    
    @Enumerated(EnumType.STRING)
    private TargetAudience targetAudience = TargetAudience.DEPARTMENT_EMPLOYEE; 
    
    @Column(name = "is_pinned")
    private Boolean isPinned = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TargetAudience { 
        ALL_IN_DEPT,        
        DEPARTMENT_EMPLOYEE, 
        PROJECT_MEMBERS,     
        HR,                  
        MANAGER,             
        EMPLOYEE             
    }

    // Manual Getters/Setters 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }
    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}