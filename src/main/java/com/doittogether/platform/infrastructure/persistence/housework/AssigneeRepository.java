package com.doittogether.platform.infrastructure.persistence.housework;

import com.doittogether.platform.domain.entity.Assignee;
import java.util.Optional;

import com.doittogether.platform.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
    Optional<Assignee> findByUserUserId(Long userId);
    void deleteByUser(User user);
}
