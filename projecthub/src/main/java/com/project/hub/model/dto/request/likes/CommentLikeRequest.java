package com.project.hub.model.dto.request.likes;

import com.project.hub.model.type.LikeType;
import lombok.Getter;

@Getter
public class CommentLikeRequest extends BaseLikeRequest {

  private final Long commentId;

  public CommentLikeRequest(Long userId, LikeType likeType, Long commentId) {
    super(userId, likeType);
    this.commentId = commentId;
  }
}
