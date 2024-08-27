package com.project.hub.model.dto.request.comments;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteCommentRequest {

  private final Long commentId;
  private final Long userId;
}
