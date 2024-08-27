package com.project.hub.repository;

import com.project.hub.entity.Projects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, Long> {

  // 최신순으로 기본 5개의 프로젝트를 조회
  Page<Projects> findAllByDeletedAtIsNullOrderByRegisteredAtDesc(Pageable pageable);

  // 해당 유저가 등록한 프로젝트를 최신순으로 조회
  Page<Projects> findAllByUserIdAndDeletedAtIsNullOrderByRegisteredAtDesc(Long userId,
      Pageable pageable);

  // 댓글 순(추후 댓글 구현시)

  // 좋아요 순(추후 좋아요 구현시)
}
