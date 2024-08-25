package com.project.hub.auth.service;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.exception.exception.TokenExpiredException;
import com.project.hub.exception.exception.TokenNotExistsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenComponent {

  private final Key hashedSecretKey;

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.token.access-expire-length}")
  private Long accessExpireLength; // 액세스 토큰의 만료 시간

  @Value("${jwt.token.refresh-expire-length}")
  private Long refreshExpireLength; // 리프레시 토큰의 만료 시간

  public TokenComponent(@Value("${jwt.secret-key}") String secretKey) {
    this.hashedSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public JwtToken generateToken(Long userId) {
    log.info("generateToken");
    log.info("userId : {}", userId);
    return JwtToken.builder()
        .accessToken(generateAccessToken(userId))
        .refreshToken(generateRefreshToken())
        .build();
  }

  public String generateAccessToken(Long userId) { // 액세스, 리프레시 토큰 생성 로직 구현
    log.info("generateAccessToken");
    log.info("userId : {}", userId);
    Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
    return Jwts.builder().setClaims(claims)
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

  public String reGenerateAccessToken(HttpServletRequest request) { // 액세스 토큰 재발급
    validateRefreshToken(request); // 만료 검사
    Long id = getUserIdFromToken(request);
    return generateAccessToken(id);
  }

  public String resolveAccessToken(HttpServletRequest request) {
    try {
      String header = request.getHeader("AUTHORIZATION");
      return header.substring("Bearer ".length()); // Bearer 을 제외한 문자열 반환
    } catch (Exception e) {
      throw new TokenNotExistsException();
    }
  }

  public String resolveRefreshToken(HttpServletRequest request) {
    try {
      return request.getHeader("REFRESH-TOKEN");
    } catch (Exception e) {
      throw new TokenNotExistsException();
    }
  }

  public void validateAccessToken(HttpServletRequest request) { // 만료 여부 검사
    try {
      String token = resolveAccessToken(request);
      Jwts.parserBuilder().setSigningKey(hashedSecretKey).build().parseClaimsJws(token);
    } catch (ExpiredJwtException e) {
      throw new TokenExpiredException();
    }
  }

  public void validateRefreshToken(HttpServletRequest request) {
    try {
      String token = resolveRefreshToken(request);
      Jwts.parserBuilder().setSigningKey(hashedSecretKey).build().parseClaimsJws(token);
    } catch (ExpiredJwtException e) { // 토큰이 만료된 경우
      throw new TokenExpiredException();
    } catch (IllegalArgumentException e) { // 토큰이 비어있거나 형식이 잘못된 경우
      throw new TokenNotExistsException();
    }
  }

  public Long getUserIdFromToken(HttpServletRequest request) { // 토큰에서 userId 정보 꺼내기
    String token = resolveAccessToken(request);
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(hashedSecretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();
      return Long.parseLong(claims.getSubject());
    } catch (ExpiredJwtException e) {
      return Long.parseLong(e.getClaims().getSubject());
    }
  }

  // JWT 토큰에서 클레임 추출
  public Claims extractClaims(HttpServletRequest request) {
    String accessToken = resolveAccessToken(request);
    return Jwts.parserBuilder().setSigningKey(this.hashedSecretKey).build().parseClaimsJws(accessToken)
        .getBody();
  }

  public boolean isExpired(HttpServletRequest request) {
    return extractClaims(request).getExpiration().before(new Date());
  }

  // JWT 토큰에서 사용자 권한 추출
  public Authentication getAuthentication(HttpServletRequest request) {

    Claims claims = extractClaims(request);
    if (claims.get("auth") == null) {
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }
    Collection<? extends GrantedAuthority> authorities = Arrays
        .stream(claims.get("auth").toString().split(","))
        .map(SimpleGrantedAuthority::new)
        .toList();

    return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
  }
}