package com.project.hub.controller;

import com.project.hub.model.dto.request.likes.BaseLikeRequest;
import com.project.hub.model.dto.request.likes.CommentLikeRequest;
import com.project.hub.model.dto.request.likes.ProjectLikeRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.comments.Comment;
import com.project.hub.service.impl.UserLikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class UserLikeController {

  private final UserLikeService userLikeService;

  @PostMapping("/project")
  public ResponseEntity<ResultResponse> projectLike(@RequestBody @Valid ProjectLikeRequest request){
    return ResponseEntity.ok(userLikeService.like(request));
  }

  @PostMapping("/comment")
  public ResponseEntity<ResultResponse> commentLike(@RequestBody @Valid CommentLikeRequest request){
    return ResponseEntity.ok(userLikeService.like(request));
  }
}
