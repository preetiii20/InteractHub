package com.interacthub.manager.repository;

import com.interacthub.manager.model.ManagerPoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerPollRepository extends JpaRepository<ManagerPoll, Long> {
    // Spring Data JPA automatically provides CRUD methods for ManagerPoll entity.
}