package com.project.hub.controller;

import com.project.hub.model.dto.request.user.UpdateUserProfileRequest;
import com.project.hub.model.dto.request.user.UpdateUserProjectVisibleRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserController", description = "유저 Profile API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "Profile 조회",
      description = "유저 프로필 조회"
  )
  @GetMapping("/profile/{userId}")
  public ResponseEntity<ResultResponse> getProfile(@PathVariable Long userId) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_PROFILE_SUCCESS,userService.myProfile(userId)));
  }

  @Operation(
      summary = "Profile 수정",
      description = "유저 프로필 수정"
  )
  @PatchMapping("/update")
  public ResponseEntity<ResultResponse> updateProfile(@RequestBody UpdateUserProfileRequest request) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_UPDATE_SUCCESS,userService.changeNickname(request)));
  }

  @Operation(
      summary = "프로젝트 공개 여부 수정",
      description = "프로젝트 공개 여부 수정"
  )
  @PostMapping("/visible")
  public ResponseEntity<ResultResponse> changeVisible(@RequestBody UpdateUserProjectVisibleRequest request) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_VISIBLE_SUCCESS,userService.changeVisible(request)));
  }
}
