package com.interacthub.manager.repository;

import com.interacthub.manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Used for Admin Visibility Report
    long countByStatus(Task.TaskStatus status);
}