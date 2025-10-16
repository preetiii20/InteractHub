package com.interacthub.manager.repository;

import com.interacthub.manager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    // Used by ManagerService to get specific projects
    Optional<List<Project>> findByManagerId(Long managerId);

    // Used for Admin Visibility Report
    long countByStatus(Project.Status status); 
}