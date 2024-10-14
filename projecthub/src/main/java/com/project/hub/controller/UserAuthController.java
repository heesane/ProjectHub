package com.project.hub.controller;

import static com.project.hub.model.type.ResultCode.USER_CREATED;
import static com.project.hub.model.type.ResultCode.USER_LOGIN_FAIL;

import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;
import com.project.hub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "UserAuthController", description = "유저 인증 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class UserAuthController {

  private final AuthService jwtAuthService;

  @GetMapping(value = "")
  @Operation(
      summary = "OAuth Login Redirect",
      description = "OAuth 로그인 리다이렉트"
  )
//  // Header에 토큰 넣기
//  public ResponseEntity<?> oauthLogin(HttpServletRequest request) {
//    log.info("accessToken : {}", request.getHeader("AccessToken"));
//    log.info("refreshToken : {}", request.getHeader("RefreshToken"));
//    return ResponseEntity.ok().body("OAuth Login Redirect");
//  }
  // Default EndPoint 사용 시 사용
//  public ResponseEntity<?> oauthLogin(
//      @RequestParam(value="a")String accessToken,
//      @RequestParam(value = "r") String refreshToken) {
//    log.info("accessToken : {}", accessToken);
//    log.info("refreshToken : {}", refreshToken);
//    return ResponseEntity.ok().body("OAuth Login Redirect");
//  }
// Custom EndPoint 사용 시 사용
  public ResponseEntity<ResultResponse> oauthLogin(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "state", required = false) String state) {
    return jwtAuthService.oauth2Login(code, state);
  }

  @PostMapping(value = "/register")
  @Operation(
      summary = "Register",
      description = "회원가입"
  )
  public ResponseEntity<ResultResponse> register(
      @Parameter(description = "Email, Nickname, Password") @RequestBody @Valid UserRegisterRequest userRegisterRequest) {
    UserRegisterResponse register = jwtAuthService.register(userRegisterRequest);
    return ResponseEntity.ok(ResultResponse.of(USER_CREATED, register));
  }

  @GetMapping(value = "/login/error")
  @Operation(
      summary = "Login Error",
      description = "로그인 실패"
  )
  public ResponseEntity<ResultResponse> loginError(
      @RequestParam(value = "message", required = false) String message) {
    log.error("로그인 오류 발생 : {}", message);
    return ResponseEntity.badRequest()
        .body(ResultResponse.of(USER_LOGIN_FAIL, "로그인 오류 발생 : " + message));

  }
}
