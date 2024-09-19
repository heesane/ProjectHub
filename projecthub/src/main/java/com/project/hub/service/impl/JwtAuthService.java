package com.project.hub.service.impl;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.auth.service.TokenService;
import com.project.hub.entity.User;
import com.project.hub.exceptions.ExceptionCode;
import com.project.hub.exceptions.exception.BusinessException;
import com.project.hub.exceptions.exception.DuplicatedEmailException;
import com.project.hub.exceptions.exception.DuplicatedNicknameException;
import com.project.hub.exceptions.exception.NotFoundException;
import com.project.hub.exceptions.exception.UnmatchedPasswordException;
import com.project.hub.model.dto.request.auth.UserLoginRequest;
import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;
import com.project.hub.model.type.ResultCode;
import com.project.hub.model.type.UserRole;
import com.project.hub.repository.jpa.UserRepository;
import com.project.hub.service.AuthService;
import jakarta.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthService implements AuthService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder;
  private final TokenService tokenService;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleClientId;

  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String googleClientSecret;

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String googleRedirectUri;

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
        () -> new NotFoundException(ExceptionCode.USER_NOT_FOUND)
    );

    if (!encoder.matches(password, user.getPassword())) {
      throw new UnmatchedPasswordException();
    }

    return tokenService.generateToken(user.getEmail());
  }

  @Transactional
  @Override
  public ResponseEntity<ResultResponse> oauth2Login(String code, String state){

    if(code!=null){
      try{

        String accessToken = exchangeCodeForToken(code);
        Map userInfo = getUserInfo(accessToken);

        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("name");

        if (userRepository.existsByEmail(email)) {
          JwtToken token = tokenService.generateToken(email);
          HttpHeaders httpHeaders = new HttpHeaders();
          httpHeaders.add("Access-Token", token.accessToken());
          httpHeaders.add("Refresh-Token", token.refreshToken());
          return ResponseEntity.ok().headers(httpHeaders).body(ResultResponse.of(ResultCode.USER_OAUTH_LOGIN_SUCCESS));
        }
        else {

          userRepository.save(
              User.builder()
                  .nickname(nickname)
                  .email(email)
                  .provider("google")
                  .providerId((String) userInfo.get("id"))
                  .role(UserRole.USER)
                  .build()
          );

          JwtToken token = tokenService.generateToken(email);
          HttpHeaders httpHeaders = new HttpHeaders();
          httpHeaders.add("Access-Token", token.accessToken());
          httpHeaders.add("Refresh-Token", token.refreshToken());

          return ResponseEntity.ok().headers(httpHeaders).body(ResultResponse.of(ResultCode.USER_OAUTH_REGISTER_SUCCESS));
        }
      } catch (Exception e) {
        log.error("OAuth2 Login Error: {}", e.getMessage());
        throw new BusinessException(ExceptionCode.ERROR_OAUTH);
      }
    }else {
      throw new BusinessException(ExceptionCode.INVALID_OAUTH_CODE);
    }
  }

  private String exchangeCodeForToken(String code) throws Exception {
    RestTemplate restTemplate = new RestTemplate();

    // 인증 코드 디코딩
    String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    // 요청 파라미터 설정
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("client_id", googleClientId);
    params.add("client_secret", googleClientSecret);
    params.add("code", decodedCode);
    params.add("grant_type", "authorization_code");
    params.add("redirect_uri", googleRedirectUri);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    // 토큰 엔드포인트로 POST 요청
    ResponseEntity<Map> response = restTemplate.postForEntity(
        "https://oauth2.googleapis.com/token", request, Map.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      Map responseBody = response.getBody();

      return (String) responseBody.get("access_token");
    } else {
      throw new Exception("액세스 토큰 요청 실패: " + response.getBody());
    }
  }

  private Map getUserInfo(String accessToken) throws Exception {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    // 사용자 정보 엔드포인트로 GET 요청
    ResponseEntity<Map> response = restTemplate.exchange(
        "https://www.googleapis.com/oauth2/v1/userinfo?alt=json",
        HttpMethod.GET,
        entity,
        Map.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    } else {
      throw new Exception("사용자 정보 요청 실패: " + response.getBody());
    }
  }
}
