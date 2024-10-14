package com.project.hub.model.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserProfileRequest {

  private final Long userId;
  private final String nickname;
}
