package com.project.hub.service;

import com.project.hub.model.dto.request.user.UpdateUserProfileRequest;
import com.project.hub.model.dto.response.user.Profile;

public interface UserService {
  Profile myProfile(Long userId);
  String changeNickname(UpdateUserProfileRequest request);
}
