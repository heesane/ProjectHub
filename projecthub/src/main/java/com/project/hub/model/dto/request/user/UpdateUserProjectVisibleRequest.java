package com.project.hub.model.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateUserProjectVisibleRequest {

  private final Long userId;
  private final Long projectId;
}
