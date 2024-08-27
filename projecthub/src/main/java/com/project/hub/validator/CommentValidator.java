package com.project.hub.validator;

import com.project.hub.entity.Comments;
import com.project.hub.repository.CommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

  private final CommentsRepository commentsRepository;

  public Comments validateAndGetComment(Long commentId) {
    return commentsRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
  }

  public void isCommentExist(Long commentId) {
    if(!commentsRepository.existsById(commentId)) {
      throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
    }
  }
}
