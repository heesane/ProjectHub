package com.project.hub.service.impl;

import static com.project.hub.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.auth.service.TokenComponent;
import com.project.hub.entity.User;
import com.project.hub.exceptions.exception.DuplicatedEmailException;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.exceptions.exception.UnmatchedPasswordException;
import com.project.hub.model.dto.request.auth.UserLoginRequest;
import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;
import com.project.hub.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class JwtAuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder encoder;

  @Mock
  private TokenComponent tokenComponent;

  @InjectMocks
  private JwtAuthService jwtAuthService;

  private UserRegisterRequest userRegisterRequest;

  private User registerUser;

  private User loginUser;

  private UserLoginRequest userLoginRequest;

  private UserLoginRequest invalidPasswordUserLoginRequest;

  private UserLoginRequest invalidEmailUserLoginRequest;

  private UserRegisterRequest duplicateEmailUserRegisterRequest;

  @BeforeEach
  void setUp() {

    // 회원가입을 위한 유저 정보
    registerUser = User.builder()
        .id(1L)
        .email("registerTest@gmail.com")
        .nickname("registerTest")
        .password("register")
        .role(USER)
        .build();

    // 로그인을 위한 유저 정보
    loginUser = User.builder()
        .id(2L)
        .email("loginTest@gmail.com")
        .nickname("loginTest")
        .password("login")
        .role(USER)
        .build();

    // 정상 회원가입
    userRegisterRequest = UserRegisterRequest.builder()
        .email("registerTest@gmail.com")
        .nickname("registerTest")
        .password("register")
        .build();

    // 정상 로그인
    userLoginRequest = UserLoginRequest.builder()
        .email("loginTest@gmail.com")
        .password("login")
        .build();

    // 중복 이메일 회원가입
    duplicateEmailUserRegisterRequest = UserRegisterRequest.builder()
        .email("registerTest@gmail.com")
        .nickname("test2")
        .password("test2")
        .build();

    // 비밀번호 불일치 로그인
    invalidPasswordUserLoginRequest = UserLoginRequest.builder()
        .email("loginTest@gmail.com")
        .password("test3")
        .build();

    // 이메일 불일치 로그인
    invalidEmailUserLoginRequest = UserLoginRequest.builder()
        .email("test3@gmail.com")
        .password("test3")
        .build();

  }

  @Test
  @DisplayName("정상적인 회원가입")
  void register() {

    //given
    given(userRepository.existsByEmail(userRegisterRequest.getEmail())).willReturn(false);

    //when
    UserRegisterResponse register = jwtAuthService.register(userRegisterRequest);

    //then
    assertEquals(register.getEmail(), registerUser.getEmail());
  }

  @Test
  @DisplayName("이메일 중복으로 인한 회원가입 실패")
  void invalidEmailRegister() {
    //given
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    //when&then
    assertThrows(DuplicatedEmailException.class, () -> {
      jwtAuthService.register(duplicateEmailUserRegisterRequest);
    });
  }

  @Test
  @DisplayName("정상적인 로그인")
  void login() {

    //given
    given(userRepository.findByEmail(anyString())).willReturn(Optional.ofNullable(this.loginUser));
    given(tokenComponent.generateToken(anyLong())).willReturn(
        JwtToken.builder().accessToken("accessToken").refreshToken("refreshToken").build());

    // when
    when(encoder.matches(anyString(), anyString())).thenReturn(true);
    JwtToken token = jwtAuthService.login(userLoginRequest);

    // then
    assertEquals(token.accessToken(), "accessToken");
    assertEquals(token.refreshToken(), "refreshToken");
  }

  @Test
  @DisplayName("비밀번호 불일치로 인한 로그인 실패")
  void unmatchedPasswordLogin() {
    //given
    given(userRepository.findByEmail(anyString())).willReturn(Optional.ofNullable(loginUser));

    //when&then
    assertThrows(UnmatchedPasswordException.class, () -> {
      jwtAuthService.login(invalidPasswordUserLoginRequest);
    });
  }

  @Test
  @DisplayName("로그인 실패")
  void failureLogin() {
    //given
    given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

    //when&then
    assertThrows(NotFoundException.class, () -> {
      jwtAuthService.login(invalidEmailUserLoginRequest);
    });
  }
}