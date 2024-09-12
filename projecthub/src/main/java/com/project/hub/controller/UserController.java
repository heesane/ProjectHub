package com.project.hub.controller;

import com.project.hub.model.dto.request.user.UpdateUserProfileRequest;
import com.project.hub.model.dto.request.user.UpdateUserProjectVisibleRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/profile/{userId}")
  public ResponseEntity<ResultResponse> getProfile(@PathVariable Long userId) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_PROFILE_SUCCESS,userService.myProfile(userId)));
  }

  @PatchMapping("/update")
  public ResponseEntity<ResultResponse> updateProfile(@RequestBody UpdateUserProfileRequest request) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_UPDATE_SUCCESS,userService.changeNickname(request)));
  }

  @PostMapping("/visible")
  public ResponseEntity<ResultResponse> changeVisible(@RequestBody UpdateUserProjectVisibleRequest request) {
    return ResponseEntity.ok(ResultResponse.of(ResultCode.USER_VISIBLE_SUCCESS,userService.changeVisible(request)));
  }
}
