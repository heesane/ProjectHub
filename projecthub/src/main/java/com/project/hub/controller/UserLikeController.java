package com.project.hub.controller;

import com.project.hub.model.dto.request.likes.BaseLikeRequest;
import com.project.hub.model.dto.request.likes.CommentLikeRequest;
import com.project.hub.model.dto.request.likes.ProjectLikeRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.comments.Comment;
import com.project.hub.service.impl.UserLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserLikeController", description = "좋아요 API")
@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class UserLikeController {

  private final UserLikeService userLikeService;

  @Operation(
      summary = "좋아요",
      description = "프로젝트, 댓글 좋아요"
  )
  @PostMapping("/project")
  public ResponseEntity<ResultResponse> projectLike(@RequestBody @Valid ProjectLikeRequest request){
    return ResponseEntity.ok(userLikeService.like(request));
  }

  @Operation(
      summary = "댓글 좋아요",
      description = "댓글 좋아요"
  )
  @PostMapping("/comment")
  public ResponseEntity<ResultResponse> commentLike(@RequestBody @Valid CommentLikeRequest request){
    return ResponseEntity.ok(userLikeService.like(request));
  }
}
