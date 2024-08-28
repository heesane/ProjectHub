package com.project.hub.repository;

import com.project.hub.entity.CommentLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsLikeRepository extends JpaRepository<CommentLikes, Long> {

  boolean existsByCommentIdAndUserId(Long commentId, Long userId);

  void deleteByCommentIdAndUserId(Long commentId, Long userId);
}
