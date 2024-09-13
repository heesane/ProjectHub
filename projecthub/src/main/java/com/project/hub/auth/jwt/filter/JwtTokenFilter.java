package com.project.hub.auth.jwt.filter;

import com.project.hub.auth.service.TokenComponent;
import com.project.hub.entity.User;
import com.project.hub.service.AuthService;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

  private final TokenComponent tokenComponent;
  private final AuthService userService;

  // 들어오는 모든 요청에 대한 Filter 적용 JwtFilterChain
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // Bearer ~~~
    String accessTokenString = validateToken(request);
    if (accessTokenString == null) {
      log.info("Token is null");
      filterChain.doFilter(request, response);
      return;
    }

    // 사용자의 Access Token
    Long userId = tokenComponent.getUserIdFromToken(request);
    Claims claims = tokenComponent.extractClaims(request);
    log.info("claims : {}", claims);
    User loginUser = userService.getUser(userId);

    // Expired Check ( Access Token이 만료되기 전)
    if (!tokenComponent.isExpired(request)) {
      // 바로 통과
      Authentication authentication = tokenComponent.getAuthentication(request);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.info("Token is valid");

    }
    // Expired Check ( Access Token 만료된 후, Refresh Token 통한 Token 재발급)
    else {
      // users 테이블에서 해당 유저의 refresh token 조회
      String userRefreshToken = loginUser.getRefreshToken();

      // Refresh Token 없는 경우
      if (userRefreshToken == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      // Refresh Token이 있는 경우
      Claims refreshTokenClaims = tokenComponent.extractClaims(request);

      // Refresh Token이 만료되지 않은 경우
      // 새로운 Access Token 발급
      if (!tokenComponent.isExpired(request)) {
        String newAccessToken = tokenComponent.reGenerateAccessToken(request);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

      } else {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        filterChain.doFilter(request, response);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private String validateToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
      return token.split(" ")[1];
    }
    return null;
  }
}