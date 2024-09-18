package com.project.hub.auth.oauth.handler;

import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.User;
import com.project.hub.model.type.UserRole;
import com.project.hub.repository.jpa.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenService tokenService;
  private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
      , Authentication authentication) throws IOException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    Optional<User> user = userRepository.findByEmail(email);

    log.info("OAuth2 User email: {}", email);

    if (user.isEmpty()) { // 기존 회원인 경우 액세스, 리프레시 토큰 생성 후 전달
      User newOAuth2User = User.builder()
          .email(email)
          .nickname(oAuth2User.getAttribute("name"))
          .role(UserRole.USER)
          .provider("google")
          .providerId(oAuth2User.getAttribute("sub"))
          .build();
      User save = userRepository.save(newOAuth2User);
      log.info("OAuth2 User is Null? : {}", save == null);
    }

    String access = tokenService.generateAccessToken(email);
    String refresh = tokenService.generateRefreshToken();

//    String targetUrl = UriComponentsBuilder.fromUriString(
//            "http://localhost:8080/api/v1/auth")
//        .queryParam("a",access)
//        .queryParam("r", refresh)
//        .build()
//        .toUriString();
    String targetUrl = UriComponentsBuilder.fromUriString(
            "http://localhost:8080/api/v1/auth/oauth/login")
        .build()
        .toUriString();

    log.info("OAuth2 Success Handler targetUrl: {}", targetUrl);
    response.setHeader("AccessToken", access);
    response.setHeader("RefreshToken", refresh);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}
