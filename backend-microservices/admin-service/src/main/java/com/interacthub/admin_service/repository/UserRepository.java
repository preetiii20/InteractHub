package com.interacthub.admin_service.repository;

import com.interacthub.admin_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
    List<User> findByIsActiveTrue();
    long countByRole(User.Role role);
}