package com.project.hub.repository.jpa;

import com.project.hub.entity.ProjectLikes;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectsLikeRepository extends JpaRepository<ProjectLikes, Long> {

  boolean existsByProjectIdAndUserId(Long projectId, Long userId);

  List<ProjectLikes> findAllByUserId(Long userId);

  void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
