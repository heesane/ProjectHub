package com.project.hub.repository.jpa;

import com.project.hub.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

  boolean existsByName(String name);
}
