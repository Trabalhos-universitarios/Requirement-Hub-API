package com.br.requirementhub.repository;

import com.br.requirementhub.entity.User;
import com.br.requirementhub.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(Role role);
    Optional<User> findByName(String name);

    @Modifying
    @Query(value = "INSERT INTO user_notifications (user_id, requirement_id) VALUES (:userId, :requirementId)", nativeQuery = true)
    void addNotificationToUser(Long userId, Long requirementId);
}