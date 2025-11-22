package com.interacthub.admin_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interacthub.admin_service.model.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetAudienceOrderByCreatedAtDesc(Announcement.TargetAudience targetAudience);
    List<Announcement> findByTargetAudienceInOrderByCreatedAtDesc(List<Announcement.TargetAudience> audiences);
}
