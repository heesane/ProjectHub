package com.project.hub.service;

import com.project.hub.model.dto.request.comments.DeleteCommentRequest;
import com.project.hub.model.dto.request.comments.UpdateCommentRequest;
import com.project.hub.model.dto.request.comments.WriteCommentRequest;
import com.project.hub.model.dto.response.ResultResponse;

public interface CommentsService {

  // 댓글 작성
  ResultResponse createComment(WriteCommentRequest request);

  // 댓글 수정
  ResultResponse updateComment(UpdateCommentRequest request);

  // 댓글 삭제
  ResultResponse deleteComment(DeleteCommentRequest request);
}
