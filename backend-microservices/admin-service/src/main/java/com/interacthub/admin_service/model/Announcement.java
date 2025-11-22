package com.interacthub.admin_service.model;

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
@Table(name = "announcements")
public class Announcement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false) private String title;

    @Column(nullable=false, columnDefinition="TEXT") private String content;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private AnnouncementType type;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private TargetAudience targetAudience;

    @Column(name="created_by_name", nullable=false)
    private String createdByName;

    @Column(name="created_at") private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public enum AnnouncementType { GENERAL, URGENT, POLICY, EVENT, UPDATE }
    public enum TargetAudience { ALL, HR, MANAGER, EMPLOYEE, DEPARTMENT, ADMIN }

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; } public void setContent(String content) { this.content = content; }
    public AnnouncementType getType() { return type; } public void setType(AnnouncementType type) { this.type = type; }
    public TargetAudience getTargetAudience() { return targetAudience; } public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }
    public String getCreatedByName() { return createdByName; } public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
