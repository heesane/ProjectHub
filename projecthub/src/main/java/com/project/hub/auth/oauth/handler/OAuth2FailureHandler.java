package com.project.hub.auth.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    log.error("OAuth2 인증 실패!");
    // 인증 실패 시 아래 주소로 리다이렉트
    // 실패 이유를 URL 파라미터로 전달할 수 있음
    String targetUrl = UriComponentsBuilder.fromUriString(
            "http://localhost:8080/api/v1/auth/login/error")
        .queryParam("message", exception.getMessage().replace("[", "").replace("]", ""))
        .build()
        .toUriString();
    response.sendRedirect(targetUrl);
  }
}
