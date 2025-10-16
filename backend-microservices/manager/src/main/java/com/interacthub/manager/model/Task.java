package com.interacthub.manager.model;

import java.time.LocalDate;
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
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_id", nullable = false)
    private Long projectId;
    
    private String title;
    private String description;
    
    @Column(name = "assigned_to")
    private Long assignedTo; // Employee ID
    
    @Column(name = "assigned_by")
    private Long assignedBy; // Manager ID
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TO_DO;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TaskStatus { TO_DO, IN_PROGRESS, IN_REVIEW, COMPLETED }

    // Manual Getters/Setters (Required)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}