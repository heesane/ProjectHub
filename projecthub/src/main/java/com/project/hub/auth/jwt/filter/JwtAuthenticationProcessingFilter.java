package com.project.hub.auth.jwt.filter;

import com.project.hub.auth.jwt.util.PasswordUtil;
import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.User;
import com.project.hub.repository.jpa.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

/***
 * /api/v1/auth/login 제외하고 모든 요청에 대해 JWT 토큰을 검증하고 인증 처리를 하는 필터
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

  private static final String NO_CHECK_URL = "/api/v1/auth/login";

  private final TokenService tokenService;
  private final UserRepository userRepository;

  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (request.getRequestURI().equals(NO_CHECK_URL)) {
      filterChain.doFilter(request, response);
      return;
    }

    String refreshToken = tokenService.extractRefreshToken(request)
        .filter(tokenService::isTokenValid)
        .orElse(null);

    if (refreshToken != null) {
      checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
      return;
    }

    checkAccessTokenAndAuthentication(request, response, filterChain);
  }

  public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
    userRepository.findByRefreshToken(refreshToken)
        .ifPresent(user -> {
          String reIssuedRefreshToken = reIssueRefreshToken(user);
          tokenService.sendAccessAndRefreshToken(response, tokenService.generateAccessToken(user.getEmail()),
              reIssuedRefreshToken);
        });
  }

  private String reIssueRefreshToken(User user) {
    String reIssuedRefreshToken = tokenService.generateRefreshToken();
    user.updateRefreshToken(reIssuedRefreshToken);
    userRepository.saveAndFlush(user);
    return reIssuedRefreshToken;
  }

  public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    log.info("checkAccessTokenAndAuthentication() 호출");
    tokenService.extractAccessToken(request)
        .filter(tokenService::isTokenValid)
        .ifPresent(accessToken -> tokenService.extractEmail(accessToken)
            .ifPresent(email -> userRepository.findByEmail(email)
                .ifPresent(this::saveAuthentication)));

    filterChain.doFilter(request, response);
  }

  public void saveAuthentication(User myUser) {
    String password = myUser.getPassword();
    if (password == null) {
      password = PasswordUtil.generateRandomPassword();
    }

    UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
        .username(myUser.getEmail())
        .password(password)
        .roles(myUser.getRole().name())
        .build();

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(userDetailsUser, null,
            authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
