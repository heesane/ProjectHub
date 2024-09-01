package com.project.hub.repository.jpa;

import com.project.hub.entity.Projects;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, Long> {

  // 최신순으로 기본 5개의 프로젝트를 조회
  Page<Projects> findAll(Pageable pageable);

  // 해당 유저가 등록한 프로젝트를 최신순으로 조회
  Page<Projects> findAllByUserId(Long userId, Pageable pageable);

  // 댓글 순(추후 댓글 구현시)

  // 좋아요 순(추후 좋아요 구현시)
  @Query("select p from Projects p JOIN fetch p.user where p.id = :projectId")
  Optional<Projects> findByIdWithDetail(@Param("projectId") Long projectId);

  Optional<Projects> findByTitle(String title);

  List<Projects> findAllByTitleLike(String title);
}
