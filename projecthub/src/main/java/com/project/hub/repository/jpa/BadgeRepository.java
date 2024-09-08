package com.project.hub.repository.jpa;

import com.project.hub.entity.Badge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge,Long> {
  List<Badge> findAll();

  boolean existsByName(String name);
}
