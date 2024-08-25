package com.project.hub.controller;

import static com.project.hub.model.type.ResultCode.USER_CREATED;
import static com.project.hub.model.type.ResultCode.USER_LOGIN_SUCCESS;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.model.dto.request.UserLoginRequest;
import com.project.hub.model.dto.request.UserRegisterRequest;
import com.project.hub.model.dto.response.UserRegisterResponse;
import com.project.hub.model.result.ResultResponse;
import com.project.hub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthController {

  private final AuthService JwtAuthService;

  @GetMapping("")
  public ResponseEntity<JwtToken> oauthLogin(
      @RequestParam("a") String accessToken,
      @RequestParam("r") String refreshToken) {
    return ResponseEntity.ok(
        JwtToken.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build());
  }

  @PostMapping("/login")
  public ResponseEntity<ResultResponse> login(
      @RequestBody @Valid UserLoginRequest jwtLoginRequest) {
    JwtToken login = JwtAuthService.login(jwtLoginRequest);
    return ResponseEntity.ok(ResultResponse.of(USER_LOGIN_SUCCESS, login));
  }

  @PostMapping("/register")
  public ResponseEntity<ResultResponse> register(
      @RequestBody @Valid UserRegisterRequest userRegisterRequest) {
    UserRegisterResponse register = JwtAuthService.register(userRegisterRequest);
    return ResponseEntity.ok(ResultResponse.of(USER_CREATED, register));
  }
}
