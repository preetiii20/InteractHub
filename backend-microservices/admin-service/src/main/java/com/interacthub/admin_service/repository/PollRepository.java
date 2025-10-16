package com.interacthub.admin_service.repository;

import com.interacthub.admin_service.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByTargetAudienceAndIsActiveTrueOrderByCreatedAtDesc(Poll.TargetAudience targetAudience);
    long countByIsActiveTrue();
}