package com.project.hub.service;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.model.dto.request.auth.UserLoginRequest;
import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.ResultResponse;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {

  UserRegisterResponse register(UserRegisterRequest userRegisterDto);

  JwtToken login(UserLoginRequest userLoginDto);

  ResponseEntity<ResultResponse> oauth2Login(String code, String state);
}
