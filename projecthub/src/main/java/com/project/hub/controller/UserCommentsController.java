package com.project.hub.controller;

import com.project.hub.model.dto.request.comments.DeleteCommentRequest;
import com.project.hub.model.dto.request.comments.UpdateCommentRequest;
import com.project.hub.model.dto.request.comments.WriteCommentRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.service.impl.UserCommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class UserCommentsController {

  private final UserCommentsService userCommentsService;

  @PostMapping("/post")
  public ResponseEntity<ResultResponse> postComment(@RequestBody WriteCommentRequest request) {
    log.info("request: {}", request);
    ResultResponse response = userCommentsService.createComment(request);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/update")
  public ResponseEntity<ResultResponse> updateComment(@RequestBody UpdateCommentRequest request) {
    ResultResponse response = userCommentsService.updateComment(request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<ResultResponse> deleteComment(@RequestBody DeleteCommentRequest commentId) {
    ResultResponse response = userCommentsService.deleteComment(commentId);
    return ResponseEntity.ok(response);
  }
}
