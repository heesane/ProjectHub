package com.project.hub.controller;

import static com.project.hub.model.type.ResultCode.USER_CREATED;
import static com.project.hub.model.type.ResultCode.USER_LOGIN_SUCCESS;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.model.dto.request.auth.UserLoginRequest;
import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;
import com.project.hub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="UserAuthController", description="유저 인증 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class UserAuthController {

  private final AuthService JwtAuthService;

  @GetMapping(value = "")
  @Operation(
      summary = "OAuth Login Redirect",
      description = "OAuth 로그인 리다이렉트"
  )
  public ResponseEntity<ResultResponse> oauthLogin(
      @RequestParam("a") String accessToken,
      @RequestParam("r") String refreshToken) {
    JwtToken jwtToken = JwtToken.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
    return ResponseEntity.ok(ResultResponse.of(USER_LOGIN_SUCCESS, jwtToken));
  }

  @PostMapping(value = "/login")
  @Operation(
      summary = "Login",
      description = "로그인"
  )
  public ResponseEntity<ResultResponse> login(
      @Parameter(description = "Email, Password") @RequestBody @Valid UserLoginRequest jwtLoginRequest) {
    JwtToken login = JwtAuthService.login(jwtLoginRequest);
    return ResponseEntity.ok(ResultResponse.of(USER_LOGIN_SUCCESS, login));
  }

  @PostMapping(value = "/register")
  @Operation(
      summary = "Register",
      description = "회원가입"
  )
  public ResponseEntity<ResultResponse> register(
      @Parameter(description = "Email, Nickname, Password") @RequestBody @Valid UserRegisterRequest userRegisterRequest) {
    UserRegisterResponse register = JwtAuthService.register(userRegisterRequest);
    return ResponseEntity.ok(ResultResponse.of(USER_CREATED, register));
  }
}
