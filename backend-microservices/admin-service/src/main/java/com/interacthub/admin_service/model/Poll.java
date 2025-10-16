package com.interacthub.admin_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "polls")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String question;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "poll_options", joinColumns = @JoinColumn(name = "poll_id"))
    @Column(name = "option_text")
    private List<String> options;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetAudience targetAudience;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "target_department_id")
    private Long targetDepartmentId;
    @Column(name = "is_active")
    private Boolean isActive = true;
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;
    @Column(name = "multiple_choice")
    private Boolean multipleChoice = false;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TargetAudience { ALL, HR, MANAGER, EMPLOYEE, DEPARTMENT }
    
    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public void setTargetAudience(TargetAudience targetAudience) { this.targetAudience = targetAudience; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Long getTargetDepartmentId() { return targetDepartmentId; }
    public void setTargetDepartmentId(Long targetDepartmentId) { this.targetDepartmentId = targetDepartmentId; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public Boolean getMultipleChoice() { return multipleChoice; }
    public void setMultipleChoice(Boolean multipleChoice) { this.multipleChoice = multipleChoice; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}