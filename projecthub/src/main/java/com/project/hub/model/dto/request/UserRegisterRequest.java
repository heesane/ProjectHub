package com.project.hub.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserRegisterRequest {

  private final String email;
  private final String password;
  private final String nickname;
}
