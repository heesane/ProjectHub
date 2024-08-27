package com.project.hub.model.dto.request.comments;

import lombok.Getter;

@Getter
public class UpdateCommentRequest extends BaseCommentsRequest {

  private final Long commentId;

  public UpdateCommentRequest(Long userId, Long projectId, String contents, Long commentId) {
    super(userId, projectId, contents);
    this.commentId = commentId;
  }
}
