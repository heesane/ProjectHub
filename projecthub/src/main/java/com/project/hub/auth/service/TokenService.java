package com.project.hub.auth.service;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.model.type.UserRole;
import com.project.hub.repository.jpa.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenService {

  private final UserRepository userRepository;

  private final Key hashedSecretKey;

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.token.access-expire-length}")
  private Long accessExpireLength; // 액세스 토큰의 만료 시간

  @Value("${jwt.token.refresh-expire-length}")
  private Long refreshExpireLength; // 리프레시 토큰의 만료 시간

  private final static String BEARER = "Bearer ";

  private final static String ACCESS_HEADER = HttpHeaders.AUTHORIZATION;

  private final static String REFRESH_HEADER = "REFRESH_AUTHORIZATION";

  public TokenService(UserRepository userRepository, @Value("${jwt.secret-key}") String secretKey) {
    this.userRepository = userRepository;
    this.hashedSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public JwtToken generateToken(String email) {
    return JwtToken.builder()
        .accessToken(generateAccessToken(email))
        .refreshToken(generateRefreshToken())
        .build();
  }

  // 액세스, 리프레시 토큰 생성 로직 구현
  public String generateAccessToken(String email) {
    User accessUser = userRepository.findByEmail(email).orElseThrow(
        () -> new NotFoundException(ExceptionCode.USER_NOT_FOUND)
    );
    UserRole userRole = accessUser.getRole();

    Claims claims = Jwts.claims().setSubject(String.valueOf(email));
    claims.put("auth", userRole.name());
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessExpireLength))
        .signWith(hashedSecretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken() {
    // refresh에는 별다른 유저 정보가 들어가지 않는다. claims 세팅 하지 않음
    return Jwts.builder()
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshExpireLength))
        .signWith(hashedSecretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Optional<String> extractRefreshToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(REFRESH_HEADER))
        .filter(refreshToken -> refreshToken.startsWith(BEARER))
        .map(refreshToken -> refreshToken.replace(BEARER, ""));
  }

  public Optional<String> extractAccessToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(ACCESS_HEADER))
        .filter(refreshToken -> refreshToken.startsWith(BEARER))
        .map(refreshToken -> refreshToken.replace(BEARER, ""));
  }

  public Optional<String> extractEmail(HttpServletRequest request) {
    return extractAccessToken(request)
        .flatMap(this::extractEmail);
  }

  public Optional<String> extractEmail(String accessToken) {
    try {
      // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
      return Optional.ofNullable(Jwts.parserBuilder()
          .setSigningKey(hashedSecretKey)
          .build()
          .parseClaimsJws(accessToken)
          .getBody()
          .getSubject()
      );
    } catch (Exception e) {
      log.error("액세스 토큰이 유효하지 않습니다.");
      return Optional.empty();
    }
  }

  public void sendAccessToken(HttpServletResponse response, String accessToken) {
    response.setStatus(HttpServletResponse.SC_OK);

    response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
    log.info("재발급된 Access Token : {}", accessToken);
  }

  public void sendRefreshToken(HttpServletResponse response, String refreshToken) {
    response.setStatus(HttpServletResponse.SC_OK);

    response.setHeader("REFRESH_AUTHORIZATION", refreshToken);
    log.info("재발급된 Refresh Token : {}", refreshToken);
  }

  public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
  }

  public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
    response.setHeader("REFRESH_AUTHORIZATION", refreshToken);
  }

  public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken,
      String refreshToken) {
    response.setStatus(HttpServletResponse.SC_OK);

    setAccessTokenHeader(response, accessToken);
    setRefreshTokenHeader(response, refreshToken);
    log.info("Access Token, Refresh Token 헤더 설정 완료");
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(hashedSecretKey).build().parseClaimsJws(token).getBody()
          .getSubject();
      return true;
    } catch (Exception e) {
      log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
      return false;
    }
  }
}