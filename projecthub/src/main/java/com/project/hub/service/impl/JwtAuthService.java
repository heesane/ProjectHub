package com.project.hub.service.impl;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.auth.service.TokenComponent;
import com.project.hub.entity.User;
import com.project.hub.exception.exception.DuplicatedEmailException;
import com.project.hub.exception.exception.DuplicatedNicknameException;
import com.project.hub.exception.exception.UnmatchedPasswordException;
import com.project.hub.exception.exception.UserNotFoundException;
import com.project.hub.model.dto.request.auth.UserLoginRequest;
import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;
import com.project.hub.model.type.UserRole;
import com.project.hub.repository.UserRepository;
import com.project.hub.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthService implements AuthService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder;
  private final TokenComponent tokenComponent;

  @Transactional
  @Override
  public UserRegisterResponse register(UserRegisterRequest userRegisterDto) {

    if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
      throw new DuplicatedEmailException();
    }

    if (userRepository.existsByNickname(userRegisterDto.getNickname())) {
      throw new DuplicatedNicknameException();
    }

    String hashedPassword = encoder.encode(userRegisterDto.getPassword());
    userRepository.save(
        User.builder()
            .nickname(userRegisterDto.getNickname())
            .email(userRegisterDto.getEmail())
            .password(hashedPassword)
            .role(UserRole.USER)
            .build()
    );
    return new UserRegisterResponse(userRegisterDto.getEmail(), userRegisterDto.getNickname());
  }

  @Override
  public JwtToken login(UserLoginRequest userLoginDto) {
    String email = userLoginDto.getEmail();
    String password = userLoginDto.getPassword();

    User user = userRepository.findByEmail(email).orElseThrow(
        UserNotFoundException::new
    );

    if (!encoder.matches(password, user.getPassword())) {
      throw new UnmatchedPasswordException();
    }

    return tokenComponent.generateToken(user.getId());
  }

  @Override
  public User getUser(Long userId) {
    return userRepository.findById(userId).orElseThrow(
        UserNotFoundException::new
    );
  }
}
