package com.project.hub.config;

import com.project.hub.auth.jwt.filter.JwtTokenFilter;
import com.project.hub.auth.oauth.CustomOAuth2UserService;
import com.project.hub.auth.oauth.handler.OAuth2FailureHandler;
import com.project.hub.auth.oauth.handler.OAuth2SuccessHandler;
import com.project.hub.auth.service.TokenComponent;
import com.project.hub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final OAuth2FailureHandler oAuth2FailureHandler;
  private final TokenComponent tokenComponent;
  private final AuthService authService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(
            HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .authorizeHttpRequests(requests ->
            requests
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/**").permitAll()
        )
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )  // 세션을 사용하지 않으므로 STATELESS 설정
        .oauth2Login(oauth2Login -> oauth2Login
            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                .userService(customOAuth2UserService)
            )
            .successHandler(oAuth2SuccessHandler)
            .failureHandler(oAuth2FailureHandler)
        )
        .addFilterAfter(this.jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  private JwtTokenFilter jwtTokenFilter() {
    return new JwtTokenFilter(tokenComponent, authService);
  }
}
