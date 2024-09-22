package com.project.hub.service.impl;

import static com.project.hub.model.type.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.User;
import com.project.hub.exceptions.exception.DuplicatedEmailException;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.exceptions.exception.UnmatchedPasswordException;
import com.project.hub.model.dto.request.auth.UserLoginRequest;
import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;
import com.project.hub.repository.jpa.UserRepository;
import java.util.Optional;
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
  private TokenService tokenService;

  @InjectMocks
  private JwtAuthService jwtAuthService;

  @Test
  @DisplayName("정상적인 회원가입")
  void register() {

    //given
    UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
        .email("register@gmail.com")
        .nickname("register")
        .password("register")
        .build();

    given(userRepository.existsByEmail(userRegisterRequest.getEmail())).willReturn(false);
    given(userRepository.existsByNickname(userRegisterRequest.getNickname())).willReturn(false);

    //when
    UserRegisterResponse register = jwtAuthService.register(userRegisterRequest);

    //then
    assertEquals(register.getEmail(), userRegisterRequest.getEmail());
  }

  @Test
  @DisplayName("이메일 중복으로 인한 회원가입 실패")
  void invalidEmailRegister() {
    //given
    UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
        .email("register@gmail.com")
        .nickname("register")
        .password("register")
        .build();
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    //when&then
    assertThrows(DuplicatedEmailException.class, () -> {
      jwtAuthService.register(userRegisterRequest);
    });
  }

  @Test
  @DisplayName("정상적인 로그인")
  void login() {

    //given
    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .role(USER)
        .build();

    UserLoginRequest userLoginRequest = UserLoginRequest.builder()
        .email("login@gmail.com")
        .password("login")
        .build();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.ofNullable(user));
    given(tokenService.generateToken(anyString())).willReturn(
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
    User user = User.builder()
        .email("login@gmail.com")
        .nickname("login")
        .password("login")
        .role(USER)
        .build();

    UserLoginRequest invalidPasswordUserLoginRequest = UserLoginRequest.builder()
        .email("login@gmail.com")
        .password("invalidPassword")
        .build();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.ofNullable(user));
    //when&then
    assertThrows(UnmatchedPasswordException.class, () -> {
      jwtAuthService.login(invalidPasswordUserLoginRequest);
    });
  }

  @Test
  @DisplayName("로그인 실패")
  void failureLogin() {
    //given
    UserLoginRequest userLoginRequest = UserLoginRequest.builder()
        .email("failLogin@gmail.com")
        .password("test")
        .build();

    given(userRepository.findByEmail(userLoginRequest.getEmail())).willReturn(Optional.empty());

    //when&then
    assertThrows(NotFoundException.class, () -> {
      jwtAuthService.login(userLoginRequest);
    });
  }
}