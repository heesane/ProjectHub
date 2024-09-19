package com.project.hub.auth.jwt.handler;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.auth.service.TokenService;
import com.project.hub.repository.jpa.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenService tokenService;
  private final UserRepository userRepository;

  @Value("${jwt.token.access-expire-length}")
  private String accessTokenExpiration;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    String email = extractUsername(authentication);
    JwtToken jwtToken = tokenService.generateToken(email);
    String accessToken = jwtToken.accessToken();
    String refreshToken = jwtToken.refreshToken();

    tokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

    userRepository.findByEmail(email)
        .ifPresent(user -> {
          user.updateRefreshToken(refreshToken);
          userRepository.saveAndFlush(user);
        });
  }

  private String extractUsername(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userDetails.getUsername();
  }
}
