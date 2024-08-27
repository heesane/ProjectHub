package com.project.hub.model.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public class UserRegisterResponse {

  private final String email;
  private final String nickname;
}
