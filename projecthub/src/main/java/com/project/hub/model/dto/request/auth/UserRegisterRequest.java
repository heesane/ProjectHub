package com.project.hub.model.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserRegisterRequest {

  @Email
  private final String email;

  @Size(min = 6, max = 20)
  private final String password;

  @Size(min = 2, max = 10)
  private final String nickname;
}
