package com.project.hub.controller;

import com.project.hub.model.dto.request.comments.DeleteCommentRequest;
import com.project.hub.model.dto.request.comments.UpdateCommentRequest;
import com.project.hub.model.dto.request.comments.WriteCommentRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.service.CommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "UserCommentsController",
    description = "유저 댓글 API"
)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class UserCommentsController {

  private final CommentsService commentsService;

  @PostMapping("/post")
  @Operation(
      summary = "Post Comment",
      description = "댓글 작성 및 대댓글 작성 (대댓글 작성 시 parentId 기재 필수)"
  )
  public ResponseEntity<ResultResponse> postComment(
      @RequestBody WriteCommentRequest request) {
    log.info("request: {}", request);
    ResultResponse response = commentsService.createComment(request);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/update")
  @Operation(
      summary = "Update Comment",
      description = "댓글 수정"
  )
  public ResponseEntity<ResultResponse> updateComment(
      HttpServletRequest request,
      @RequestBody UpdateCommentRequest updateCommentRequest) {
    ResultResponse response = commentsService.updateComment(request, updateCommentRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete")
  @Operation(
      summary = "Delete Comment",
      description = "댓글 삭제"
  )
  public ResponseEntity<ResultResponse> deleteComment(
      HttpServletRequest request, @RequestBody DeleteCommentRequest deleteCommentRequest) {
    ResultResponse response = commentsService.deleteComment(request, deleteCommentRequest);
    return ResponseEntity.ok(response);
  }
}
