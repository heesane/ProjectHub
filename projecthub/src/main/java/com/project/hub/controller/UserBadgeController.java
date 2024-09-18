package com.project.hub.controller;

import com.project.hub.model.dto.request.badge.CreateBadgeRequest;
import com.project.hub.model.dto.request.badge.DeleteBadgeRequest;
import com.project.hub.model.dto.request.badge.UpdateBadgeRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.service.impl.UserBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserBadgeController", description = "뱃지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/badge")
public class UserBadgeController {

  private final UserBadgeService userBadgeService;

  @Operation(
      summary = "모든 뱃지 조회",
      description = "모든 뱃지를 조회합니다."
  )
  @GetMapping("/all")
  public ResponseEntity<ResultResponse> getAllBadges() {
    return ResponseEntity.ok(userBadgeService.getAllBadges());
  }

  @Operation(
      summary = "뱃지 생성",
      description = "새로운 뱃지를 생성합니다."
  )
  @PostMapping("/create")
  public ResponseEntity<ResultResponse> createNewBadge(@RequestBody CreateBadgeRequest request) {
    return ResponseEntity.ok(userBadgeService.createNewBadge(request));
  }

  @Operation(
      summary = "뱃지 수정",
      description = "기존 뱃지를 수정합니다."
  )
  @PatchMapping("/update")
  public ResponseEntity<ResultResponse> updateBadge(@RequestBody UpdateBadgeRequest request) {
    return ResponseEntity.ok(userBadgeService.updateBadge(request));
  }

  @Operation(
      summary = "뱃지 삭제",
      description = "기존 뱃지를 삭제합니다."
  )
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ResultResponse> deleteBadge(@PathVariable("id") DeleteBadgeRequest request) {
    return ResponseEntity.ok(userBadgeService.deleteBadge(request));
  }

}
