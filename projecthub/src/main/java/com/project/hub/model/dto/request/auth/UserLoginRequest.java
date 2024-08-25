package com.project.hub.model.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserLoginRequest {

  @Email
  private final String email;

  @NotBlank
  @Size(min = 6, max = 20)
  private final String password;
}
