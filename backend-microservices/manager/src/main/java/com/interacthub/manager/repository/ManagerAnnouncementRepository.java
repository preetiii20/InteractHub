package com.interacthub.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interacthub.manager.model.ManagerAnnouncement;

@Repository
public interface ManagerAnnouncementRepository extends JpaRepository<ManagerAnnouncement, Long> {
    // This file must be a clean interface.
}