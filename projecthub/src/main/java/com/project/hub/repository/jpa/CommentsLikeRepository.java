package com.project.hub.repository.jpa;

import com.project.hub.entity.CommentLikes;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsLikeRepository extends JpaRepository<CommentLikes, Long> {

  boolean existsByCommentIdAndUserId(Long commentId, Long userId);

  List<CommentLikes> findAllByUserId(Long userId);

  void deleteByCommentIdAndUserId(Long commentId, Long userId);

  Long countByUserId(Long userId);
}
