package com.project.hub.service;

import com.project.hub.auth.jwt.dto.JwtToken;
import com.project.hub.entity.User;
import com.project.hub.model.dto.request.UserLoginRequest;
import com.project.hub.model.dto.request.UserRegisterRequest;
import com.project.hub.model.dto.response.UserRegisterResponse;

public interface AuthService {

  UserRegisterResponse register(UserRegisterRequest userRegisterDto);

  JwtToken login(UserLoginRequest userLoginDto);

  User getUser(Long userId);
}
