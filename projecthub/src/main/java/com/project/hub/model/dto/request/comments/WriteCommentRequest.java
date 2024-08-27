package com.project.hub.model.dto.request.comments;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class WriteCommentRequest extends BaseCommentsRequest {

  private Long parentCommentId = null;

  public WriteCommentRequest(Long userId, Long projectId, String contents, Long parentCommentId) {
    super(userId, projectId, contents);
    // 부모 댓글이 없는 경우 null로 설정
    this.parentCommentId = parentCommentId;
  }
}