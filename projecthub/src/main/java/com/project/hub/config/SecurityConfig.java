package com.project.hub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hub.auth.jwt.LoginService;
import com.project.hub.auth.jwt.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.project.hub.auth.jwt.filter.JwtAuthenticationProcessingFilter;
import com.project.hub.auth.jwt.handler.LoginFailureHandler;
import com.project.hub.auth.jwt.handler.LoginSuccessHandler;
import com.project.hub.auth.oauth.CustomOAuth2UserService;
import com.project.hub.auth.oauth.handler.OAuth2FailureHandler;
import com.project.hub.auth.oauth.handler.OAuth2SuccessHandler;
import com.project.hub.auth.service.TokenService;
import com.project.hub.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;
  private final OAuth2FailureHandler oAuth2FailureHandler;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final TokenService tokenService;
  private final LoginService loginService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(
            HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .authorizeHttpRequests(requests ->
                requests.requestMatchers("/**").permitAll()
//            requests
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                .requestMatchers("/v3/**","swagger-ui/**").permitAll()
//                .requestMatchers("/api/v1/auth/login","/api/v1/auth/register").permitAll()
//                .requestMatchers("/api/v1/user/profile/**").authenticated()
        )
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .oauth2Login(oauth2Login -> oauth2Login
            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                .userService(customOAuth2UserService)
            )
            .successHandler(oAuth2SuccessHandler)
            .failureHandler(oAuth2FailureHandler)
        )
        .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
        .addFilterBefore(jwtAuthenticationProcessingFilter(),
            CustomJsonUsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(bCryptPasswordEncoder);
    provider.setUserDetailsService(loginService);
    return new ProviderManager(provider);
  }

  @Bean
  public LoginSuccessHandler loginSuccessHandler() {
    return new LoginSuccessHandler(tokenService, userRepository);
  }

  @Bean
  public LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler();
  }

  @Bean
  public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
    CustomJsonUsernamePasswordAuthenticationFilter filter = new CustomJsonUsernamePasswordAuthenticationFilter(
        objectMapper);
    filter.setAuthenticationManager(authenticationManager());
    filter.setFilterProcessesUrl("/api/v1/auth/login");
    filter.setAuthenticationSuccessHandler(loginSuccessHandler());
    filter.setAuthenticationFailureHandler(loginFailureHandler());
    return filter;
  }

  @Bean
  public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
    return new JwtAuthenticationProcessingFilter(
        tokenService, userRepository);
  }
}
