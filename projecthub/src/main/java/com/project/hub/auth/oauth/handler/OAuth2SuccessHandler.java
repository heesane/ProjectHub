package com.project.hub.auth.oauth.handler;

import com.project.hub.auth.service.TokenComponent;
import com.project.hub.repository.UserRepository;
import com.project.hub.model.type.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import com.project.hub.entity.User;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenComponent tokenComponent;
  private final UserRepository userRepository;

  public OAuth2SuccessHandler(
      TokenComponent tokenComponent,
      UserRepository userRepository) {
    this.tokenComponent = tokenComponent;
    this.userRepository = userRepository;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
      , Authentication authentication) throws IOException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    Optional<User> user = userRepository.findByEmail(email);
    Long userId;
    String targetUrl;
    log.info("OAuth2User: {}", oAuth2User);
    if (user.isPresent()) { // 기존 회원인 경우 액세스, 리프레시 토큰 생성 후 전달
      userId = user.get().getId();

    } else { // 신규 회원인 경우 회원가입 페이지로 이동
      User newOAuth2User = User.builder()
          .email(email)
          .nickname(oAuth2User.getAttribute("name"))
          .role(UserRole.USER)
          .provider("google")
          .providerId(oAuth2User.getAttribute("sub"))
          .build();
      User savedOAuthUser = userRepository.save(newOAuth2User);
      userId = savedOAuthUser.getId();
    }

    String accessToken = tokenComponent.generateAccessToken(userId);
    String refreshToken = tokenComponent.generateRefreshToken();

    targetUrl = UriComponentsBuilder.fromUriString(
            "http://localhost:8080/api/v1/auth")
        .queryParam("a", accessToken)
        .queryParam("r", refreshToken)
        .build()
        .toUriString();
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}
