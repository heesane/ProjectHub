package com.project.hub.service;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.entity.User;
import com.project.hub.model.dto.request.auth.UserLoginRequest;
import com.project.hub.model.dto.request.auth.UserRegisterRequest;
import com.project.hub.model.dto.response.auth.UserRegisterResponse;

public interface AuthService {

  UserRegisterResponse register(UserRegisterRequest userRegisterDto);

  JwtToken login(UserLoginRequest userLoginDto);

  User getUser(Long userId);
}
