package com.project.hub.service;

import com.project.hub.model.dto.request.user.UpdateUserProfileRequest;
import com.project.hub.model.dto.request.user.UpdateUserProjectVisibleRequest;
import com.project.hub.model.dto.response.user.Profile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

  Profile myProfile(HttpServletRequest request, HttpServletResponse response, Long userId);

  String changeNickname(HttpServletRequest request,
      UpdateUserProfileRequest updateUserProfileRequest);

  String changeVisible(HttpServletRequest request,
      UpdateUserProjectVisibleRequest updateUserProjectVisibleRequest);
}
